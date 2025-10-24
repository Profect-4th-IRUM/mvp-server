package com.irum.come2us.domain.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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

        when(reviewService.createReview(any(Long.class), any(ReviewCreateRequest.class)))
                .thenReturn(
                        new ReviewResponse(
                                UUID.randomUUID(),
                                productId,
                                1L,
                                "좋은 상품입니다.",
                                (short) 5,
                                List.of("https://example.com/review1.jpg")));

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
                                        fieldWithPath("content").description("리뷰 내용"),
                                        fieldWithPath("rate").description("평점 (1~5)"),
                                        fieldWithPath("productId").description("상품 ID"),
                                        fieldWithPath("imageUrls").description("리뷰 이미지 URL 목록"))));
    }

    @Test
    @DisplayName("리뷰 수정 API 문서 생성")
    void updateReviewApiDocs() throws Exception {
        UUID reviewId = UUID.randomUUID();
        ReviewUpdateRequest request = new ReviewUpdateRequest("내용 수정", 4, List.of());
        String requestJson = objectMapper.writeValueAsString(request);

        when(reviewService.updateReview(eq(reviewId), any(ReviewUpdateRequest.class)))
                .thenReturn(
                        new ReviewResponse(
                                reviewId, UUID.randomUUID(), 1L, "내용 수정", (short) 4, List.of()));

        mockMvc.perform(
                        patch("/reviews/{reviewId}", reviewId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "review-update",
                                requestFields(
                                        fieldWithPath("content").description("리뷰 내용"),
                                        fieldWithPath("rate").description("평점 (1~5)"),
                                        fieldWithPath("imageUrls").description("리뷰 이미지 URL 목록"))));
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
