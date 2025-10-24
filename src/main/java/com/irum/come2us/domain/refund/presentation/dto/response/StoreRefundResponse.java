package com.irum.come2us.domain.refund.presentation.dto.response;

import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundReason;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import java.util.UUID;

public record StoreRefundResponse(
        UUID refundId,
        UUID orderId,
        int amount,
        RefundReason refundReason,
        String description,
        RefundStatus status) {
    public static StoreRefundResponse from(Refund refund) {
        return new StoreRefundResponse(
                refund.getRefundId(),
                refund.getOrder().getOrderId(),
                refund.getPrice(),
                refund.getReason(),
                refund.getDescription(),
                refund.getRefundStatus());
    }
}
