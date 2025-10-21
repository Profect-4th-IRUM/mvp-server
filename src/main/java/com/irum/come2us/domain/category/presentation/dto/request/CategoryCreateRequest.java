package com.irum.come2us.domain.category.presentation.dto.request;

import java.util.UUID;
import lombok.Getter;

@Getter
public class CategoryCreateRequest {
    private String name;
    private UUID parentId; // null이면 루트 카테고리
}
