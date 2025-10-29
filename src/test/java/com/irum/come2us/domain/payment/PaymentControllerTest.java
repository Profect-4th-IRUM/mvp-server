package com.irum.come2us.domain.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.payment.application.service.PaymentService;
import com.irum.come2us.domain.payment.presentation.controller.PaymentController;
import com.irum.come2us.domain.payment.presentation.dto.request.PaymentRequest;
import com.irum.come2us.domain.payment.presentation.dto.response.PaymentResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
import com.irum.come2us.global.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc
@Import({SecurityTestConfig.class, TestConfig.class})
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PaymentService paymentService;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("결제 등록 API")
    void paymentCreateApiTest() throws Exception {
        UUID orderId = UUID.randomUUID();
        String orderNum = "ORD-777777";
        int totalAmount = 10000;
        PaymentRequest request = new PaymentRequest("tossOrderId", "tosspaymentkey", orderId);
        PaymentResponse response = new PaymentResponse(orderNum, totalAmount);

        Mockito.when(paymentService.createPayment(request)).thenReturn(response);

        mockMvc.perform(post("/payment")
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
                                        fieldWithPath("tossOrderId").description("토스에서 발급해주는 orderId"),
                                        fieldWithPath("tossPaymentKey").description("토스에서 발급해주는 paymentKey"),
                                        fieldWithPath("orderId").description("결제하려는 주문 id"))));

    }
}
