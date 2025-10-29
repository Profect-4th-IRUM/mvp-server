package com.irum.come2us.domain.order;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.order.application.service.OwnerOrderService;
import com.irum.come2us.domain.order.presentation.controller.OwnerOrderController;
import com.irum.come2us.domain.order.presentation.dto.request.OwnerOrderShippedRequest;
import com.irum.come2us.domain.order.presentation.dto.response.OwnerOrderListResponse;
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

@WebMvcTest(OwnerOrderController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class OwnerOrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private OwnerOrderService ownerOrderService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public OwnerOrderService ownerOrderService() {
            return Mockito.mock(OwnerOrderService.class);
        }
    }

    // 배송 준비중
    @Test
    @DisplayName("[상점] 배송 준비 중 목록 조회 API")
    void preparingOrderListGet() throws Exception {
        // given
        UUID storeId = UUID.randomUUID();

        OwnerOrderListResponse.ProductSummary product1 =
                new OwnerOrderListResponse.ProductSummary(
                        UUID.randomUUID(), "라운드 반팔 티셔츠", 2, 15000, "색상: 블랙 / 사이즈: M");

        OwnerOrderListResponse.OrderSummary orderSummary =
                new OwnerOrderListResponse.OrderSummary(
                        UUID.randomUUID(),
                        "홍길동",
                        "010-1234-5678",
                        "서울특별시 강남구 테헤란로 1",
                        LocalDateTime.of(2025, 10, 28, 13, 0),
                        30000,
                        1000,
                        29000,
                        3000,
                        List.of(product1));

        OwnerOrderListResponse response =
                new OwnerOrderListResponse(List.of(orderSummary), null, false);

        Mockito.when(ownerOrderService.getPreparingOrderList(any(), any(), any()))
                .thenReturn(response);

        // when & then
        mockMvc.perform(
                        get("/orders/owner/{storeId}/preparing", storeId)
                                .param("size", "10")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "owner-order-preparing-get",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(parameterWithName("storeId").description("상점 ID")),
                                queryParameters(
                                        parameterWithName("cursor")
                                                .description("다음 페이지 커서")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("페이지 크기 (기본값 10)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.orderList[].orderId")
                                                .description("주문 ID"),
                                        fieldWithPath("data.orderList[].recipientName")
                                                .description("수령인 이름"),
                                        fieldWithPath("data.orderList[].recipientContact")
                                                .description("수령인 연락처"),
                                        fieldWithPath("data.orderList[].recipientAddress")
                                                .description("수령인 주소"),
                                        fieldWithPath("data.orderList[].orderDate")
                                                .description("주문 일시"),
                                        fieldWithPath("data.orderList[].totalProductPrice")
                                                .description("총 상품 금액"),
                                        fieldWithPath("data.orderList[].discountAmount")
                                                .description("할인 금액"),
                                        fieldWithPath("data.orderList[].payingAmount")
                                                .description("결제 금액"),
                                        fieldWithPath("data.orderList[].deliveryFee")
                                                .description("배송비"),
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
                                                .description("옵션 정보"),
                                        fieldWithPath("data.nextCursor").description("다음 커서"),
                                        fieldWithPath("data.hasNext").description("다음 페이지 여부"))));
    }

    // 2 배송중
    @Test
    @DisplayName("[상점] 부분 배송 중 목록 조회 API")
    void partiallyShippedOrderListGet() throws Exception {
        // given
        UUID storeId = UUID.randomUUID();

        OwnerOrderListResponse.ProductSummary product1 =
                new OwnerOrderListResponse.ProductSummary(
                        UUID.randomUUID(), "기모 후드티", 1, 35000, "색상: 그레이 / 사이즈: L");

        OwnerOrderListResponse.OrderSummary orderSummary =
                new OwnerOrderListResponse.OrderSummary(
                        UUID.randomUUID(),
                        "이영희",
                        "010-5678-9999",
                        "서울특별시 서초구 강남대로 100",
                        LocalDateTime.of(2025, 10, 28, 14, 0),
                        35000,
                        0,
                        35000,
                        3000,
                        List.of(product1));

        OwnerOrderListResponse response =
                new OwnerOrderListResponse(List.of(orderSummary), null, false);

        Mockito.when(ownerOrderService.getPartiallyShippedOrderList(any(), any(), any()))
                .thenReturn(response);

        // when & then
        mockMvc.perform(
                        get("/orders/owner/{storeId}/partially-shipped", storeId)
                                .param("size", "10")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "owner-order-partially-shipped-get",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(parameterWithName("storeId").description("상점 ID")),
                                queryParameters(
                                        parameterWithName("cursor")
                                                .description("다음 페이지 커서 (선택)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("페이지 크기 (기본값 10)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.orderList[].orderId")
                                                .description("주문 ID"),
                                        fieldWithPath("data.orderList[].recipientName")
                                                .description("수령인 이름"),
                                        fieldWithPath("data.orderList[].recipientContact")
                                                .description("수령인 연락처"),
                                        fieldWithPath("data.orderList[].recipientAddress")
                                                .description("수령인 주소"),
                                        fieldWithPath("data.orderList[].orderDate")
                                                .description("주문 일시"),
                                        fieldWithPath("data.orderList[].totalProductPrice")
                                                .description("총 상품 금액"),
                                        fieldWithPath("data.orderList[].discountAmount")
                                                .description("할인 금액"),
                                        fieldWithPath("data.orderList[].payingAmount")
                                                .description("결제 금액"),
                                        fieldWithPath("data.orderList[].deliveryFee")
                                                .description("배송비"),
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
                                                .description("옵션 정보"),
                                        fieldWithPath("data.nextCursor").description("다음 커서"),
                                        fieldWithPath("data.hasNext").description("다음 페이지 여부"))));
    }

    // 3 배송완료
    @Test
    @DisplayName("[상점] 부분 배송 완료 목록 조회 API")
    void partiallyDeliveredOrderListGet() throws Exception {
        // given
        UUID storeId = UUID.randomUUID();

        OwnerOrderListResponse.ProductSummary product1 =
                new OwnerOrderListResponse.ProductSummary(
                        UUID.randomUUID(), "청바지", 1, 45000, "사이즈: M / 색상: 블루");

        OwnerOrderListResponse.OrderSummary orderSummary =
                new OwnerOrderListResponse.OrderSummary(
                        UUID.randomUUID(),
                        "박민수",
                        "010-7777-8888",
                        "서울특별시 마포구 홍익로 5",
                        LocalDateTime.of(2025, 10, 28, 15, 30),
                        45000,
                        2000,
                        43000,
                        3000,
                        List.of(product1));

        OwnerOrderListResponse response =
                new OwnerOrderListResponse(List.of(orderSummary), null, false);

        Mockito.when(ownerOrderService.getPartiallyDeliveredOrderList(any(), any(), any()))
                .thenReturn(response);

        // when & then
        mockMvc.perform(
                        get("/orders/owner/{storeId}/partially-delivered", storeId)
                                .param("size", "10")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "owner-order-partially-delivered-get",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(parameterWithName("storeId").description("상점 ID")),
                                queryParameters(
                                        parameterWithName("cursor")
                                                .description("다음 페이지 커서 (선택)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("페이지 크기 (기본값 10)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.orderList[].orderId")
                                                .description("주문 ID"),
                                        fieldWithPath("data.orderList[].recipientName")
                                                .description("수령인 이름"),
                                        fieldWithPath("data.orderList[].recipientContact")
                                                .description("수령인 연락처"),
                                        fieldWithPath("data.orderList[].recipientAddress")
                                                .description("수령인 주소"),
                                        fieldWithPath("data.orderList[].orderDate")
                                                .description("주문 일시"),
                                        fieldWithPath("data.orderList[].totalProductPrice")
                                                .description("총 상품 금액"),
                                        fieldWithPath("data.orderList[].discountAmount")
                                                .description("할인 금액"),
                                        fieldWithPath("data.orderList[].payingAmount")
                                                .description("결제 금액"),
                                        fieldWithPath("data.orderList[].deliveryFee")
                                                .description("배송비"),
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
                                                .description("옵션 정보"),
                                        fieldWithPath("data.nextCursor").description("다음 커서"),
                                        fieldWithPath("data.hasNext").description("다음 페이지 여부"))));
    }

    // 배송준비중 상태변경
    @Test
    @DisplayName("[상점] 주문 상태 변경 (배송준비중) API")
    void orderStatusToPreparingUpdate() throws Exception {
        // given
        UUID orderDetailId = UUID.randomUUID();
        Mockito.doNothing().when(ownerOrderService).updateOrderStatusToPreparing(orderDetailId);

        // when & then
        mockMvc.perform(
                        patch(
                                        "/orders/owner/order-details/{orderDetailId}/preparing",
                                        orderDetailId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "owner-order-status-to-preparing-update",
                                preprocessRequest(prettyPrint()),
                                pathParameters(
                                        parameterWithName("orderDetailId")
                                                .description("주문 상세 ID"))));
    }

    // 배송중 상태변경
    @Test
    @DisplayName("[상점] 주문 상태 변경 (배송중) API")
    void orderStatusToShippedUpdate() throws Exception {
        // given
        UUID orderDetailId = UUID.randomUUID();
        OwnerOrderShippedRequest request = new OwnerOrderShippedRequest("1234567890123");
        String requestJson = objectMapper.writeValueAsString(request);

        Mockito.doNothing()
                .when(ownerOrderService)
                .updateOrderStatusToShipped(orderDetailId, request);

        // when & then
        mockMvc.perform(
                        patch("/orders/owner/order-details/{orderDetailId}/shipped", orderDetailId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "owner-order-status-to-shipped-update",
                                preprocessRequest(prettyPrint()),
                                pathParameters(
                                        parameterWithName("orderDetailId").description("주문 상세 ID")),
                                requestFields(
                                        fieldWithPath("trackingNumber")
                                                .description("운송장 번호 (필수)"))));
    }

    // 6 배송완료 상태변경
    @Test
    @DisplayName("[상점] 주문 상태 변경 (배송완료) API")
    void orderStatusToDeliveredUpdate() throws Exception {
        // given
        UUID orderDetailId = UUID.randomUUID();
        Mockito.doNothing().when(ownerOrderService).updateOrderStatusToDelivered(orderDetailId);

        // when & then
        mockMvc.perform(
                        patch(
                                        "/orders/owner/order-details/{orderDetailId}/delivered",
                                        orderDetailId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "owner-order-status-to-delivered-update",
                                preprocessRequest(prettyPrint()),
                                pathParameters(
                                        parameterWithName("orderDetailId")
                                                .description("배송완료로 변경할 주문 상세 ID"))));
    }
}
