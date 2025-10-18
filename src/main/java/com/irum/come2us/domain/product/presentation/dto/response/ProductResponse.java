package com.irum.come2us.domain.product.presentation.dto.response;

import com.irum.come2us.domain.product.domain.entity.Product;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String description,
        String detailDescription,
        int price,
        boolean isPublic) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getDetailDescription(),
                product.getPrice(),
                product.isPublic());
    }
}
