package com.irum.come2us.domain.product.presentation.dto.response;

import java.util.List;

public record ProductCursorResponse(List<ProductResponse> products) {

    public static ProductCursorResponse of(List<ProductResponse> products) {
        return new ProductCursorResponse(products);
    }
}