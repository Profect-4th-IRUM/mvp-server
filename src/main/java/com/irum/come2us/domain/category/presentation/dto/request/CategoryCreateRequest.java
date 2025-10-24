package com.irum.come2us.domain.category.presentation.dto.request;

import java.util.UUID;

public record CategoryCreateRequest(String name, UUID parentId // null이면 루트 카테고리
        ) {}
