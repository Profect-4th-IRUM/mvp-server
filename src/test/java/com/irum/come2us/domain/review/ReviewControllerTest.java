package com.irum.come2us.domain.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.review.application.ReviewService;
import com.irum.come2us.domain.review.presentation.controller.ReviewController;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewCreateRequest;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewUpdateRequest;
import com.irum.come2us.domain.review.presentation.dto.response.ReviewResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class ReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ReviewService reviewService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ReviewService reviewService() {
            return Mockito.mock(ReviewService.class);
        }
    }

    @Test
    @DisplayName("리뷰 작성 API 문서 생성")
    void createReviewApiDocs() throws Exception {
        UUID productId = UUID.randomUUID();
        ReviewCreateRequest request =
                new ReviewCreateRequest(
                        "좋은 상품입니다.", 5, productId, List.of("https://example.com/review1.jpg"));

        String requestJson = objectMapper.writeValueAsString(request);

        ReviewResponse mockResponse =
                new ReviewResponse(
                        UUID.randomUUID(),
                        productId,
                        1L,
                        "좋은 상품입니다.",
                        (short) 5,
                        List.of("https://example.com/review1.jpg"),
                        LocalDateTime.now(),
                        LocalDateTime.now());

        when(reviewService.createReview(any(Long.class), any(ReviewCreateRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(
                        post("/reviews")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "review-create",
                                requestFields(
                                        fieldWithPath("content")
                                                .description("리뷰 내용 (optional)")
                                                .optional(),
                                        fieldWithPath("rate").description("평점 (1~5)"),
                                        fieldWithPath("productId").description("상품 ID"),
                                        fieldWithPath("imageUrls").description("리뷰 이미지 URL 목록")),
                                responseFields(
                                        fieldWithPath("success").description("요청 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 생성 시각"),
                                        subsectionWithPath("data").description("응답 데이터 본문"),
                                        fieldWithPath("data.reviewId").description("리뷰 ID"),
                                        fieldWithPath("data.productId").description("상품 ID"),
                                        fieldWithPath("data.memberId").description("회원 ID"),
                                        fieldWithPath("data.content").description("리뷰 내용"),
                                        fieldWithPath("data.rate").description("평점"),
                                        fieldWithPath("data.imageUrls")
                                                .description("리뷰 이미지 URL 목록"),
                                        fieldWithPath("data.createdAt").description("리뷰 생성 시각"),
                                        fieldWithPath("data.updatedAt").description("리뷰 수정 시각"))));
    }

    @Test
    @DisplayName("리뷰 수정 API 문서 생성")
    void updateReviewApiDocs() throws Exception {
        UUID reviewId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        ReviewUpdateRequest request = new ReviewUpdateRequest("내용 수정", 4, List.of());
        String requestJson = objectMapper.writeValueAsString(request);

        ReviewResponse mockResponse =
                new ReviewResponse(
                        reviewId,
                        productId,
                        1L,
                        "내용 수정",
                        (short) 4,
                        List.of(),
                        LocalDateTime.now(),
                        LocalDateTime.now());

        when(reviewService.updateReview(eq(reviewId), any(ReviewUpdateRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(
                        patch("/reviews/{reviewId}", reviewId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andDo(document("review-update"));
    }

    @Test
    @DisplayName("내 리뷰 목록 조회 API 문서 생성")
    void getMyReviewsApiDocs() throws Exception {
        ReviewResponse review1 =
                new ReviewResponse(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        1L,
                        "좋아요",
                        (short) 5,
                        List.of(),
                        LocalDateTime.now(),
                        LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        when(reviewService.getMyReviews(any(Long.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review1), pageable, 1));

        mockMvc.perform(get("/reviews/me").with(csrf()))
                .andExpect(status().isOk())
                .andDo(document("review-my-list"));
    }

    @Test
    @DisplayName("상품 리뷰 목록 조회 API 문서 생성")
    void getProductReviewsApiDocs() throws Exception {
        UUID productId = UUID.randomUUID();

        ReviewResponse review1 =
                new ReviewResponse(
                        UUID.randomUUID(),
                        productId,
                        1L,
                        "상품 좋아요",
                        (short) 5,
                        List.of(),
                        LocalDateTime.now(),
                        LocalDateTime.now());

        Pageable pageable = PageRequest.of(0, 10);
        when(reviewService.getProductReviews(eq(productId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review1), pageable, 1));

        mockMvc.perform(get("/reviews/products/{productId}", productId).with(csrf()))
                .andExpect(status().isOk())
                .andDo(document("review-product-list"));
    }

    @Test
    @DisplayName("리뷰 삭제 API 문서 생성")
    void deleteReviewApiDocs() throws Exception {
        UUID reviewId = UUID.randomUUID();

        doNothing().when(reviewService).deleteReview(eq(reviewId));

        mockMvc.perform(delete("/reviews/{reviewId}", reviewId).with(csrf()))
                .andExpect(status().isOk())
                .andDo(document("review-delete"));
    }
}
