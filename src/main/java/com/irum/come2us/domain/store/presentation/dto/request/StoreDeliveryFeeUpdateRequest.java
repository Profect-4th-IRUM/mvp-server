package com.irum.come2us.domain.store.presentation.dto.request;

import jakarta.validation.constraints.Min;

public record StoreDeliveryFeeUpdateRequest(
        @Min(value = 0, message = "배달비는 0원 이상이어야 합니다.") int deliveryFee) {}
