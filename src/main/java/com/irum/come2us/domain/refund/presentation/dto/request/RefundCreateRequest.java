package com.irum.come2us.domain.refund.presentation.dto.request;

import com.irum.come2us.domain.refund.domain.entity.enums.RefundReason;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record RefundCreateRequest(
        @NotNull(message = "환불 사유는 필수 입력값입니다.") RefundReason reason,
        @Nullable String description) {}
