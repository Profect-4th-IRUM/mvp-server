package com.irum.come2us.domain.deliverypolicy.presentation.dto.request;

import jakarta.validation.constraints.Min;

public record DeliveryPolicyInfoUpdateRequest(
        @Min(value = 0, message = "기본 배송비는 필수 입력값입니다.") Integer defaultDeliveryFee,
        @Min(value = 1, message = "최소 주문 수량을 입력해주세요.") Integer minQuantity,
        @Min(value = 0, message = "최소 주문 금액을 입력해수세요.") Integer minAmount) {}
