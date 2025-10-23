package com.irum.come2us.domain.product.presentation.dto.response;

import com.irum.come2us.domain.product.domain.entity.ProductImage;
import java.util.UUID;

public record ProductImageResponse(UUID id, String imageUrl, boolean isDefault) {
    public static ProductImageResponse from(ProductImage entity) {
        return new ProductImageResponse(entity.getId(), entity.getImageUrl(), entity.isDefault());
    }
}
