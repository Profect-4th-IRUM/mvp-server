package com.irum.come2us.domain.payment.presentation.dto.request;

import java.util.UUID;

public record PaymentRequest (
	String tossOrderId,
	String tossPaymentKey,
	UUID orderId
){
}
