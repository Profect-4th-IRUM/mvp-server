package com.irum.come2us.domain.review.presentation.controller;

import com.irum.come2us.domain.review.application.ReviewService;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewCreateRequest;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewUpdateRequest;
import com.irum.come2us.domain.review.presentation.dto.response.ReviewResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewResponse createReview(@Valid @RequestBody ReviewCreateRequest request) {
        return reviewService.createReview(request);
    }

    @PatchMapping("/{reviewId}")
    public ReviewResponse updateReview(
            @PathVariable UUID reviewId, @Valid @RequestBody ReviewUpdateRequest request) {
        return reviewService.updateReview(reviewId, request);
    }

    @GetMapping("/me")
    public Page<ReviewResponse> getMyReviews(Pageable pageable) {
        return reviewService.getMyReviews(pageable);
    }

    @GetMapping("/products/{productId}")
    public Page<ReviewResponse> getProductReviews(@PathVariable UUID productId, Pageable pageable) {
        return reviewService.getProductReviews(productId, pageable);
    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId);
    }
}
