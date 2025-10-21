package com.irum.come2us.domain.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.member.application.service.MemberService;
import com.irum.come2us.domain.member.presentation.controller.MemberController;
import com.irum.come2us.domain.member.presentation.dto.request.MemberCreateRequest;
import com.irum.come2us.global.config.SecurityTestConfig;
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

@WebMvcTest(MemberController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class MemberControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private MemberService memberService;

    @Autowired private ObjectMapper objectMapper; // Request DTO를 JSON으로 변환

    @TestConfiguration
    static class TestConfig {

        // MemberService 타입의 Bean을 Mockito로 직접 생성하여 반환
        @Bean
        public MemberService memberService() {
            return Mockito.mock(MemberService.class);
        }
    }

    @Test
    @DisplayName("고객 회원가입 API")
    void customerSignupApiTest() throws Exception {

        // Given
        MemberCreateRequest request =
                new MemberCreateRequest(
                        "customer@example.com", "password123!", "회원1", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing().when(memberService).createCustomer(any(MemberCreateRequest.class));

        // When & Then
        mockMvc.perform(
                        post("/members/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "member-signup",
                                requestFields(
                                        fieldWithPath("email").description("가입할 이메일 (아이디)"),
                                        fieldWithPath("password").description("가입할 비밀번호"),
                                        fieldWithPath("name").description("회원 이름"),
                                        fieldWithPath("contact")
                                                .description("회원 연락처 ex) 010-1234-5678"))));
    }
}
