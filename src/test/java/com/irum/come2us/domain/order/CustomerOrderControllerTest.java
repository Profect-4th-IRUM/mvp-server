package com.irum.come2us.domain.order;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.order.application.service.CustomerOrderService;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.order.presentation.controller.CustomerOrderController;
import com.irum.come2us.domain.order.presentation.dto.request.CustomerOrderRequest;
import com.irum.come2us.domain.order.presentation.dto.response.*;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import com.irum.come2us.global.config.SecurityTestConfig;
import java.time.LocalDate;
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

@WebMvcTest(CustomerOrderController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class CustomerOrderControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private CustomerOrderService customerOrderService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CustomerOrderService customerOrderService() {
            return Mockito.mock(CustomerOrderService.class);
        }
    }

    @Test
    @DisplayName("주문 상태 조회 API")
    void orderStatusGet() throws Exception {
        // given
        UUID orderDetailId = UUID.randomUUID();
        AddressResponse address = new AddressResponse("06241", "서울특별시", "강남구", "테헤란로 1", "101호");

        OrderDetailStatusResponse response =
                new OrderDetailStatusResponse(
                        "라운드 반팔 티셔츠",
                        2,
                        "색상: 블랙 / 사이즈: M",
                        address,
                        "홍길동",
                        OrderStatus.SHIPPED,
                        "123456789",
                        "문 앞에 두세요");

        Mockito.when(customerOrderService.getOrderDetailStatus(orderDetailId)).thenReturn(response);

        // when & then
        mockMvc.perform(
                        get(
                                        "/orders/customer/order-detail/{orderDetailId}/delivery-status",
                                        orderDetailId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "customer-order-detail-status-get",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("orderDetailId").description("상세 주문 ID")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.productName").description("상품명"),
                                        fieldWithPath("data.quantity").description("주문 수량"),
                                        fieldWithPath("data.optionName").description("옵션 정보"),
                                        fieldWithPath("data.address.postalCode")
                                                .description("우편번호"),
                                        fieldWithPath("data.address.city").description("도시"),
                                        fieldWithPath("data.address.sigungu").description("시군구"),
                                        fieldWithPath("data.address.roadname").description("도로명"),
                                        fieldWithPath("data.address.addressDetail")
                                                .description("상세 주소"),
                                        fieldWithPath("data.recipientName").description("수령인 이름"),
                                        fieldWithPath("data.orderStatus").description("주문 상태"),
                                        fieldWithPath("data.trackingNumber").description("운송장 번호"),
                                        fieldWithPath("data.deliveryRequest")
                                                .description("배송 요청사항"))));
    }

    @Test
    @DisplayName("주문 상세 조회 API")
    void orderDetailGetTest() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();

        AddressResponse address = new AddressResponse("06241", "서울특별시", "강남구", "테헤란로 1", "101호");

        List<OrderDetailResponse.ProductResponse> productResponses =
                List.of(
                        new OrderDetailResponse.ProductResponse(
                                "라운드 반팔 티셔츠", "색상: 블랙 / 사이즈: M", 2, 15000, LocalDate.now()),
                        new OrderDetailResponse.ProductResponse(
                                "린넨 셔츠", "색상: 아이보리 / 사이즈: L", 1, 32000, LocalDate.now()));

        OrderDetailResponse response =
                OrderDetailResponse.builder()
                        .orderAt(LocalDateTime.of(2025, 10, 28, 13, 30))
                        .paymentStatus(PaymentStatus.PENDING)
                        .paymentMethod(PaymentMethod.CARD)
                        .deliveryFee(3000)
                        .discountAmount(2000)
                        .totalProductPrice(62000)
                        .totalPaymentPrice(63000)
                        .orderStatusAll(OrderStatus.DELIVERED)
                        .refundStatus(RefundStatus.PENDING)
                        .deliveryRequest("문 앞에 두세요")
                        .address(address)
                        .recipientContact("010-1234-5678")
                        .recipientName("홍길동")
                        .productResponseList(productResponses)
                        .build();

        Mockito.when(customerOrderService.getOrderDetail(orderId)).thenReturn(response);

        // when & then
        mockMvc.perform(
                        get("/orders/customer/{orderId}", orderId)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "customer-order-detail-get",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(parameterWithName("orderId").description("주문 ID")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),

                                        // Order 기본 정보
                                        fieldWithPath("data.orderAt").description("주문 일시"),
                                        fieldWithPath("data.paymentStatus").description("결제 상태"),
                                        fieldWithPath("data.paymentMethod").description("결제 수단"),
                                        fieldWithPath("data.deliveryFee").description("배송비"),
                                        fieldWithPath("data.discountAmount").description("총 할인 금액"),
                                        fieldWithPath("data.totalProductPrice")
                                                .description("총 상품 금액"),
                                        fieldWithPath("data.totalPaymentPrice")
                                                .description("총 결제 금액"),
                                        fieldWithPath("data.orderStatusAll")
                                                .description("전체 주문 상태"),
                                        fieldWithPath("data.refundStatus").description("환불 상태"),
                                        fieldWithPath("data.deliveryRequest")
                                                .description("배송 요청사항"),

                                        // Address
                                        fieldWithPath("data.address.postalCode")
                                                .description("우편번호"),
                                        fieldWithPath("data.address.city").description("도시"),
                                        fieldWithPath("data.address.sigungu").description("시군구"),
                                        fieldWithPath("data.address.roadname").description("도로명"),
                                        fieldWithPath("data.address.addressDetail")
                                                .description("상세 주소"),

                                        // 수령인 정보
                                        fieldWithPath("data.recipientContact")
                                                .description("수령인 연락처"),
                                        fieldWithPath("data.recipientName").description("수령인 이름"),

                                        // 상품 리스트
                                        fieldWithPath("data.productResponseList[].productName")
                                                .description("상품명"),
                                        fieldWithPath("data.productResponseList[].optionTitle")
                                                .description("옵션명"),
                                        fieldWithPath("data.productResponseList[].quantity")
                                                .description("수량"),
                                        fieldWithPath("data.productResponseList[].price")
                                                .description("가격"),
                                        fieldWithPath("data.productResponseList[].receivedDate")
                                                .description("도착 일자"))));
    }

    @Test
    @DisplayName("주문 목록 조회 API")
    void orderListGetTest() throws Exception {
        // given
        UUID nextCursor = UUID.randomUUID();

        // 상품 리스트 1
        List<CustomerOrderListResponse.ProductResponse> productList1 =
                List.of(
                        new CustomerOrderListResponse.ProductResponse(
                                UUID.randomUUID(),
                                "라운드 반팔 티셔츠",
                                "색상: 블랙 / 사이즈: M",
                                2,
                                15000,
                                OrderStatus.DELIVERED),
                        new CustomerOrderListResponse.ProductResponse(
                                UUID.randomUUID(),
                                "린넨 셔츠",
                                "색상: 아이보리 / 사이즈: L",
                                1,
                                32000,
                                OrderStatus.DELIVERED));

        // 상품 리스트 2
        List<CustomerOrderListResponse.ProductResponse> productList2 =
                List.of(
                        new CustomerOrderListResponse.ProductResponse(
                                UUID.randomUUID(),
                                "패딩 점퍼",
                                "색상: 블루 / 사이즈: L",
                                1,
                                89000,
                                OrderStatus.SHIPPED));

        // 주문 리스트
        List<CustomerOrderListResponse.OrderResponse> orderList =
                List.of(
                        new CustomerOrderListResponse.OrderResponse(
                                UUID.randomUUID(),
                                LocalDateTime.of(2025, 10, 1, 13, 20),
                                RefundStatus.PENDING,
                                productList1),
                        new CustomerOrderListResponse.OrderResponse(
                                UUID.randomUUID(),
                                LocalDateTime.of(2025, 10, 15, 16, 45),
                                RefundStatus.PENDING,
                                productList2));

        CustomerOrderListResponse response =
                CustomerOrderListResponse.builder()
                        .nextCursor(nextCursor)
                        .hasNext(true)
                        .orderList(orderList)
                        .build();

        Mockito.when(customerOrderService.getOrderList(any(), anyInt(), any(), any()))
                .thenReturn(response);

        // when & then
        mockMvc.perform(
                        get("/orders/customer")
                                .param("size", "10")
                                .param("startDate", "2025-10-01")
                                .param("endDate", "2025-10-31")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "customer-order-list-get",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(
                                        parameterWithName("cursor")
                                                .description("다음 페이지 커서 (선택)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description(
                                                        "페이지 크기 (필수, 기본값 10 / 허용값: 10, 30, 50)"),
                                        parameterWithName("startDate")
                                                .description("조회 시작일 (선택, 예: 2025-10-01)")
                                                .optional(),
                                        parameterWithName("endDate")
                                                .description("조회 종료일 (선택, 예: 2025-10-31)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),

                                        // Cursor
                                        fieldWithPath("data.nextCursor").description("다음 페이지 커서"),
                                        fieldWithPath("data.hasNext").description("다음 페이지 존재 여부"),

                                        // 주문 리스트
                                        fieldWithPath("data.orderList[].orderId")
                                                .description("주문 ID"),
                                        fieldWithPath("data.orderList[].orderAt")
                                                .description("주문 일시"),
                                        fieldWithPath("data.orderList[].refundStatus")
                                                .description("환불 상태"),

                                        // 주문 상세 상품 리스트
                                        fieldWithPath(
                                                        "data.orderList[].productResponseList[].orderDetailId")
                                                .description("상세 주문 ID"),
                                        fieldWithPath(
                                                        "data.orderList[].productResponseList[].productName")
                                                .description("상품명"),
                                        fieldWithPath(
                                                        "data.orderList[].productResponseList[].optionName")
                                                .description("옵션명"),
                                        fieldWithPath(
                                                        "data.orderList[].productResponseList[].quantity")
                                                .description("수량"),
                                        fieldWithPath(
                                                        "data.orderList[].productResponseList[].price")
                                                .description("가격"),
                                        fieldWithPath(
                                                        "data.orderList[].productResponseList[].orderStatus")
                                                .description("상품 주문 상태"))));
    }

    @Test
    @DisplayName("주문 생성 API")
    void orderCreateTest() throws Exception {
        // given
        UUID productId = UUID.randomUUID();
        UUID optionValueId = UUID.randomUUID();
        UUID addressId = UUID.randomUUID();
        UUID storeId = UUID.randomUUID();
        UUID couponId = UUID.randomUUID();

        CustomerOrderRequest request =
                new CustomerOrderRequest(
                        List.of(
                                new CustomerOrderRequest.ProductSummary(
                                        productId, optionValueId, 2)),
                        addressId,
                        "문 앞에 두세요",
                        List.of(couponId),
                        storeId);

        List<CustomerOrderResponse.ProductSummary> productSummaries =
                List.of(
                        new CustomerOrderResponse.ProductSummary(
                                UUID.randomUUID(), "라운드 반팔 티셔츠", "색상: 블랙 / 사이즈: M", 15000, 2));

        AddressResponse address = new AddressResponse("06241", "서울특별시", "강남구", "테헤란로 1", "101호");

        CustomerOrderResponse response =
                CustomerOrderResponse.builder()
                        .productList(productSummaries)
                        .orderId(UUID.randomUUID())
                        .address(address)
                        .totalProductPrice(30000)
                        .totalPaymentAmount(32000)
                        .totalDiscountAmount(1000)
                        .build();

        Mockito.when(customerOrderService.prepareOrder(any(CustomerOrderRequest.class)))
                .thenReturn(response);

        // when & then
        mockMvc.perform(
                        post("/orders/customer")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "customer-order-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("productList[].productId")
                                                .description("상품 ID"),
                                        fieldWithPath("productList[].optionValueId")
                                                .description("선택 옵션 값 ID"),
                                        fieldWithPath("productList[].quantity")
                                                .description("주문 수량"),
                                        fieldWithPath("deliveryAddressId").description("배송지 주소 ID"),
                                        fieldWithPath("deliveryRequest").description("배송 요청사항"),
                                        fieldWithPath("couponIdList[]").description("적용 쿠폰 ID 목록"),
                                        fieldWithPath("storeId").description("상점 ID")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.orderId").description("주문 ID"),
                                        fieldWithPath("data.address.postalCode")
                                                .description("우편번호"),
                                        fieldWithPath("data.address.city").description("도시"),
                                        fieldWithPath("data.address.sigungu").description("시군구"),
                                        fieldWithPath("data.address.roadname").description("도로명"),
                                        fieldWithPath("data.address.addressDetail")
                                                .description("상세 주소"),
                                        fieldWithPath("data.totalProductPrice")
                                                .description("총 상품 금액"),
                                        fieldWithPath("data.totalPaymentAmount")
                                                .description("총 결제 금액"),
                                        fieldWithPath("data.totalDiscountAmount")
                                                .description("총 할인 금액"),
                                        fieldWithPath("data.productList[].orderDetailId")
                                                .description("상세 주문 ID"),
                                        fieldWithPath("data.productList[].productName")
                                                .description("상품명"),
                                        fieldWithPath("data.productList[].optionName")
                                                .description("옵션명"),
                                        fieldWithPath("data.productList[].price").description("가격"),
                                        fieldWithPath("data.productList[].quantity")
                                                .description("수량"))));
    }
}
