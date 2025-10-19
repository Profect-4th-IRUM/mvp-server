package com.irum.come2us.domain.product.domain.repository;

import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import java.util.List;
import java.util.UUID;

public interface ProductRepositoryCustom {
    List<ProductResponse> findProductsByCursor(UUID cursor, int size);
}
