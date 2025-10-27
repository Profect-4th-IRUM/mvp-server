package com.irum.come2us.domain.refund;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.refund.application.service.RefundService;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundReason;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import com.irum.come2us.domain.refund.presentation.controller.RefundController;
import com.irum.come2us.domain.refund.presentation.dto.request.RefundCreateRequest;
import com.irum.come2us.domain.refund.presentation.dto.request.StoreRefundStatusRequest;
import com.irum.come2us.domain.refund.presentation.dto.response.RefundDetailResponse;
import com.irum.come2us.domain.refund.presentation.dto.response.RefundDetailResponse.ProductInfoDto;
import com.irum.come2us.domain.refund.presentation.dto.response.RefundOrderList;
import com.irum.come2us.domain.refund.presentation.dto.response.RefundProductList;
import com.irum.come2us.domain.refund.presentation.dto.response.StoreRefundListResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
import com.irum.come2us.global.config.TestConfig;
import java.time.LocalDateTime;
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

@WebMvcTest(RefundController.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, TestConfig.class})
public class RefundControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private RefundService refundService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("환불 등록 API")
    void registerRefundApiTest() throws Exception {
        UUID orderId = UUID.randomUUID();
        RefundCreateRequest request =
                new RefundCreateRequest(RefundReason.MERCHANDISE_DAMAGED, "상품 파손");
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing().when(refundService).createRefund(eq(orderId), any(RefundCreateRequest.class));

        mockMvc.perform(
                        post("/refunds/{orderId}", orderId)
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andDo(
                        document(
                                "refund-register",
                                pathParameters(
                                        parameterWithName("orderId").description("환불을 요청할 주문 ID")),
                                requestFields(
                                        fieldWithPath("reason").description("환불 사유"),
                                        fieldWithPath("description")
                                                .description("상세 설명")
                                                .optional())));
    }

    @Test
    @DisplayName("환불 상세 조회 API")
    void getRefundDetailApiTest() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID refundId = UUID.randomUUID();
        LocalDateTime cancelDate = LocalDateTime.now();

        // 1. DTO Mocking
        RefundDetailResponse.RecipientAddressDto addressDto =
                new RefundDetailResponse.RecipientAddressDto(
                        "12345", "서울시", "강남구", "테헤란로 145", "더피나클 역삼2", "회원1", "010-1234-5678");

        List<ProductInfoDto> productList =
                List.of(
                        new ProductInfoDto(UUID.randomUUID(), 15000, "상품A", "옵션1", 1),
                        new ProductInfoDto(UUID.randomUUID(), 20000, "상품B", "옵션2", 2));

        RefundDetailResponse mockResponse =
                new RefundDetailResponse(
                        orderId,
                        "ORD12345",
                        productList,
                        addressDto,
                        refundId,
                        cancelDate,
                        RefundStatus.REQUESTED,
                        55000,
                        RefundReason.MERCHANDISE_DAMAGED);

        when(refundService.findRefundDetail(eq(orderId))).thenReturn(mockResponse);

        // 2. MockMvc 수행
        mockMvc.perform(
                        get("/refunds/{orderId}/detail", orderId)
                                .with(csrf())
                                .with(user("1").roles("CUSTOMER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNum").value("ORD12345"))
                .andExpect(jsonPath("$.data.refundStatus").value("REQUESTED"))
                .andExpect(jsonPath("$.data.productList[0].productName").value("상품A"))
                .andDo(
                        document(
                                "refund-detail-get",
                                pathParameters(
                                        parameterWithName("orderId")
                                                .description("환불 정보를 조회할 주문 ID")),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.orderId").description("주문 식별 ID"),
                                        fieldWithPath("data.orderNum").description("주문 번호"),
                                        fieldWithPath("data.refundId").description("환불 식별 ID"),
                                        fieldWithPath("data.cancelDate").description("환불 요청 일시"),
                                        fieldWithPath("data.refundStatus")
                                                .description("환불 상태 (REQUESTED 등)"),
                                        fieldWithPath("data.refundPrice").description("환불 금액"),
                                        fieldWithPath("data.refundReason").description("환불 사유"),
                                        fieldWithPath("data.recipientAddress.postalCode")
                                                .description("수취인 우편번호"),
                                        fieldWithPath("data.recipientAddress.city")
                                                .description("수취인 시/도"),
                                        fieldWithPath("data.recipientAddress.sigungu")
                                                .description("수취인 시/군/구"),
                                        fieldWithPath("data.recipientAddress.RoadName")
                                                .description("수취인 도로명"),
                                        fieldWithPath("data.recipientAddress.AddressDetail")
                                                .description("수취인 상세 주소"),
                                        fieldWithPath("data.recipientAddress.recipientName")
                                                .description("수취인 이름"),
                                        fieldWithPath("data.recipientAddress.recipientContact")
                                                .description("수취인 연락처"),
                                        fieldWithPath("data.productList[]").description("환불 상품 목록"),
                                        fieldWithPath("data.productList[].orderDetailId")
                                                .description("주문 상세 ID"),
                                        fieldWithPath("data.productList[].productPrice")
                                                .description("개별 상품 가격"),
                                        fieldWithPath("data.productList[].productName")
                                                .description("상품 이름"),
                                        fieldWithPath("data.productList[].optionTitle")
                                                .description("선택 옵션 이름"),
                                        fieldWithPath("data.productList[].quantity")
                                                .description("상품 수량"))));
    }

    // --- Owner API Tests ---

    @Test
    @DisplayName("환불 상태 변경 API")
    void patchRefundStatusApiTest() throws Exception {
        UUID refundId = UUID.randomUUID();
        StoreRefundStatusRequest request = new StoreRefundStatusRequest(RefundStatus.APPROVED);
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing()
                .when(refundService)
                .changeRefundStatus(eq(refundId), any(StoreRefundStatusRequest.class));

        mockMvc.perform(
                        patch("/refunds/{refund-id}", refundId)
                                .with(csrf())
                                .with(user("100").roles("OWNER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "refund-status-patch",
                                pathParameters(
                                        parameterWithName("refund-id").description("상태 변경할 환불 ID")),
                                requestFields(
                                        fieldWithPath("refundStatus").description("변경할 환불 상태"))));
    }

    @Test
    @DisplayName("환불 요청 목록 조회 API")
    void getRefundListApiTest() throws Exception {
        UUID orderId1 = UUID.randomUUID();
        UUID orderId2 = UUID.randomUUID();

        RefundProductList product1 =
                new RefundProductList(UUID.randomUUID(), "상품A", 1, 10000, "옵션1");
        RefundProductList product2 =
                new RefundProductList(UUID.randomUUID(), "상품B", 2, 20000, "옵션2");

        RefundOrderList order1 =
                new RefundOrderList(
                        orderId1,
                        "회원1",
                        LocalDateTime.now().minusDays(1),
                        LocalDateTime.now(),
                        RefundStatus.REQUESTED,
                        30000,
                        List.of(product1));

        RefundOrderList order2 =
                new RefundOrderList(
                        orderId2,
                        "회원2",
                        LocalDateTime.now().minusDays(2),
                        LocalDateTime.now().minusHours(1),
                        RefundStatus.REQUESTED,
                        40000,
                        List.of(product2));

        StoreRefundListResponse mockResponse =
                new StoreRefundListResponse(List.of(order1, order2), "70000", false, null);

        when(refundService.findRefundListByStatus(eq(RefundStatus.REQUESTED)))
                .thenReturn(mockResponse);
        mockMvc.perform(
                        get("/refunds/list")
                                .param("status", RefundStatus.REQUESTED.name())
                                .with(csrf().asHeader())
                                .with(user("100").roles("OWNER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalRefundPrice").value("70000"))
                .andExpect(jsonPath("$.data.refundOrders[0].recipientName").value("회원1"))
                .andDo(
                        document(
                                "refund-list-get",
                                queryParameters(
                                        parameterWithName("status")
                                                .description("조회할 환불 상태")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.totalRefundPrice")
                                                .description("총 환불 금액"),
                                        fieldWithPath("data.hasNext").description("다음 페이지 존재 여부"),
                                        fieldWithPath("data.nextCursor")
                                                .description("다음 페이지 조회 위한 커서"),
                                        fieldWithPath("data.refundOrders[]")
                                                .description("환불 주문 목록"),
                                        fieldWithPath("data.refundOrders[].orderId")
                                                .description("주문 ID"),
                                        fieldWithPath("data.refundOrders[].recipientName")
                                                .description("수취인 이름"),
                                        fieldWithPath("data.refundOrders[].orderDate")
                                                .description("주문 일시"),
                                        fieldWithPath("data.refundOrders[].cancelDate")
                                                .description("환불 요청 일시"),
                                        fieldWithPath("data.refundOrders[].refundStatus")
                                                .description("환불 상태"),
                                        fieldWithPath("data.refundOrders[].refundPrice")
                                                .description("환불 금액"),
                                        fieldWithPath("data.refundOrders[].productLists[]")
                                                .description("환불 상품 목록"),
                                        fieldWithPath(
                                                        "data.refundOrders[].productLists[].orderDetailId")
                                                .description("주문 상세 ID"),
                                        fieldWithPath(
                                                        "data.refundOrders[].productLists[].productName")
                                                .description("상품 이름"),
                                        fieldWithPath("data.refundOrders[].productLists[].quantity")
                                                .description("상품 수량"),
                                        fieldWithPath(
                                                        "data.refundOrders[].productLists[].productPrice")
                                                .description("상품 가격"),
                                        fieldWithPath(
                                                        "data.refundOrders[].productLists[].optionTitle")
                                                .description("상품 옵션"))));
    }
}
