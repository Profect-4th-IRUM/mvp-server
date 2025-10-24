package com.irum.come2us.domain.product.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record ProductOptionGroupRequest(
        @NotBlank(message = "옵션 그룹명은 필수 입력값입니다.") String name,
        List<ProductOptionValueRequest> optionValues) {}
