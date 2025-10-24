package com.irum.come2us.domain.order.presentation.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CustomerOrderResponse(
        List<ProductSummary> prodcutList,
        UUID orderId,
        AddressResponse address,
        int totalProductPrice,
        int totalPaymentAmount,
        int totalDiscountAmount) {
    @Builder
    public record ProductSummary(
            UUID orderDetailId, String productName, String optionName, int price, int quantity) {}
}
