package com.irum.come2us.domain.discount.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record DiscountRegisterRequest(
        @NotBlank(message = "상품 할인명은 필수 입력값입니다.") String name,
        @NotNull(message = "할인 금액은 필수 입력값입니다.") int amount,
        @NotNull(message = "상품 아이디는 필수 입력값입니다.") UUID productId) {}
