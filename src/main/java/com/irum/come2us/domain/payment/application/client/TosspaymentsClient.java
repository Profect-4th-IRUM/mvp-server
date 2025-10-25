package com.irum.come2us.domain.payment.application.client;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.stereotype.Component;

import com.irum.come2us.domain.payment.application.client.dto.TossPaymentsRequest;
import com.irum.come2us.domain.payment.application.client.dto.TossPaymentsResponse;
import com.irum.come2us.domain.payment.presentation.dto.request.PaymentRequest;
import com.irum.come2us.global.infrastructure.properties.TossProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class TosspaymentsClient {
	private final TossProperties tossProperties;
	private final TosspaymentsAPI tosspaymentsAPI;

	public TossPaymentsResponse confirmPayment(PaymentRequest request, int paymentAmount){
		Base64.Encoder encoder = Base64.getEncoder();
		byte[] encodedBytes =
			encoder.encode((tossProperties.secretKey() + ":").getBytes(StandardCharsets.UTF_8));
		String authorizations = "Basic " + new String(encodedBytes);

		// request 제작
		TossPaymentsRequest tossPaymentsRequest =
			new TossPaymentsRequest(
				request.tossPaymentKey(), request.tossOrderId(), paymentAmount);

		return tosspaymentsAPI.confirmPayment(authorizations, tossPaymentsRequest);
	}
}
