package com.irum.come2us.domain.review.presentation.controller;

import com.irum.come2us.domain.review.application.service.ReviewService;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewCreateRequest;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewUpdateRequest;
import com.irum.come2us.domain.review.presentation.dto.response.ReviewListResponse;
import com.irum.come2us.domain.review.presentation.dto.response.ReviewResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ReviewController {

    private final ReviewService reviewService;

    /** 리뷰 생성 */
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody @Validated ReviewCreateRequest request) {
        log.info("리뷰 생성 요청: productId={}, rate={}", request.productId(), request.rate());
        ReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /** 리뷰 수정 */
    @PatchMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable UUID reviewId, @RequestBody @Validated ReviewUpdateRequest request) {
        log.info("리뷰 수정 요청: reviewId={}", reviewId);
        ReviewResponse response = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(response);
    }

    /** 내 리뷰 목록 조회 */
    @GetMapping("/me")
    public ResponseEntity<ReviewListResponse<ReviewResponse>> getMyReviews(Pageable pageable) {
        return ResponseEntity.ok(ReviewListResponse.from(reviewService.getMyReviews(pageable)));
    }

    /** 상품별 리뷰 목록 조회 */
    @GetMapping("/products/{productId}")
    public ResponseEntity<ReviewListResponse<ReviewResponse>> getProductReviews(
            @PathVariable UUID productId, Pageable pageable) {
        return ResponseEntity.ok(
                ReviewListResponse.from(reviewService.getProductReviews(productId, pageable)));
    }

    /** 리뷰 삭제 */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID reviewId) {
        log.info("리뷰 삭제 요청: reviewId={}", reviewId);
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
