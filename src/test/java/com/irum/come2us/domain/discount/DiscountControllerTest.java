package com.irum.come2us.domain.discount;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.discount.application.service.DiscountService;
import com.irum.come2us.domain.discount.presentation.controller.DiscountController;
import com.irum.come2us.domain.discount.presentation.dto.request.DiscountInfoUpdateRequest;
import com.irum.come2us.domain.discount.presentation.dto.request.DiscountRegisterRequest;
import com.irum.come2us.domain.discount.presentation.dto.response.DiscountInfoListResponse;
import com.irum.come2us.domain.discount.presentation.dto.response.DiscountInfoResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(DiscountController.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, TestConfig.class})
public class DiscountControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private DiscountService discountService;
    @Autowired private ObjectMapper objectMapper;

    private final UUID mockDiscountId = UUID.randomUUID();
    private final UUID mockDiscountId1 = UUID.randomUUID();
    private final UUID mockDiscountId2 = UUID.randomUUID();
    private final UUID mockProductId = UUID.randomUUID();
    private final UUID mockProductId1 = UUID.randomUUID();
    private final UUID mockProductId2 = UUID.randomUUID();
    private final UUID mockStoreId = UUID.randomUUID();

    @Test
    @DisplayName("할인 등록 API")
    void registerDiscountApiTest() throws Exception {
        DiscountRegisterRequest request = new DiscountRegisterRequest("할인1", 5000, mockProductId);
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing().when(discountService).createDiscount(any(DiscountRegisterRequest.class));

        mockMvc.perform(
                        post("/discounts")
                                .with(csrf())
                                .with(user("100").roles("OWNER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andDo(
                        document(
                                "discount-register",
                                requestFields(
                                        fieldWithPath("name").description("할인 이름"),
                                        fieldWithPath("amount").description("할인 금액"),
                                        fieldWithPath("productId").description("할인 적용 대상 상품 ID"))));
    }

    @Test
    @DisplayName("상품별 할인 정보 조회 API")
    void getDiscountInfoByProductApiTest() throws Exception {
        DiscountInfoResponse response =
                new DiscountInfoResponse(mockDiscountId, "할인1", 10000, mockProductId);

        when(discountService.findDiscountInfoByProduct(eq(mockProductId))).thenReturn(response);

        mockMvc.perform(
                        get("/products/{productId}/discounts", mockProductId)
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.discountId").value(mockDiscountId.toString()))
                .andExpect(jsonPath("$.data.name").value(response.name()))
                .andExpect(jsonPath("$.data.amount").value(10000))
                .andDo(
                        document(
                                "discount-info-get-by-product",
                                pathParameters(
                                        parameterWithName("productId")
                                                .description("할인 정보를 조회할 상품 ID")),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.discountId").description("할인 식별 ID"),
                                        fieldWithPath("data.name").description("할인 이름"),
                                        fieldWithPath("data.amount").description("할인 금액"),
                                        fieldWithPath("data.productId")
                                                .description("할인이 적용된 상품 ID"))));
    }

    @Test
    @DisplayName("상점별 할인 목록 조회 API")
    void getDiscountListInfoByStoreApiTest() throws Exception {
        List<DiscountInfoResponse> discountInfoList =
                List.of(
                        new DiscountInfoResponse(mockDiscountId2, "할인2", 10000, mockProductId1),
                        new DiscountInfoResponse(mockDiscountId1, "할인1", 10000, mockProductId2));

        DiscountInfoListResponse response =
                new DiscountInfoListResponse(discountInfoList, null, false);

        when(discountService.findDiscountInfoListByStore(eq(mockStoreId), any(), anyInt()))
                .thenReturn(response);

        mockMvc.perform(
                        get("/stores/{storeId}/discounts", mockStoreId)
                                .param("size", "10")
                                .with(csrf().asHeader())
                                .with(user("1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(
                        jsonPath("$.data.discountInfoList[0].discountId")
                                .value(mockDiscountId2.toString()))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andDo(
                        document(
                                "discount-list-get-by-store",
                                pathParameters(
                                        parameterWithName("storeId")
                                                .description("할인 목록 조회할 상점 ID")),
                                queryParameters(
                                        parameterWithName("cursor")
                                                .description("마지막으로 조회된 할인 ID")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("조회할 페이지 크기 (기본값 10)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.discountInfoList[]")
                                                .description("할인 정보 목록"),
                                        fieldWithPath("data.discountInfoList[].discountId")
                                                .description("할인 식별 ID"),
                                        fieldWithPath("data.discountInfoList[].name")
                                                .description("할인 이름"),
                                        fieldWithPath("data.discountInfoList[].amount")
                                                .description("할인 금액"),
                                        fieldWithPath("data.discountInfoList[].productId")
                                                .description("할인이 적용된 상품 ID"),
                                        fieldWithPath("data.nextCursor")
                                                .description("다음 페이지 조회 위한 커서 ID"),
                                        fieldWithPath("data.hasNext")
                                                .description("다음 페이지 존재 여부"))));
    }

    @Test
    @DisplayName("할인 정보 수정 API (사장님 권한)")
    void updateDiscountInfoApiTest() throws Exception {
        DiscountInfoUpdateRequest request = new DiscountInfoUpdateRequest("수정된 할인 이름", 7500);
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing()
                .when(discountService)
                .changeDiscountInfo(eq(mockDiscountId), any(DiscountInfoUpdateRequest.class));

        mockMvc.perform(
                        patch("/discounts/{discountId}", mockDiscountId)
                                .with(csrf())
                                .with(user("100").roles("OWNER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "discount-info-update",
                                pathParameters(
                                        parameterWithName("discountId").description("수정할 할인 ID")),
                                requestFields(
                                        fieldWithPath("name").description("변경할 할인 이름").optional(),
                                        fieldWithPath("amount").description("변경할 할인 금액"))));
    }

    @Test
    @DisplayName("할인 정보 삭제 API (사장님 권한)")
    void deleteDiscountApiTest() throws Exception {
        doNothing().when(discountService).removeDiscount(eq(mockDiscountId));

        mockMvc.perform(
                        delete("/discounts/{discountId}", mockDiscountId)
                                .with(csrf())
                                .with(user("100").roles("OWNER")))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "discount-delete",
                                pathParameters(
                                        parameterWithName("discountId").description("삭제할 할인 ID"))));
    }
}
