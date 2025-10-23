package com.irum.come2us.domain.product.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductImageCreateRequest(
        @NotBlank(message = "이미지 URL은 필수 입력값입니다.") String imageUrl,
        @NotNull(message = "대표 여부는 필수 입력값입니다.") Boolean isDefault) {}
