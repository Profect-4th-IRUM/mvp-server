package com.irum.come2us.domain.payment.presentation.dto.response;

public record PaymentResponse(
	String orderNum,
	int totalAmount

) {
}
