package com.irum.come2us.domain.coupon;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.coupon.application.service.CouponService;
import com.irum.come2us.domain.coupon.presentation.controller.CouponController;
import com.irum.come2us.domain.coupon.presentation.dto.request.CouponGenerateRequest;
import com.irum.come2us.domain.coupon.presentation.dto.response.CouponResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.CouponErrorCode;
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
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CouponController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class CouponControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private CouponService couponService;
    @Autowired private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        public CouponService couponService() {
            return Mockito.mock(CouponService.class);
        }
    }

    @Test
    @DisplayName("쿠폰 생성 API")
    void couponCreateApiTest() throws Exception {

        // Given
        CouponGenerateRequest request =
                new CouponGenerateRequest(
                        "기간 한정 5000원 할인쿠폰", 5000, LocalDateTime.of(2028, 10, 10, 12, 12, 12));
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing().when(couponService).createCoupon(any(CouponGenerateRequest.class), anyLong());

        // when
        mockMvc.perform(
                        post("/coupons")
                                .with(csrf())
                                .with(user("1"))
                                .contentType("application/json")
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "coupon-create",
                                requestFields(
                                        fieldWithPath("name").description("쿠폰명"),
                                        fieldWithPath("discountAmount").description("할인 금액"),
                                        fieldWithPath("expiration").description("유효기간"))));
    }

    @Test
    @DisplayName("쿠폰 정보 조회 API")
    void couponListTest() throws Exception {
        // Given
        LocalDateTime now = LocalDateTime.of(2028, 10, 10, 12, 12, 12);
        CouponResponse coupon1 =
                new CouponResponse(
                        UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                        "기간 한정 5000원 할인쿠폰",
                        5000,
                        now);

        CouponResponse coupon2 =
                new CouponResponse(
                        UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                        "신규가입 10% 할인",
                        10,
                        now.plusDays(1));

        List<CouponResponse> responses = List.of(coupon1, coupon2);
        when(couponService.getCouponByMember(anyLong())).thenReturn(responses);

        // When & Then
        mockMvc.perform(get("/coupons").with(user("1")).accept("application/json"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(2)))
                // coupon1
                .andExpect(jsonPath("$.data[0].id").value("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"))
                .andExpect(jsonPath("$.data[0].name").value("기간 한정 5000원 할인쿠폰"))
                .andExpect(jsonPath("$.data[0].discountAmount").value(5000))
                .andExpect(jsonPath("$.data[0].expiration").value("2028-10-10 12:12:12"))
                // coupon2
                .andExpect(jsonPath("$.data[1].id").value("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"))
                .andExpect(jsonPath("$.data[1].name").value("신규가입 10% 할인"))
                .andExpect(jsonPath("$.data[1].discountAmount").value(10))
                .andExpect(jsonPath("$.data[1].expiration").value("2028-10-11 12:12:12"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(
                        document(
                                "coupon-list",
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("data").description("쿠폰 목록"),
                                        fieldWithPath("data[].id").description("쿠폰 ID"),
                                        fieldWithPath("data[].name").description("쿠폰명"),
                                        fieldWithPath("data[].discountAmount").description("할인 금액"),
                                        fieldWithPath("data[].expiration")
                                                .description("만료 일시 (yyyy-MM-dd HH:mm:ss)"),
                                        fieldWithPath("timestamp").description("응답 생성 시각"))));
    }

    @Test
    @DisplayName("쿠폰 삭제 API")
    void couponDeleteApiTest() throws Exception {
        // Given
        UUID couponId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        doNothing().when(couponService).deleteCoupon(couponId, 1L);

        // When & Then
        mockMvc.perform(delete("/coupons/{couponId}", couponId).with(csrf()).with(user("1")))
                .andExpect(status().isNoContent())
                .andDo(document("coupon-delete"));

        Mockito.verify(couponService).deleteCoupon(couponId, 1L);
    }

    // import static org.mockito.Mockito.doThrow;

    @Test
    @DisplayName("쿠폰 삭제 API - 쿠폰 없음")
    void couponDelete_NotFound() throws Exception {
        UUID couponId = UUID.fromString("cccccccc-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        doThrow(new CommonException(CouponErrorCode.COUPON_NOT_FOUND))
                .when(couponService)
                .deleteCoupon(couponId, 1L);

        mockMvc.perform(delete("/coupons/{coupon-id}", couponId).with(csrf()).with(user("1")))
                .andExpect(status().isNotFound());
    }
}
