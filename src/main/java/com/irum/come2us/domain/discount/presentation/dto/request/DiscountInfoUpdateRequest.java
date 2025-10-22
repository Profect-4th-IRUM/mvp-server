package com.irum.come2us.domain.discount.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DiscountInfoUpdateRequest(
        @NotBlank(message = "상품 할인명은 필수 입력값입니다.") String name,
        @NotNull(message = "할인 금액은 필수 입력값입니다.") int amount) {}
