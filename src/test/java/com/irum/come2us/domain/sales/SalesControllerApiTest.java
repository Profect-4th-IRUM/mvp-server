package com.irum.come2us.domain.sales;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.order.application.service.SalesService;
import com.irum.come2us.domain.order.presentation.controller.SalesController;
import com.irum.come2us.domain.order.presentation.dto.response.BalanceResponse;
import com.irum.come2us.domain.order.presentation.dto.response.SalesResponse;
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
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SalesController.class)
@AutoConfigureRestDocs
@Import({TestConfig.class, SecurityTestConfig.class})
public class SalesControllerApiTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private SalesService salesService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("총 주문 내역 API")
    void salesListApiTest() throws Exception {
        UUID storeId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID orderDetailId = UUID.randomUUID();

        SalesResponse.ProductSummary productSummary =
                new SalesResponse.ProductSummary(orderDetailId, "상품1", 2, 10000, "옵션1");

        SalesResponse.OrderSummary orderSummary =
                new SalesResponse.OrderSummary(
                        orderId,
                        "홍길동",
                        "010-1234-5678",
                        "서울시 강남구",
                        LocalDateTime.of(2025, 10, 25, 11, 30, 0),
                        20000,
                        2000,
                        21000,
                        3000,
                        List.of(productSummary),
                        "COMPLETED");

        SalesResponse response = new SalesResponse(List.of(orderSummary), null, false);

        when(salesService.getSalesList(storeId)).thenReturn(response);

        mockMvc.perform(get("/orders/{storeId}/sales", storeId).with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.orderList[0].orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.data.orderList[0].recipientName").value("홍길동"))
                .andExpect(jsonPath("$.data.orderList[0].recipientContact").value("010-1234-5678"))
                .andExpect(jsonPath("$.data.orderList[0].recipientAddress").value("서울시 강남구"))
                .andExpect(jsonPath("$.data.orderList[0].totalProductPrice").value(20000))
                .andExpect(jsonPath("$.data.orderList[0].discountAmount").value(2000))
                .andExpect(jsonPath("$.data.orderList[0].payingAmount").value(21000))
                .andExpect(jsonPath("$.data.orderList[0].deliveryFee").value(3000))
                .andExpect(jsonPath("$.data.orderList[0].orderStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.data.orderList[0].productList[0].productName").value("상품1"))
                .andExpect(jsonPath("$.data.orderList[0].productList[0].productCounts").value(2))
                .andExpect(jsonPath("$.data.orderList[0].productList[0].productPrice").value(10000))
                .andExpect(jsonPath("$.data.orderList[0].productList[0].optionTitle").value("옵션1"))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andDo(
                        document(
                                "sales-list-get",
                                pathParameters(
                                        parameterWithName("storeId").description("스토어 식별 ID")),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp")
                                                .description("2025-10-28T14:20:08.101904"),
                                        fieldWithPath("data.orderList[]").description("주문 목록"),
                                        fieldWithPath("data.orderList[].orderId")
                                                .description("주문 ID"),
                                        fieldWithPath("data.orderList[].recipientName")
                                                .description("수령인 이름"),
                                        fieldWithPath("data.orderList[].recipientContact")
                                                .description("수령인 연락처"),
                                        fieldWithPath("data.orderList[].recipientAddress")
                                                .description("수령인 주소"),
                                        fieldWithPath("data.orderList[].orderDate")
                                                .description("주문 일시 ex) 2025-10-25 11:30:00"),
                                        fieldWithPath("data.orderList[].totalProductPrice")
                                                .description("총 상품 가격"),
                                        fieldWithPath("data.orderList[].discountAmount")
                                                .description("할인 금액"),
                                        fieldWithPath("data.orderList[].payingAmount")
                                                .description("최종 결제 금액"),
                                        fieldWithPath("data.orderList[].deliveryFee")
                                                .description("배송비"),
                                        fieldWithPath("data.orderList[].productList[]")
                                                .description("상품 목록"),
                                        fieldWithPath(
                                                        "data.orderList[].productList[].orderDetailId")
                                                .description("주문 상세 ID"),
                                        fieldWithPath("data.orderList[].productList[].productName")
                                                .description("상품명"),
                                        fieldWithPath(
                                                        "data.orderList[].productList[].productCounts")
                                                .description("상품 수량"),
                                        fieldWithPath("data.orderList[].productList[].productPrice")
                                                .description("상품 가격"),
                                        fieldWithPath("data.orderList[].productList[].optionTitle")
                                                .description("옵션명"),
                                        fieldWithPath("data.orderList[].orderStatus")
                                                .description("주문 상태 ex) COMPLETED, PENDING"),
                                        fieldWithPath("data.nextCursor")
                                                .description("다음 페이지 커서 (없으면 null)"),
                                        fieldWithPath("data.hasNext")
                                                .description("다음 페이지 존재 여부"))));
    }

    @Test
    @DisplayName("정산 내역 조회 API")
    void balanceGetApiTest() throws Exception {
        UUID storeId = UUID.randomUUID();
        BalanceResponse response = new BalanceResponse(100000, 10000, 90000);

        when(salesService.getBalance(storeId)).thenReturn(response);

        mockMvc.perform(get("/orders/{storeId}/balance", storeId).with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.totalSalesAmount").value(100000))
                .andExpect(jsonPath("$.data.totalRefundAmount").value(10000))
                .andExpect(jsonPath("$.data.settlementAmount").value(90000))
                .andDo(
                        document(
                                "balance-get",
                                pathParameters(
                                        parameterWithName("storeId").description("스토어 식별 ID")),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp")
                                                .description("2025-10-28T14:20:08.055808"),
                                        fieldWithPath("data.totalSalesAmount")
                                                .description("총 결제 금액"),
                                        fieldWithPath("data.totalRefundAmount")
                                                .description("총 환불 금액"),
                                        fieldWithPath("data.settlementAmount")
                                                .description("정산 금액 (결제 금액 - 환불 금액)"))));
    }
}
