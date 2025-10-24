package com.irum.come2us.domain.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OwnerOrderListResponse(
        List<OrderSummary> orderList, UUID nextCursor, boolean hasNext) {
    public record OrderSummary(
            UUID orderId,
            String recipientName,
            String recipientContact,
            String recipientAddress,
            @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime orderDate,
            int totalProductPrice,
            int discountAmount,
            int payingAmount,
            int deliveryFee,
            List<ProductSummary> productList) {}

    public record ProductSummary(
            UUID orderDetailId,
            String productName,
            int productCounts,
            int productPrice,
            String optionTitle) {}
}
