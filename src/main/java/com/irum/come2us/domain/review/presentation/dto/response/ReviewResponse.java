package com.irum.come2us.domain.review.presentation.dto.response;

import com.irum.come2us.domain.review.domain.entity.Review;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReviewResponse(
        UUID reviewId,
        UUID productId,
        Long memberId,
        String content,
        Short rate,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
    public static ReviewResponse from(Review review, List<String> imageUrls) {
        return new ReviewResponse(
                review.getId(),
                review.getProductId(),
                review.getMemberId(),
                review.getContent(),
                review.getRate(),
                imageUrls,
                review.getCreatedAt(),
                review.getUpdatedAt());
    }
}
