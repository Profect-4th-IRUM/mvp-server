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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @Valid @RequestBody ReviewCreateRequest request) {
        // TODO: Security 적용 후 memberId 추출
        ReviewResponse response = reviewService.createReview(1L, request); // 임시 memberId
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable UUID reviewId, @Valid @RequestBody ReviewUpdateRequest request) {
        ReviewResponse response = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<Page<ReviewResponse>> getMyReviews(Pageable pageable) {
        Page<ReviewResponse> response = reviewService.getMyReviews(1L, pageable); // 임시 memberId
        return ResponseEntity.ok(response);
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<Page<ReviewResponse>> getProductReviews(
            @PathVariable UUID productId, Pageable pageable) {
        Page<ReviewResponse> response = reviewService.getProductReviews(productId, pageable);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
