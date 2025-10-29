package com.irum.come2us.domain.cart.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CartCreateRequest(
        @NotNull(message = "옵션 값 ID는 필수 입력값입니다.") UUID optionValueId,
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.") int quantity) {}
