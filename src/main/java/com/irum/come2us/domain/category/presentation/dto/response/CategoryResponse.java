package com.irum.come2us.domain.category.presentation.dto.response;

import com.irum.come2us.domain.category.domain.entity.Category;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {

    private UUID categoryId;
    private String name;
    private int depth;
    private UUID parentId;

    public static CategoryResponse fromEntity(Category category) {
        return CategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .depth(category.getDepth())
                .parentId(
                        category.getParent() != null ? category.getParent().getCategoryId() : null)
                .build();
    }
}
