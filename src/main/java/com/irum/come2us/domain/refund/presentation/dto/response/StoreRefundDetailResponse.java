package com.irum.come2us.domain.refund.presentation.dto.response;

import com.irum.come2us.domain.deliveryaddress.domain.entity.Address;
import java.util.List;
import java.util.UUID;

public record StoreRefundDetailResponse(
        List<ProductDetailDto> productList,
        String recipientName,
        String recipientContact,
        Address recipientAddress,
        String orderDate,
        String cancelDate,
        String cancelNumber,
        String cancelStatus,
        Integer totalProductPrice,
        String couponName,
        Integer deliveryFee,
        Integer discountAmount,
        Integer payingAmount,
        Integer refundPrice) {
    public record ProductDetailDto(
            UUID orderDetailId,
            String productName,
            Integer productPrice,
            Integer productCounts,
            String productOption) {}
}
