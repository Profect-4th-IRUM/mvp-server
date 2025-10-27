package com.irum.come2us.domain.order.presentation.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;
import java.util.UUID;

public record CustomerOrderRequest(
        List<ProductSummary> productList,
        UUID deliveryAddressId,
        String deliveryRequest,
        List<UUID> couponIdList,
        UUID storeId) {
    public record ProductSummary(UUID productId, UUID optionValueId, @PositiveOrZero int quantity) {}
}
