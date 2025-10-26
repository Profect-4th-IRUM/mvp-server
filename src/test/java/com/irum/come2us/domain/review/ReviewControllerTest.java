package com.irum.come2us.domain.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.review.application.ReviewService;
import com.irum.come2us.domain.review.presentation.controller.ReviewController;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewCreateRequest;
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
        // given
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

        when(reviewService.createReview(any(ReviewCreateRequest.class)))
                .thenReturn(mockResponse);

        // when & then
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
}
