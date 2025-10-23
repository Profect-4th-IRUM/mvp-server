package com.irum.come2us.domain.review.application;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.review.domain.entity.Review;
import com.irum.come2us.domain.review.domain.entity.ReviewImage;
import com.irum.come2us.domain.review.domain.repository.ReviewImageRepository;
import com.irum.come2us.domain.review.domain.repository.ReviewRepository;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewCreateRequest;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewUpdateRequest;
import com.irum.come2us.domain.review.presentation.dto.response.ReviewResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ReviewErrorCode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public ReviewResponse createReview(Long memberId, ReviewCreateRequest request) {
        log.info(
                "리뷰 작성 요청: memberId={}, productId={}, rate={}",
                memberId,
                request.productId(),
                request.rate());

        Member member =
                memberRepository
                        .findById(memberId)
                        .orElseThrow(() -> new CommonException(MemberErrorCode.MEMBER_NOT_FOUND));

        Product product =
                productRepository
                        .findById(request.productId())
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        Review review = Review.createReview(request.content(), request.rate(), member, product);
        Review saved = reviewRepository.save(review);

        // 이미지 저장
        List<ReviewImage> images =
                (request.imageUrls() == null)
                        ? List.of()
                        : request.imageUrls().stream()
                                .map(url -> ReviewImage.create(url, saved))
                                .toList();

        reviewImageRepository.saveAll(images);

        return ReviewResponse.from(saved, images.stream().map(ReviewImage::getImageUrl).toList());
    }

    public ReviewResponse updateReview(UUID reviewId, ReviewUpdateRequest request) {
        Review review =
                reviewRepository
                        .findById(reviewId)
                        .orElseThrow(() -> new CommonException(ReviewErrorCode.REVIEW_NOT_FOUND));

        if (request.content() == null && request.rate() == null && request.imageUrls() == null) {
            throw new CommonException(ReviewErrorCode.REVIEW_NOT_MODIFIED);
        }

        review.updateReview(request.content(), request.rate());

        if (request.imageUrls() != null) {
            reviewImageRepository.deleteAll(reviewImageRepository.findAllByReview(review));
            List<ReviewImage> newImages =
                    request.imageUrls().stream()
                            .map(url -> ReviewImage.create(url, review))
                            .toList();
            reviewImageRepository.saveAll(newImages);
        }

        List<String> imageUrls =
                reviewImageRepository.findAllByReview(review).stream()
                        .map(ReviewImage::getImageUrl)
                        .toList();

        return ReviewResponse.from(review, imageUrls);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(Long memberId, Pageable pageable) {
        log.info("내 리뷰 목록 조회 요청: memberId={}", memberId);

        return reviewRepository
                .findAllByMember_MemberId(memberId, pageable)
                .map(
                        review -> {
                            List<String> urls =
                                    reviewImageRepository.findAllByReview(review).stream()
                                            .map(ReviewImage::getImageUrl)
                                            .toList();
                            return ReviewResponse.from(review, urls);
                        });
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getProductReviews(UUID productId, Pageable pageable) {
        log.info("상품 리뷰 목록 조회 요청: productId={}", productId);

        return reviewRepository
                .findAllByProduct_Id(productId, pageable)
                .map(
                        review -> {
                            List<String> urls =
                                    reviewImageRepository.findAllByReview(review).stream()
                                            .map(ReviewImage::getImageUrl)
                                            .toList();
                            return ReviewResponse.from(review, urls);
                        });
    }

    public void deleteReview(UUID reviewId) {
        Review review =
                reviewRepository
                        .findById(reviewId)
                        .orElseThrow(() -> new CommonException(ReviewErrorCode.REVIEW_NOT_FOUND));

        reviewImageRepository.deleteAll(reviewImageRepository.findAllByReview(review));
        reviewRepository.delete(review);

        log.info("리뷰 삭제 완료: reviewId={}", reviewId);
    }
}
