package com.irum.come2us.domain.order.application.service;

import com.irum.come2us.domain.coupon.application.service.AppliedCouponService;
import com.irum.come2us.domain.coupon.application.service.CouponService;
import com.irum.come2us.domain.deliveryaddress.domain.entity.DeliveryAddress;
import com.irum.come2us.domain.deliveryaddress.domain.repository.DeliveryAddressRepository;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.application.mapper.CustomerOrderMapper;
import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.order.infrastructure.repository.dto.CustomerOrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.CustomerOrderSummaryRow;
import com.irum.come2us.domain.order.presentation.dto.response.CustomerOrderListResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailStatusResponse;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.repository.RefundRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.time.LocalDate;
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
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomerOrderService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;
    private final MemberUtil memberUtil;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final CouponService couponService;
    private final AppliedCouponService appliedCouponService;
    private final PaymentService paymentService;

    @Transactional(readOnly = true)
    public OrderDetailStatusResponse getOrderDetailStatus(UUID orderDetailId) {
        Member member = memberUtil.getCurrentMember();

        OrderDetail orderDetail =
                orderDetailRepository
                        .findByOrderDetailIdWithOrderAndMember(orderDetailId)
                        .orElseThrow(
                                () -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        if (member.equals(orderDetail.getOrder().getMember()))
            throw new CommonException(OrderErrorCode.ORDER_FORBIDDEN);

        return OrderDetailStatusResponse.from(orderDetail);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(UUID orderId) {
        Member member = memberUtil.getCurrentMember();

        // order 조회 및 member 검증
        Order order =
                orderRepository
                        .findByOrderIdAndMember(orderId, member)
                        .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));

        List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);

        Refund refund = refundRepository.findByOrder(order).orElse(null);

        return CustomerOrderMapper.toOrderDetailResponse(order, orderDetailList, refund);
    }

    @Transactional
    public CustomerOrderListResponse getOrderList(
            UUID cursor, int size, LocalDate startDate, LocalDate endDate) {

        Member member = memberUtil.getCurrentMember();
        log.info("member {}", member.getMemberId());

        // 2. order list 검색
        List<CustomerOrderSummaryRow> headerList =
                orderRepository.fetchOrderListByMember(member, startDate, endDate, cursor, size);
        log.info("order list {}", headerList);

        boolean hasNext = headerList.size() > size;
        if (hasNext) {
            headerList = headerList.subList(0, size);
        }
        log.info("hasNext {}", hasNext);

        // 3. order id list
        List<UUID> orderIdList = headerList.stream().map(CustomerOrderSummaryRow::orderId).toList();
        List<CustomerOrderDetailRow> orderDetailList =
                orderRepository.fetchOrderDetailListByMember(orderIdList);
        log.info("orderDetailList {}", orderDetailList);

        // 4. orderId로 그룹핑 : productSummary 제작
        Map<UUID, List<CustomerOrderListResponse.ProductResponse>> detailMap =
                orderDetailList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        CustomerOrderDetailRow::orderId,
                                        Collectors.mapping(
                                                CustomerOrderMapper::toProductResponse,
                                                Collectors.toList())));

        // 5. orderResponse 제작
        List<CustomerOrderListResponse.OrderResponse> orderResponseList =
                headerList.stream()
                        .filter(order -> detailMap.containsKey(order.orderId()))
                        .map(
                                order ->
                                        CustomerOrderMapper.toOrderResponse(
                                                order,
                                                detailMap.getOrDefault(
                                                        order.orderId(),
                                                        List.of()) // order detail 없다면 빈 리스트
                                        ))
                        .toList();

        // 6. next cursor계산
        UUID nextCursor = orderResponseList.isEmpty() ? null : headerList.getLast().orderId();

        return CustomerOrderListResponse.builder()
                .orderList(orderResponseList)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }


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
            // 재고보다 요청 물품 개수가 많을때
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
