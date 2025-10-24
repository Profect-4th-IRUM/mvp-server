package com.irum.come2us.domain.discount.presentation.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record DiscountInfoUpdateRequest(
        @Nullable String name, @NotNull(message = "할인 금액은 필수 입력값입니다.") int amount) {}
