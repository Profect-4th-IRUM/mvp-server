package com.irum.come2us.domain.review.presentation.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;

public record ReviewCreateRequest(
        @NotNull(message = "상품 ID는 필수 입력값입니다.") UUID productId,
        @NotBlank(message = "리뷰 내용은 필수 입력값입니다.") String content,
        @NotNull(message = "평점은 필수 입력값입니다.")
                @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
                @Max(value = 5, message = "평점은 5 이하여야 합니다.")
                Short rate,
        @Size(max = 5, message = "리뷰 이미지는 최대 5장까지 등록할 수 있습니다.")
                List<@NotBlank(message = "이미지 주소가 비어 있으면 안 됩니다.") String> imageUrls) {}
