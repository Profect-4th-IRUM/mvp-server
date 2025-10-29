package com.irum.come2us.domain.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
import com.irum.come2us.domain.review.presentation.dto.response.ReviewListResponse;
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

    /** 리뷰 생성 */
    @Test
    @DisplayName("리뷰 생성 API (고객 권한)")
    void createReviewApiTest() throws Exception {
        ReviewCreateRequest request =
                new ReviewCreateRequest(
                        mockProductId, "좋은 상품이에요", (short) 5, List.of("https://img.com/1.jpg"));
        String json = objectMapper.writeValueAsString(request);

        ReviewResponse mockResponse =
                new ReviewResponse(
                        mockReviewId,
                        mockProductId,
                        1L,
                        "좋은 상품이에요",
                        (short) 5,
                        List.of("https://img.com/1.jpg"));

        when(reviewService.createReview(any())).thenReturn(mockResponse);

        mockMvc.perform(
                        post("/reviews")
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.reviewId").value(mockReviewId.toString()))
                .andExpect(jsonPath("$.data.productId").value(mockProductId.toString()))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andDo(
                        document(
                                "review-create",
                                requestFields(
                                        fieldWithPath("productId").description("리뷰 대상 상품 ID"),
                                        fieldWithPath("content").description("리뷰 내용"),
                                        fieldWithPath("rate").description("평점 (1~5)"),
                                        fieldWithPath("imageUrls[]")
                                                .description("이미지 URL 목록")
                                                .optional())));
    }

    /** 리뷰 수정 */
    @Test
    @DisplayName("리뷰 수정 API (고객 권한)")
    void updateReviewApiTest() throws Exception {
        ReviewUpdateRequest request =
                new ReviewUpdateRequest("수정된 리뷰입니다", (short) 4, List.of("https://img.com/2.jpg"));
        String json = objectMapper.writeValueAsString(request);

        ReviewResponse mockResponse =
                new ReviewResponse(
                        mockReviewId,
                        mockProductId,
                        1L,
                        "수정된 리뷰입니다",
                        (short) 4,
                        List.of("https://img.com/2.jpg"));

        when(reviewService.updateReview(eq(mockReviewId), any())).thenReturn(mockResponse);

        mockMvc.perform(
                        patch("/reviews/{reviewId}", mockReviewId)
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reviewId").value(mockReviewId.toString()))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andDo(
                        document(
                                "review-update",
                                pathParameters(
                                        parameterWithName("reviewId").description("수정할 리뷰 ID")),
                                requestFields(
                                        fieldWithPath("content").description("수정할 내용").optional(),
                                        fieldWithPath("rate")
                                                .description("수정할 평점 (1~5)")
                                                .optional(),
                                        fieldWithPath("imageUrls[]")
                                                .description("수정할 이미지 목록")
                                                .optional())));
    }

    /** 내 리뷰 목록 조회 */
    @Test
    @DisplayName("내 리뷰 목록 조회 API")
    void getMyReviewsApiTest() throws Exception {
        ReviewResponse review =
                new ReviewResponse(
                        mockReviewId,
                        mockProductId,
                        1L,
                        "좋아요",
                        (short) 5,
                        List.of("https://img.com/1.jpg"));
        ReviewListResponse<ReviewResponse> mockResponse =
                ReviewListResponse.from(new PageImpl<>(List.of(review)));

        when(reviewService.getMyReviews(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review)));

        mockMvc.perform(get("/reviews/me").with(csrf()).with(user("1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reviewList[0].reviewId").value(mockReviewId.toString()))
                .andExpect(
                        jsonPath("$.data.reviewList[0].productId").value(mockProductId.toString()))
                .andDo(
                        document(
                                "review-list-me",
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.reviewList[].reviewId")
                                                .description("리뷰 ID"),
                                        fieldWithPath("data.reviewList[].productId")
                                                .description("상품 ID"),
                                        fieldWithPath("data.reviewList[].memberId")
                                                .description("작성자 ID"),
                                        fieldWithPath("data.reviewList[].content")
                                                .description("리뷰 내용"),
                                        fieldWithPath("data.reviewList[].rate").description("평점"),
                                        fieldWithPath("data.reviewList[].imageUrls[]")
                                                .description("이미지 URL 목록"),
                                        fieldWithPath("data.pageInfo.pageNumber")
                                                .description("현재 페이지"),
                                        fieldWithPath("data.pageInfo.pageSize")
                                                .description("페이지 크기"),
                                        fieldWithPath("data.pageInfo.totalElements")
                                                .description("전체 개수"),
                                        fieldWithPath("data.pageInfo.totalPages")
                                                .description("전체 페이지 수"),
                                        fieldWithPath("data.pageInfo.last")
                                                .description("마지막 페이지 여부"))));
    }

    /** 상품 리뷰 목록 조회 */
    @Test
    @DisplayName("상품 리뷰 목록 조회 API")
    void getProductReviewsApiTest() throws Exception {
        ReviewResponse review =
                new ReviewResponse(
                        mockReviewId,
                        mockProductId,
                        1L,
                        "괜찮아요",
                        (short) 4,
                        List.of("https://img.com/2.jpg"));

        when(reviewService.getProductReviews(eq(mockProductId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review)));

        mockMvc.perform(
                        get("/reviews/products/{productId}", mockProductId)
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reviewList[0].reviewId").value(mockReviewId.toString()))
                .andExpect(
                        jsonPath("$.data.reviewList[0].productId").value(mockProductId.toString()))
                .andDo(
                        document(
                                "review-list-product",
                                pathParameters(
                                        parameterWithName("productId").description("리뷰 조회할 상품 ID")),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.reviewList[].reviewId")
                                                .description("리뷰 ID"),
                                        fieldWithPath("data.reviewList[].productId")
                                                .description("상품 ID"),
                                        fieldWithPath("data.reviewList[].memberId")
                                                .description("작성자 ID"),
                                        fieldWithPath("data.reviewList[].content")
                                                .description("리뷰 내용"),
                                        fieldWithPath("data.reviewList[].rate").description("평점"),
                                        fieldWithPath("data.reviewList[].imageUrls[]")
                                                .description("이미지 URL 목록"),
                                        fieldWithPath("data.pageInfo.pageNumber")
                                                .description("현재 페이지"),
                                        fieldWithPath("data.pageInfo.pageSize")
                                                .description("페이지 크기"),
                                        fieldWithPath("data.pageInfo.totalElements")
                                                .description("전체 개수"),
                                        fieldWithPath("data.pageInfo.totalPages")
                                                .description("전체 페이지 수"),
                                        fieldWithPath("data.pageInfo.last")
                                                .description("마지막 페이지 여부"))));
    }

    /** 리뷰 삭제 */
    @Test
    @DisplayName("리뷰 삭제 API (고객 권한)")
    void deleteReviewApiTest() throws Exception {
        doNothing().when(reviewService).deleteReview(eq(mockReviewId));

        mockMvc.perform(
                        delete("/reviews/{reviewId}", mockReviewId)
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER")))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "review-delete",
                                pathParameters(
                                        parameterWithName("reviewId").description("삭제할 리뷰 ID"))));
    }
}
