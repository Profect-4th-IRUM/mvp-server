package com.irum.come2us.domain.category.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public record CategoryCreateRequest(
        @NotBlank(message = "카테고리명은 필수 입력값입니다.") String name, UUID parentId // null이면 루트 카테고리
        ) {}
