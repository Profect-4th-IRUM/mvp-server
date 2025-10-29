package com.irum.come2us.domain.payment;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.payment.application.service.PaymentService;
import com.irum.come2us.domain.payment.presentation.controller.PaymentController;
import com.irum.come2us.domain.payment.presentation.dto.request.PaymentRequest;
import com.irum.come2us.domain.payment.presentation.dto.response.PaymentResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
import com.irum.come2us.global.config.TestConfig;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, TestConfig.class})
public class PaymentControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private PaymentService paymentService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("결제 등록 API")
    void paymentCreateApiTest() throws Exception {
        UUID orderId = UUID.randomUUID();
        String orderNum = "ORD-777777";
        int totalAmount = 10000;
        PaymentRequest request = new PaymentRequest("tossOrderId", "tosspaymentkey", orderId);
        PaymentResponse response = new PaymentResponse(orderNum, totalAmount);

        Mockito.when(paymentService.createPayment(request)).thenReturn(response);

        mockMvc.perform(
                        post("/payment")
                                .with(csrf())
                                .with(user("100").roles("CUSTOMER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.orderNum").value(orderNum))
                .andExpect(jsonPath("$.data.totalAmount").value(totalAmount))
                .andDo(
                        document(
                                "payment-create",
                                requestFields(
                                        fieldWithPath("tossOrderId")
                                                .description("토스에서 발급해주는 orderId"),
                                        fieldWithPath("tossPaymentKey")
                                                .description("토스에서 발급해주는 paymentKey"),
                                        fieldWithPath("orderId").description("결제하려는 주문 id")),
                                responseFields(
                                        fieldWithPath("success").description("성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp")
                                                .description("응답 생성 시각 (ISO-8601)"),
                                        subsectionWithPath("data").description("응답 데이터 객체")),
                                responseFields(
                                        beneathPath("data").withSubsectionId("data"),
                                        fieldWithPath("orderNum").description("주문 번호"),
                                        fieldWithPath("totalAmount").description("실 결제 금액"))));
    }
}
