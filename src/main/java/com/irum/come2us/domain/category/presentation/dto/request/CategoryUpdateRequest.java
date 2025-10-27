package com.irum.come2us.domain.category.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryUpdateRequest(@NotBlank(message = "카테고리명은 필수 입력값입니다.") String name) {}
