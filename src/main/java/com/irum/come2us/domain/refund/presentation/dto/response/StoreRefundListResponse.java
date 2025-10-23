package com.irum.come2us.domain.refund.presentation.dto.response;

import java.util.List;

public record StoreRefundListResponse(
        List<RefundOrderList> refundOrders,
        String totalRefundPrice,
        Boolean hasNext,
        String nextCursor) {}
