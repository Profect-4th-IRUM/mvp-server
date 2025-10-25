package com.irum.come2us.domain.refund.presentation.dto.request;

import com.irum.come2us.domain.refund.domain.entity.enums.RefundReason;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StoreRefundCreateRequest(
        @NotNull RefundReason refundReason, @NotBlank String description) {}
