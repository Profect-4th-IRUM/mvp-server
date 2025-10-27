package com.irum.come2us.domain.product.presentation.dto.response;

import com.irum.come2us.domain.category.presentation.dto.response.CategoryInfoResponse;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.store.presentation.dto.response.StoreInfoResponse;
import java.util.List;
import java.util.UUID;

/**
 * 상품 상세 조회 DTO - 추후 Store, Category, Images, Options 확장 예정
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
public record ProductDetailResponse(
        UUID id,
        String name,
        String description,
        String detailDescription,
        int price,
        boolean isPublic,
        Double avgRating,
        Integer reviewCount,
        StoreInfoResponse store,
        CategoryInfoResponse category,
        // TODO: List<ImageResponse> images,
        List<ProductOptionGroupResponse> optionGroups) {
    public static ProductDetailResponse from(Product product) {
        return new ProductDetailResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getDetailDescription(),
                product.getPrice(),
                product.isPublic(),
                product.getAvgRating(),
                product.getReviewCount(),
                StoreInfoResponse.from(product.getStore()),
                CategoryInfoResponse.from(product.getCategory()),
                product.getOptionGroups().stream().map(ProductOptionGroupResponse::from).toList());
    }
}
