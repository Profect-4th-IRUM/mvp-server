package com.irum.come2us.domain.order.application.service;

import com.irum.come2us.domain.coupon.application.service.CouponService;
import com.irum.come2us.domain.deliveryaddress.domain.entity.DeliveryAddress;
import com.irum.come2us.domain.deliveryaddress.domain.repository.DeliveryAddressRepository;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.order.presentation.dto.request.CustomerOrderRequest;
import com.irum.come2us.domain.order.presentation.dto.response.CustomerOrderResponse;
import com.irum.come2us.domain.payment.application.service.PaymentService;
import com.irum.come2us.domain.payment.domain.entity.Payment;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private final PaymentService paymentService;

    public CustomerOrderResponse createOrder(
            CustomerOrderRequest request
    ){
        Member member = memberUtil.getCurrentMember();
        Store store = storeRepository.findById(request.storeId())
                .orElseThrow(() -> new CommonException(StoreErrorCode.STORE_NOT_FOUND));
        DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(request.deliveryAddressId())
                .orElseThrow(() -> new CommonException(DeliveryAddressErrorCode.DELIVERY_ADDRESS_NOT_FOUND));


        // 상품 확인, 재고확인, 상품 정보 조회 , 가격 계산, 주문 상세 엔티티 생성 준비
        int calculatedTotalPrice = 0;
        int productCount = 0;
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (CustomerOrderRequest.ProductSummary productReq:request.productList()){
            //상품이 해당 상점의 상품인지 확인
            Product product = productRepository.findById(productReq.productId())
                    .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));
            if (!product.getStore().getId().equals(store.getId())){
                throw new CommonException(OrderErrorCode.INVALID_ORDER);
            }

            // 재고 확인
            ProductOptionValue productOptionValue = productOptionValueRepository.findByIdWithLock(productReq.optionValueId())
                    .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_OPTION_VALUE_NOT_FOUND));
            if (productOptionValue.getStockQuantity() < productReq.quantity()){
                throw new CommonException(ProductErrorCode.PRODUCT_OUT_OF_STOCK);
            }


            // 제품 가격 계산
            int productPrice = (product.getPrice() + productOptionValue.getStockQuantity()) * productReq.quantity();
            calculatedTotalPrice += productPrice;
            productCount += productReq.quantity();

            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .price(productPrice)
                    .quantity(productReq.quantity())
                    .orderStatusIndi(OrderStatus.PREPARING)
                    .optionName(productOptionValue.getName())
                    .productName(product.getName())
                    .productOptionValue(productOptionValue)
                    .build();
            orderDetails.add(orderDetail);
        }


        /**배송비 적용**/
        int deliveryFee = store.getDeliveryFee();
        int deliveryMinAmount = store.getDeliveryPolicy().getMinAmount();
        int deliveryMinQuantity = store.getDeliveryPolicy().getMinQuantity();
        if( calculatedTotalPrice > deliveryMinAmount || productCount > deliveryMinQuantity ){
            deliveryFee = 0;
        }

        /** 할인 적용 */
        int discountAmount = couponService.applyCoupons(request.couponIdList());
        int finalPaymentAmount = calculatedTotalPrice - discountAmount;

        /**결재 생성 및 PG사 호출**/
        Payment payment = paymentService.createPayment(finalPaymentAmount);
        // (실제 PG사 API 호출 - 예: I'mport, Toss Payments)
        // paymentService.requestPaymentToPG(payment, ...);
        // (결제가 실패하면 여기서 예외가 발생하고, @Transactional에 의해 롤백됨)

        String orderId = "ORD-" + (int) ((Math.random() * 10000000));

        //주문 엔티티 생성
        Order order = Order.builder()
                .orderNum(orderId)
                .totalPrice(calculatedTotalPrice)
                .deliveryFee(deliveryFee)
                .deliveryRequest(request.deliveryRequest())
                .orderStatusAll(OrderStatus.PREPARING)
                .member(member)
                .store(store)
                .payment(payment)
                .deliveryAddress(deliveryAddress)
                .build();

        /**주문 상세 저장하고, 재고 차감**/
        for (OrderDetail orderDetail:orderDetails){
            orderDetail.initOrderDetail(order);
            orderDetailRepository.save(orderDetail);

            //재고 감소
            ProductOptionValue pov = orderDetail.getProductOptionValue();
            pov.decreaseStock(orderDetail.getQuantity());
        }


        /**사용한 쿠폰 처리, 장바구니 비우기? */

    }

}
