package com.irum.come2us.domain.review.presentation.dto.request;

import jakarta.validation.constraints.*;
import java.util.List;

public record ReviewUpdateRequest(
        @Size(max = 500, message = "리뷰 내용은 최대 500자입니다.") String content,
        @Min(value = 1, message = "평점은 1 이상이어야 합니다.") @Max(value = 5, message = "평점은 5 이하여야 합니다.")
                Short rate,
        @Size(max = 5, message = "리뷰 이미지는 최대 5장까지 등록할 수 있습니다.")
                List<@NotBlank(message = "이미지 주소가 비어 있으면 안 됩니다.") String> imageUrls) {}
