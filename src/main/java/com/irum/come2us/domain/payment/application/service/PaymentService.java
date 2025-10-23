package com.irum.come2us.domain.payment.application.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.payment.application.client.TosspaymentsClient;
import com.irum.come2us.domain.payment.application.client.dto.TossPaymentsRequest;
import com.irum.come2us.domain.payment.application.client.dto.TossPaymentsResponse;
import com.irum.come2us.domain.payment.domain.entity.Payment;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.come2us.domain.payment.presentation.dto.request.PaymentRequest;
import com.irum.come2us.domain.payment.presentation.dto.response.PaymentResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

	@Value("${payment.toss.secret-key}")
	String secreteKey;

	private final TosspaymentsClient tosspaymentsClient;
	private final OrderRepository orderRepository;

	public PaymentResponse createPayment(PaymentRequest request){
		Order order = orderRepository.findById(request.orderId())
			.orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));
		Payment payment = order.getPayment();

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
		TossPaymentsResponse tossPaymentsResponse = tosspaymentsClient.confirmPayment(authorizations, tossPaymentsRequest);
		//TODO: error처리
		// error났을때 재고 롤백하고 payment와 order상태를 failed로 변경

		payment.updateStatus(PaymentStatus.PAID);

		return new PaymentResponse(order.getOrderNum(), payment.getAmount());

	}


}
