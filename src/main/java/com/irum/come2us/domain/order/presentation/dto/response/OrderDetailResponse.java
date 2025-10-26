package com.irum.come2us.domain.order.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record OrderDetailResponse(
        List<ProductSummary> productList,
        String recipientName,
        String recipientContact,
        String recipientAddress,
        String paymentStatus,
        String deliveryStatus,
        String deliveryRequest,
        String trackingNumber,
        String arrivedDate,
        String couponName,
        Integer deliveryFee,
        Integer discountAmount,
        int payingAmount,
        String orderDate,
        int totalProductPrice) {
    public record ProductSummary(
            UUID orderDetailId,
            String productName,
            int productPrice,
            int productCounts,
            String productOption) {}
}
