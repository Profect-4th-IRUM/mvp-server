package com.irum.come2us.domain.product.presentation.dto.response;

import com.irum.come2us.domain.product.domain.entity.ProductOptionGroup;
import java.util.List;
import java.util.UUID;

public record ProductOptionGroupResponse(
        UUID id, String name, List<ProductOptionValueResponse> optionValues) {
    public static ProductOptionGroupResponse from(ProductOptionGroup group) {
        return new ProductOptionGroupResponse(
                group.getId(),
                group.getName(),
                group.getOptionValues().stream().map(ProductOptionValueResponse::from).toList());
    }
}
