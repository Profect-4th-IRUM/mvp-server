package com.irum.come2us.domain.cart.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartUpdateRequest(
        @NotNull(message = "수정할 수량은 필수 입력값입니다.") @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
                int quantity) {}
