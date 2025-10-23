package com.irum.come2us.domain.order.presentation.dto.request;

import com.irum.come2us.domain.order.presentation.dto.response.CustomerOrderResponse;

import java.util.List;
import java.util.UUID;

public record CustomerOrderRequest(
        List<ProductSummary> productList,
        String address,
        String phone,
        String name,
        String deliveryRequest,
        List<UUID> couponIdList
) {
    public record ProductSummary(
            UUID productId,
            UUID optionValueId,
            UUID quantity
    ) {}
}
