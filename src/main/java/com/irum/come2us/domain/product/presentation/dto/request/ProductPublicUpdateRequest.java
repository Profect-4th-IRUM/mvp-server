package com.irum.come2us.domain.product.presentation.dto.request;

import jakarta.validation.constraints.NotNull;

public record ProductPublicUpdateRequest(
        @NotNull(message = "상품 공개 여부는 필수 입력값입니다.") Boolean isPublic) {}
