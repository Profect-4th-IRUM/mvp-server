package com.irum.come2us.domain.payment.application.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.irum.come2us.domain.coupon.application.service.AppliedCouponService;
import com.irum.come2us.domain.coupon.application.service.CouponService;
import com.irum.come2us.domain.coupon.domain.repository.AppliedCouponRepository;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentCorp;
import com.irum.come2us.domain.payment.domain.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.payment.application.client.TosspaymentsClient;
import com.irum.come2us.domain.payment.application.client.dto.TossPaymentsRequest;
import com.irum.come2us.domain.payment.application.client.dto.TossPaymentsResponse;
import com.irum.come2us.domain.payment.domain.entity.Payment;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.come2us.domain.payment.presentation.dto.request.PaymentRequest;
import com.irum.come2us.domain.payment.presentation.dto.response.PaymentResponse;
import com.irum.come2us.domain.product.application.service.ProductOptionValueService;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.PaymentErrorCode;

import feign.FeignException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final AppliedCouponService appliedCouponService;
    @Value("${payment.toss.secret-key}")
	String secreteKey;

	private final TosspaymentsClient tosspaymentsClient;
	private final OrderRepository orderRepository;
	private final ProductOptionValueService productOptionValueService;
    private final PaymentRepository paymentRepository;
    private final OrderDetailRepository orderDetailRepository;

    public Payment preparePayment(Member member, int finalPaymentAmount, int discountAmount, PaymentCorp paymentCorp) {
        Payment payment = Payment.builder()
                .member(member)
                .amount(finalPaymentAmount)
                .totalDiscountAmount(discountAmount)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentCorp(paymentCorp)
                .build();
        return paymentRepository.save(payment);
    }


	public PaymentResponse createPayment(PaymentRequest request){
		Order order = orderRepository.findById(request.orderId())
			.orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));
        List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);
		Payment payment = order.getPayment();

//		 Payment payment = Payment.builder()
//		 	.amount(50000)
//		 	.paymentStatus(PaymentStatus.PENDING)
//		 	.build();
//		 Order order = Order.builder()
//		 	.orderId(UUID.randomUUID())
//		 	.payment(payment)
//		 	.orderNum("ddddddddddd")
//		 	.build();

		// 이미 처리된 결제인지 확인
		if (!payment.getPaymentStatus().equals(PaymentStatus.PENDING)){
			return new PaymentResponse(order.getOrderNum(), payment.getAmount());
		}

		// 토스페이먼츠 authorization 제작
		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes = encoder.encode((secreteKey + ":").getBytes(StandardCharsets.UTF_8));
		String authorizations = "Basic " + new String(encodedBytes);

		// 토스 페이먼츠 승인 API호출
		TossPaymentsRequest tossPaymentsRequest = new TossPaymentsRequest(request.tossPaymentKey(), request.tossOrderId(), payment.getAmount());
		try {
			TossPaymentsResponse tossPaymentsResponse = tosspaymentsClient.confirmPayment(authorizations, tossPaymentsRequest);

            //상태 업데이트
            payment.updateToPaid(PaymentStatus.PAID, tossPaymentsResponse);
            order.updateOrderStatus(OrderStatus.PREPARING);
            orderDetailList.forEach(orderDetail -> {orderDetail.updateStatus(OrderStatus.PREPARING);});

			return new PaymentResponse(order.getOrderNum(), payment.getAmount());
		} catch (FeignException e){
			//추후에 Feign client error decoder로 변환하면 좋을 듯

            //상태 업데이트
			payment.updateStatus(PaymentStatus.FAILED);
			order.updateOrderStatus(OrderStatus.FAILED);
            orderDetailList.forEach(orderDetail -> {orderDetail.updateStatus(OrderStatus.FAILED);});

            //재고 롤백
			productOptionValueService.rollbackStockForOrder(order);
            //쿠폰 롤백
            appliedCouponService.rollbackAppliedCouponList(payment);

			throw new CommonException(PaymentErrorCode.PAYMENT_ERROR);
		}

	}



}
