package com.irum.come2us.domain.deliverypolicy;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.deliverypolicy.application.service.DeliveryPolicyService;
import com.irum.come2us.domain.deliverypolicy.presentation.controller.DeliveryPolicyController;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.request.DeliveryPolicyCreateRequest;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.request.DeliveryPolicyInfoUpdateRequest;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.response.DeliveryPolicyInfoResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
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

@WebMvcTest(DeliveryPolicyController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class DeliveryPolicyControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private DeliveryPolicyService deliveryPolicyService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public DeliveryPolicyService deliveryPolicyService() {
            return Mockito.mock(DeliveryPolicyService.class);
        }
    }

    // 배송비 정책 생성
    @Test
    @DisplayName("배송비 정책 생성 API")
    void createDeliveryPolicyTest() throws Exception {
        DeliveryPolicyCreateRequest request = new DeliveryPolicyCreateRequest(2500, 2, 15000);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/delivery-policies")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "delivery-policy-create",
                                requestFields(
                                        fieldWithPath("defaultDeliveryFee").description("기본 배송비"),
                                        fieldWithPath("minQuantity").description("최소 주문 수량"),
                                        fieldWithPath("minAmount").description("최소 주문 금액"))));
    }

    // 배송비 정책 조회
    @Test
    @DisplayName("배송비 정책 조회 API")
    void getDeliveryPolicyTest() throws Exception {
        UUID id = UUID.randomUUID();
        DeliveryPolicyInfoResponse response = new DeliveryPolicyInfoResponse(id, 3000, 2, 10000);
        Mockito.when(deliveryPolicyService.findDeliveryPolicy(id)).thenReturn(response);

        mockMvc.perform(get("/delivery-policies/{id}", id).with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(id.toString()))
                .andExpect(jsonPath("$.data.defaultDeliveryFee").value(3000))
                .andDo(
                        document(
                                "delivery-policy-detail",
                                pathParameters(
                                        parameterWithName("id").description("조회할 배송비 정책 ID")),
                                responseFields(
                                        fieldWithPath("success").description("API 성공 여부"),
                                        fieldWithPath("status").description("HTTP 상태 코드"),
                                        fieldWithPath("timestamp").description("응답 시각"),
                                        fieldWithPath("data.id").description("정책 ID"),
                                        fieldWithPath("data.defaultDeliveryFee")
                                                .description("기본 배송비"),
                                        fieldWithPath("data.minQuantity").description("최소 주문 수량"),
                                        fieldWithPath("data.minAmount").description("최소 주문 금액"))));
    }

    // 배송비 정책 수정
    @Test
    @DisplayName("배송비 정책 수정 API")
    void updateDeliveryPolicyTest() throws Exception {
        UUID id = UUID.randomUUID();
        DeliveryPolicyInfoUpdateRequest request =
                new DeliveryPolicyInfoUpdateRequest(2000, 3, 12000);
        String json = objectMapper.writeValueAsString(request);

        Mockito.doNothing().when(deliveryPolicyService).changeDeliveryPolicy(any(), any());

        mockMvc.perform(
                        patch("/delivery-policies/{id}", id)
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "delivery-policy-update",
                                pathParameters(
                                        parameterWithName("id").description("수정할 배송비 정책 ID")),
                                requestFields(
                                        fieldWithPath("defaultDeliveryFee").description("기본 배송비"),
                                        fieldWithPath("minQuantity").description("최소 주문 수량"),
                                        fieldWithPath("minAmount").description("최소 주문 금액"))));
    }

    // 배송비 정책 삭제
    @Test
    @DisplayName("배송비 정책 삭제 API")
    void deleteDeliveryPolicyTest() throws Exception {
        UUID id = UUID.randomUUID();
        Mockito.doNothing().when(deliveryPolicyService).withdrawDeliveryPolicy(id);

        mockMvc.perform(delete("/delivery-policies/{id}", id).with(csrf().asHeader()))
                .andExpect(status().isNoContent())
                .andDo(
                        document(
                                "delivery-policy-delete",
                                pathParameters(
                                        parameterWithName("id").description("삭제할 배송비 정책 ID"))));
    }

    // 실패 케이스 - 배송비 0 미만
    @Test
    @DisplayName("배송비 정책 생성 실패 - 기본 배송비 음수")
    void createDeliveryPolicyFail_InvalidFee() throws Exception {
        DeliveryPolicyCreateRequest request = new DeliveryPolicyCreateRequest(-1, 2, 10000);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/delivery-policies")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("기본 배송비는 필수 입력값입니다."))
                .andDo(
                        document(
                                "delivery-policy-create-fail-invalid-fee",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메시지"))));
    }

    // 실패 케이스 - 최소 주문 수량 0
    @Test
    @DisplayName("배송비 정책 생성 실패 - 최소 주문 수량 1 미만으로 입력.")
    void createDeliveryPolicyFail_InvalidQuantity() throws Exception {
        DeliveryPolicyCreateRequest request = new DeliveryPolicyCreateRequest(2000, 0, 15000);
        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/delivery-policies")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("최소 주문 수량을 입력해주세요."))
                .andDo(
                        document(
                                "delivery-policy-create-fail-invalid-quantity",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메시지"))));
    }

    @Test
    @DisplayName("배송비 정책 생성 실패 - 최소 주문 금액 0 미만으로 입력.")
    void createDeliveryPolicyFail_InvalidAmount() throws Exception {
        // given
        DeliveryPolicyCreateRequest request = new DeliveryPolicyCreateRequest(2000, 2, -100);
        String json = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(
                        post("/delivery-policies")
                                .with(csrf().asHeader())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("최소 주문 금액을 입력해수세요."))
                .andDo(
                        document(
                                "delivery-policy-create-fail-invalid-amount",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메시지"))));
    }
}
