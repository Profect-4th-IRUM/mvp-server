package com.irum.come2us.domain.product.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record ProductCreateRequest(
        @NotBlank(message = "상품명은 필수 입력값입니다.") String name,
        @NotBlank(message = "상품 설명은 필수 입력값입니다.") String description,
        @NotBlank(message = "상품 상세 설명은 필수 입력값입니다.") String detailDescription,
        @NotNull(message = "상품 공개 여부는 필수 입력값입니다.") Boolean isPublic,
        @Min(value = 0, message = "상품 가격은 0 이상이어야 합니다.") int price,
        @NotNull(message = "상품 카테고리는 필수 입력값입니다.") UUID categoryId,
        List<ProductOptionGroupRequest> optionGroups) {}
