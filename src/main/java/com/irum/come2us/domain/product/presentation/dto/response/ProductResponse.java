package com.irum.come2us.domain.product.presentation.dto.response;

import com.irum.come2us.domain.product.domain.entity.Product;
import java.util.UUID;

/**
 * @param id
 * @param name
 * @param description
 * @param detailDescription
 * @param price
 * @param isPublic
 * @param avgRating
 * @param reviewCount
 */
public record ProductResponse(
        UUID id,
        String name,
        String description,
        String detailDescription,
        int price,
        boolean isPublic,
        Double avgRating,
        Integer reviewCount,
        UUID categoryId,
        String categoryName) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getDetailDescription(),
                product.getPrice(),
                product.isPublic(),
                product.getAvgRating(),
                product.getReviewCount(),
                product.getCategory().getCategoryId(),
                product.getCategory().getName());
    }
}
