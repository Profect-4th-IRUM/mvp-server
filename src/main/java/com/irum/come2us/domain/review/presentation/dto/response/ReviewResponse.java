package com.irum.come2us.domain.review.presentation.dto.response;

import com.irum.come2us.domain.review.domain.entity.Review;
import java.util.List;
import java.util.UUID;

public record ReviewResponse(
        UUID reviewId,
        UUID productId,
        Long memberId,
        String content,
        Short rate,
        List<String> imageUrls) {
    public static ReviewResponse from(Review review, List<String> imageUrls) {
        return new ReviewResponse(
                review.getId(),
                review.getProduct().getId(),
                review.getMember().getMemberId(),
                review.getContent(),
                review.getRate(),
                imageUrls);
    }
}
