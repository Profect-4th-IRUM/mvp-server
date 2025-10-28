package com.irum.come2us.domain.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.review.application.service.ReviewService;
import com.irum.come2us.domain.review.presentation.controller.ReviewController;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewCreateRequest;
import com.irum.come2us.domain.review.presentation.dto.request.ReviewUpdateRequest;
import com.irum.come2us.domain.review.presentation.dto.response.ReviewResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
import com.irum.come2us.global.config.TestConfig;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReviewController.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, TestConfig.class})
class ReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ReviewService reviewService;

    private final UUID mockReviewId = UUID.randomUUID();
    private final UUID mockProductId = UUID.randomUUID();

    @Test
    @DisplayName("리뷰 생성 API (고객 권한)")
    void createReviewApiTest() throws Exception {
        ReviewCreateRequest req =
                new ReviewCreateRequest(mockProductId, "좋은 상품이에요", (short)5, List.of("https://img.com/1.jpg"));
        ReviewResponse res =
                new ReviewResponse(mockReviewId, mockProductId, 1L, "좋은 상품이에요", (short)5, List.of("https://img.com/1.jpg"));
        when(reviewService.createReview(any())).thenReturn(res);

        mockMvc.perform(post("/reviews")
                        .with(csrf()).with(user("1").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andDo(document("review-create",
                        requestFields(
                                fieldWithPath("productId").description("리뷰 대상 상품 ID"),
                                fieldWithPath("content").description("리뷰 내용"),
                                fieldWithPath("rate").description("평점 (1~5)"),
                                fieldWithPath("imageUrls[]").description("이미지 URL 목록").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("status").description("HTTP 상태 코드"),
                                fieldWithPath("data.reviewId").description("리뷰 ID"),
                                fieldWithPath("data.productId").description("상품 ID"),
                                fieldWithPath("data.memberId").description("작성자 ID"),
                                fieldWithPath("data.content").description("리뷰 내용"),
                                fieldWithPath("data.rate").description("평점"),
                                fieldWithPath("data.imageUrls[]").description("이미지 URL 목록"),
                                fieldWithPath("timestamp").description("응답 시간")
                        )));
    }

    @Test
    @DisplayName("리뷰 수정 API (고객 권한)")
    void updateReviewApiTest() throws Exception {
        ReviewUpdateRequest req =
                new ReviewUpdateRequest("수정된 리뷰입니다", (short)4, List.of("https://img.com/2.jpg"));
        ReviewResponse res =
                new ReviewResponse(mockReviewId, mockProductId, 1L, "수정된 리뷰입니다", (short)4, List.of("https://img.com/2.jpg"));
        when(reviewService.updateReview(eq(mockReviewId), any())).thenReturn(res);

        mockMvc.perform(patch("/reviews/{reviewId}", mockReviewId)
                        .with(csrf()).with(user("1").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andDo(document("review-update",
                        pathParameters(parameterWithName("reviewId").description("수정할 리뷰 ID")),
                        requestFields(
                                fieldWithPath("content").description("수정할 내용").optional(),
                                fieldWithPath("rate").description("수정할 평점 (1~5)").optional(),
                                fieldWithPath("imageUrls[]").description("수정할 이미지 목록").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("status").description("HTTP 상태 코드"),
                                fieldWithPath("data.reviewId").description("리뷰 ID"),
                                fieldWithPath("data.productId").description("상품 ID"),
                                fieldWithPath("data.memberId").description("작성자 ID"),
                                fieldWithPath("data.content").description("리뷰 내용"),
                                fieldWithPath("data.rate").description("평점"),
                                fieldWithPath("data.imageUrls[]").description("이미지 URL 목록"),
                                fieldWithPath("timestamp").description("응답 시간")
                        )));
    }

    @Test
    @DisplayName("내 리뷰 목록 조회 API")
    void getMyReviewsApiTest() throws Exception {
        ReviewResponse r = new ReviewResponse(mockReviewId, mockProductId, 1L, "좋아요", (short)5, List.of());
        when(reviewService.getMyReviews(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(r)));

        mockMvc.perform(get("/reviews/me").with(csrf()).with(user("1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andDo(document("review-list-me",
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("status").description("HTTP 상태 코드"),
                                fieldWithPath("timestamp").description("응답 시간"),
                                fieldWithPath("data.content[].reviewId").description("리뷰 ID"),
                                fieldWithPath("data.content[].productId").description("상품 ID"),
                                fieldWithPath("data.content[].memberId").description("작성자 ID"),
                                fieldWithPath("data.content[].content").description("리뷰 내용"),
                                fieldWithPath("data.content[].rate").description("평점"),
                                fieldWithPath("data.content[].imageUrls[]").description("이미지 URL 목록"),
                                subsectionWithPath("data.pageable").ignored(),
                                fieldWithPath("data.totalPages").ignored(),
                                fieldWithPath("data.totalElements").ignored(),
                                fieldWithPath("data.last").ignored(),
                                fieldWithPath("data.size").ignored(),
                                fieldWithPath("data.number").ignored(),
                                subsectionWithPath("data.sort").ignored(),
                                fieldWithPath("data.first").ignored(),
                                fieldWithPath("data.numberOfElements").ignored(),
                                fieldWithPath("data.empty").ignored()
                        )));
    }

    @Test
    @DisplayName("상품 리뷰 목록 조회 API")
    void getProductReviewsApiTest() throws Exception {
        ReviewResponse r = new ReviewResponse(mockReviewId, mockProductId, 1L, "괜찮아요", (short)4, List.of());
        when(reviewService.getProductReviews(eq(mockProductId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(r)));

        mockMvc.perform(get("/reviews/products/{productId}", mockProductId)
                        .with(csrf()).with(user("1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andDo(document("review-list-product",
                        pathParameters(parameterWithName("productId").description("리뷰 조회할 상품 ID")),
                        responseFields(
                                fieldWithPath("success").description("요청 성공 여부"),
                                fieldWithPath("status").description("HTTP 상태 코드"),
                                fieldWithPath("timestamp").description("응답 시간"),
                                fieldWithPath("data.content[].reviewId").description("리뷰 ID"),
                                fieldWithPath("data.content[].productId").description("상품 ID"),
                                fieldWithPath("data.content[].memberId").description("작성자 ID"),
                                fieldWithPath("data.content[].content").description("리뷰 내용"),
                                fieldWithPath("data.content[].rate").description("평점"),
                                fieldWithPath("data.content[].imageUrls[]").description("이미지 URL 목록"),
                                subsectionWithPath("data.pageable").ignored(),
                                fieldWithPath("data.totalPages").ignored(),
                                fieldWithPath("data.totalElements").ignored(),
                                fieldWithPath("data.last").ignored(),
                                fieldWithPath("data.size").ignored(),
                                fieldWithPath("data.number").ignored(),
                                subsectionWithPath("data.sort").ignored(),
                                fieldWithPath("data.first").ignored(),
                                fieldWithPath("data.numberOfElements").ignored(),
                                fieldWithPath("data.empty").ignored()
                        )));
    }

    @Test
    @DisplayName("리뷰 삭제 API (고객 권한)")
    void deleteReviewApiTest() throws Exception {
        mockMvc.perform(delete("/reviews/{reviewId}", mockReviewId)
                        .with(csrf()).with(user("1").roles("CUSTOMER")))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(document("review-delete",
                        pathParameters(parameterWithName("reviewId").description("삭제할 리뷰 ID"))));
    }
}
