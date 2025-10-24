package com.irum.come2us.domain.ai.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AiRequest(
        @NotBlank(message = "프롬프트는 필수입니다.") @Size(max = 500, message = "프롬프트는 500자를 초과할 수 없습니다.")
                String prompt,
        @NotBlank(message = "상품 ID는 필수입니다.") String productId) {}
