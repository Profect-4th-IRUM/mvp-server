package com.irum.come2us.domain.product.domain.repository;

import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;

public interface ProductRepositoryCustom {
    List<ProductResponse> findProductsByCursor(UUID cursor, int size);

    List<ProductResponse> findProductsByKeyword(UUID cursor, int size, String keyword);

    List<ProductResponse> findProductsByStoreWithCursor(UUID storeId, UUID cursor, int size);

    List<ProductResponse> findProductsByCategoryIds(UUID cursor, int size, List<UUID> categoryIds);

    List<ProductResponse> findProductsByCategoryIdsAndKeyword(
            UUID cursor, int size, List<UUID> categoryIds, String keyword);
}
