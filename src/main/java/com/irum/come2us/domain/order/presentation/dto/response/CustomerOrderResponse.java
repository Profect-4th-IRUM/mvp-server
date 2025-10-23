package com.irum.come2us.domain.order.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record CustomerOrderResponse (
        List<ProductSummary> prodcutList,
        UUID orderId,
        String address,
        int totalPrice
        ){
    public record ProductSummary(
            UUID orderDetailId,
            String productName,
            String optionName,
            int price,
            int quantity
    ){}
}
