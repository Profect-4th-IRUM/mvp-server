package com.irum.come2us.domain.payment.application.client.dto;

public record TossPaymentsRequest(
	String paymentKey,
	String orderId,
	int amount
) {


}
