package com.irum.come2us.domain.deliverypolicy.presentation.dto.request;

import jakarta.validation.constraints.Min;

public record DeliveryPolicyCreateRequest(
        @Min(value = 0, message = "기본 배송비는 필수 입력값입니다.") int defaultDeliveryFee,
        @Min(value = 1, message = "최소 주문 수량을 입력해주세요.") int minQuantity,
        @Min(value = 0, message = "최소 주문 금액을 입력해수세요.") int minAmount) {}
