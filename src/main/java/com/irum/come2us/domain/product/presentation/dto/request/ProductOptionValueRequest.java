package com.irum.come2us.domain.product.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductOptionValueRequest(
        @NotBlank(message = "옵션명은 필수 입력값입니다.") String name,
        @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다.") int stockQuantity,
        @NotNull(message = "추가 금액은 필수 입력값입니다.") Integer extraPrice) {}
