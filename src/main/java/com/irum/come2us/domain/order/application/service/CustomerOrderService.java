package com.irum.come2us.domain.order.application.service;

import com.irum.come2us.domain.coupon.application.service.AppliedCouponService;
import com.irum.come2us.domain.coupon.application.service.CouponService;
import com.irum.come2us.domain.deliveryaddress.domain.entity.DeliveryAddress;
import com.irum.come2us.domain.deliveryaddress.domain.repository.DeliveryAddressRepository;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.application.mapper.CustomerOrderMapper;
import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.order.presentation.dto.request.CustomerOrderRequest;
import com.irum.come2us.domain.order.presentation.dto.response.CustomerOrderResponse;
import com.irum.come2us.domain.payment.application.service.PaymentService;
import com.irum.come2us.domain.payment.domain.entity.Payment;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentCorp;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.entity.ProductOptionValue;
import com.irum.come2us.domain.product.domain.repository.ProductOptionValueRepository;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.domain.store.domain.repository.StoreRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.DeliveryAddressErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.StoreErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CustomerOrderService {
    private final MemberUtil memberUtil;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CouponService couponService;
    private final AppliedCouponService appliedCouponService;
    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    public CustomerOrderResponse prepareOrder(CustomerOrderRequest request) {
        Member member = memberUtil.getCurrentMember();
        Store store =
                storeRepository
                        .findByIdWithDeliveryPolicy(request.storeId())
                        .orElseThrow(() -> new CommonException(StoreErrorCode.STORE_NOT_FOUND));
        DeliveryAddress deliveryAddress =
                deliveryAddressRepository
                        .findById(request.deliveryAddressId())
                        .orElseThrow(
                                () ->
                                        new CommonException(
                                                DeliveryAddressErrorCode
                                                        .DELIVERY_ADDRESS_NOT_FOUND));
        log.info("[주문준비] 멤버 {} {} , 상점, 주소 검색", member.getMemberId(), member.getName());

        // 요청에서 id목록 추출
        List<UUID> productIds =
                request.productList().stream()
                        .map(CustomerOrderRequest.ProductSummary::productId)
                        .distinct()
                        .toList();

        List<UUID> optionValueIds =
                request.productList().stream()
                        .map(CustomerOrderRequest.ProductSummary::optionValueId)
                        .distinct()
                        .toList();

        // 조회
        List<Product> products = productRepository.findAllById(productIds);
        List<ProductOptionValue> optionValues =
                productOptionValueRepository.findAllByIdInWithLock(optionValueIds);

        // Map으로 변환
        Map<UUID, Product> productMap =
                products.stream().collect(Collectors.toMap(Product::getId, product -> product));

        Map<UUID, ProductOptionValue> optionMap =
                optionValues.stream()
                        .collect(Collectors.toMap(ProductOptionValue::getId, option -> option));

        // 정합 정검
        if (productMap.size() != productIds.size()) {
            throw new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        if (optionMap.size() != optionValueIds.size()) {
            throw new CommonException(ProductErrorCode.PRODUCT_OPTION_VALUE_NOT_FOUND);
        }

        // 상품 확인, 재고확인, 상품 정보 조회 , 가격 계산, 주문 상세 엔티티 생성 준비
        int calculatedTotalPrice = 0;
        int productCount = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CustomerOrderRequest.ProductSummary productReq : request.productList()) {
            // 상품이 해당 상점의 상품인지 확인
            Product product = productMap.get(productReq.productId());

            if (!product.getStore().getId().equals(store.getId())) {
                throw new CommonException(OrderErrorCode.INVALID_ORDER);
            }

            // 재고 확인
            ProductOptionValue productOptionValue = optionMap.get(productReq.optionValueId());

            if (productOptionValue.getStockQuantity() < productReq.quantity()) {
                throw new CommonException(ProductErrorCode.PRODUCT_OUT_OF_STOCK);
            }

            // 제품 가격 계산
            int productPrice =
                    (product.getPrice() + productOptionValue.getExtraPrice())
                            * productReq.quantity();
            calculatedTotalPrice += productPrice;
            productCount += productReq.quantity();

            // 재고 미리 차감
            productOptionValue.decreaseStock(productReq.quantity());

            OrderDetail orderDetail =
                    OrderDetail.builder()
                            .product(product)
                            .price(productPrice)
                            .quantity(productReq.quantity())
                            .orderStatusIndi(OrderStatus.PENDING)
                            .optionName(productOptionValue.getName())
                            .productName(product.getName())
                            .productOptionValue(productOptionValue)
                            .build();
            orderDetails.add(orderDetail);
        }
        log.info("상품 확인, 재고 확인, 재고 차감, 가격 계산 완료");

        /** 배송비 적용* */
        int deliveryFee = store.getDeliveryPolicy().getDefaultDeliveryFee();
        int deliveryMinAmount = store.getDeliveryPolicy().getMinAmount();
        int deliveryMinQuantity = store.getDeliveryPolicy().getMinQuantity();
        if (calculatedTotalPrice > deliveryMinAmount || productCount > deliveryMinQuantity) {
            deliveryFee = 0;
        }
        log.info("배송비 {}", deliveryFee);

        /** 할인 적용 */
        int discountAmount =
                couponService.validAndCalCoupon(
                        request.couponIdList(), calculatedTotalPrice, member);
        int finalPaymentAmount = calculatedTotalPrice - discountAmount;
        log.info("할인 {}, 할인 후 가격 {}", discountAmount, finalPaymentAmount);

        /** 결재 생성 PENDING 상태* */
        Payment payment =
                paymentService.preparePayment(
                        member, finalPaymentAmount, discountAmount, PaymentCorp.TOSS);
        // 쿠폰 미리 차감
        appliedCouponService.createAppliedCouponList(payment, request.couponIdList());

        // 주문 엔티티 생성 PENDING 상태
        String orderNum = "ORD-" + (int) ((Math.random() * 10000000));

        Order order =
                Order.builder()
                        .orderNum(orderNum)
                        .totalPrice(calculatedTotalPrice)
                        .deliveryFee(deliveryFee)
                        .deliveryRequest(request.deliveryRequest())
                        .orderStatusAll(OrderStatus.PENDING)
                        .member(member)
                        .store(store)
                        .payment(payment)
                        .deliveryAddress(deliveryAddress)
                        .build();
        orderRepository.save(order);

        /** 주문 상세 저장* */
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.updateOrder(order);
            orderDetailRepository.save(orderDetail);
        }

        return CustomerOrderMapper.toCustomerOrderResponse(order, orderDetails);
    }
}
