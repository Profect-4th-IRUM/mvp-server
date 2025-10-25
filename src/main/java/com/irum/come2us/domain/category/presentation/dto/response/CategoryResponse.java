package com.irum.come2us.domain.category.presentation.dto.response;

import com.irum.come2us.domain.category.domain.entity.Category;
import java.util.List;
import java.util.UUID;

public record CategoryResponse(
        UUID categoryId,
        String name,
        int depth,
        UUID parentId,
        List<CategoryResponse> children // 트리 조회용
        ) {
    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getName(),
                category.getDepth(),
                category.getParent() != null ? category.getParent().getCategoryId() : null,
                null);
    }

    public static CategoryResponse fromEntityWithChildren(Category category) {
        return new CategoryResponse(
                category.getCategoryId(),
                category.getName(),
                category.getDepth(),
                category.getParent() != null ? category.getParent().getCategoryId() : null,
                category.getChildren().stream()
                        .map(CategoryResponse::fromEntityWithChildren)
                        .toList());
    }
}
