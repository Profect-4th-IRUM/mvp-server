package com.irum.come2us.domain.order.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

public record OwnerOrderListResponse(
        List<OrderSummary> orderList, String nextCursor, boolean hasNext) {
    public record OrderSummary(
            String orderId,
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
            String orderDetailId,
            String productName,
            int productCounts,
            int productPrice,
            String optionTitle) {}
}
