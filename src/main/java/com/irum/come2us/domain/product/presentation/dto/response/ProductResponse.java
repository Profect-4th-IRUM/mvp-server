package com.irum.come2us.domain.product.presentation.dto.response;

import com.irum.come2us.domain.product.domain.entity.Product;
import java.util.UUID;

/**
 * ProductResponse - 상품 등록/수정/조회 공용 DTO - 추후 ProductListResponse를 분리할지 고민
 *
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
        Integer reviewCount) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getDetailDescription(),
                product.getPrice(),
                product.isPublic(),
                product.getAvgRating(),
                product.getReviewCount());
    }
}
