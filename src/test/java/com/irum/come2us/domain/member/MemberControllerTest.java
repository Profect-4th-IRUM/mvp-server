package com.irum.come2us.domain.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.member.application.service.MemberService;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.domain.member.presentation.controller.MemberController;
import com.irum.come2us.domain.member.presentation.dto.request.MemberCreateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberInfoUpdateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberPasswordUpdateRequest;
import com.irum.come2us.domain.member.presentation.dto.response.MemberInfoResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MemberController.class)
@AutoConfigureRestDocs
@Import(SecurityTestConfig.class)
public class MemberControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private MemberService memberService;
    @Autowired private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {

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
                        "customer@example.com", "password123!", "고객1", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing().when(memberService).createCustomer(any(MemberCreateRequest.class));

        // When & Then
        mockMvc.perform(
                        post("/members/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
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

    @Test
    @DisplayName("판매자 회원가입 API")
    void ownerSignupApiTest() throws Exception {

        // Given
        MemberCreateRequest request =
                new MemberCreateRequest(
                        "owner@example.com", "password123!", "판매자1", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing().when(memberService).createCustomer(any(MemberCreateRequest.class));

        // When & Then
        mockMvc.perform(
                        post("/members/owner-signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andDo(
                        document(
                                "owner-signup",
                                requestFields(
                                        fieldWithPath("email").description("가입할 이메일 (아이디)"),
                                        fieldWithPath("password").description("가입할 비밀번호"),
                                        fieldWithPath("name").description("회원 이름"),
                                        fieldWithPath("contact")
                                                .description("회원 연락처 ex) 010-1234-5678"))));
    }

    @Test
    @DisplayName("회원 정보 조회 API")
    void memberInfoGetApiTest() throws Exception {
        MemberInfoResponse response =
                new MemberInfoResponse(
                        1L, "customer@example.com", "고객1", "010-1234-5678", Role.CUSTOMER);
        when(memberService.findMemberInfo()).thenReturn(response);
        mockMvc.perform(get("/members/info").with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.memberId").value(response.memberId()))
                .andExpect(jsonPath("$.data.memberId").value(response.memberId()))
                .andExpect(jsonPath("$.data.name").value(response.name()))
                .andExpect(jsonPath("$.data.contact").value(response.contact()))
                .andExpect(jsonPath("$.data.role").value(response.role().name()))
                .andDo(
                        document(
                                "member-info-get",
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp")
                                                .description("2025-10-25T11:31:40.108465"),
                                        fieldWithPath("data.memberId").description("회원 식별 아이디"),
                                        fieldWithPath("data.email").description("이메일 (아이디)"),
                                        fieldWithPath("data.name").description("회원 이름"),
                                        fieldWithPath("data.contact").description("회원 이름"),
                                        fieldWithPath("data.role")
                                                .description(
                                                        "사용자 권한 ex) " + Role.CUSTOMER.name()))));
    }

    @Test
    @DisplayName("회원 정보 수정 API")
    void memberInfoUpdateApiTest() throws Exception {
        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest("판매자1", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);
        doNothing()
                .when(memberService)
                .changeMemberNameAndContact(any(MemberInfoUpdateRequest.class));
        mockMvc.perform(
                        patch("/members/info")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "member-info-update",
                                requestFields(
                                        fieldWithPath("name").description("회원 이름"),
                                        fieldWithPath("contact")
                                                .description("회원 연락처 ex) 010-1234-5678"))));
    }

    @Test
    @DisplayName("회원 비밀번호 변경 API")
    void memberPasswordUpdateApiTest() throws Exception {
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("original", "new");
        String requestJson = objectMapper.writeValueAsString(request);
        doNothing()
                .when(memberService)
                .changeMemberPassword(any(MemberPasswordUpdateRequest.class));
        mockMvc.perform(
                        patch("/members/password")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "member-password-update",
                                requestFields(
                                        fieldWithPath("originalPassword").description("변경 전 비밀번호"),
                                        fieldWithPath("newPassword").description("변경할 비밀번호"))));
    }

    @Test
    @DisplayName("회원 권한 변경 API")
    void memberRoleUpdateTest() throws Exception {
        doNothing().when(memberService).changeCustomerRoleToOwner();
        mockMvc.perform(patch("/members/role").with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(document("member-role-update"));
    }

    @Test
    @DisplayName("회원 탈퇴 API")
    void memberDeleteApiTest() throws Exception {
        doNothing().when(memberService).withdrawCustomer();
        mockMvc.perform(delete("/members").with(csrf()))
                .andExpect(status().is2xxSuccessful())
                .andDo(document("member-delete"));
    }

    @Test
    @DisplayName("고객 회원가입 실패 - 이메일 형식 오류")
    void customerSignupApiTest_Fail_InvalidEmailFormat() throws Exception {
        MemberCreateRequest request =
                new MemberCreateRequest(
                        "not-an-email", // "@Email" 어노테이션 위반
                        "password123!",
                        "고객1",
                        "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        post("/members/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("이메일 형식에 맞지 않습니다."))
                .andDo(
                        document(
                                "member-signup-fail-invalid-email",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    @Test
    @DisplayName("고객 회원가입 실패 - 비밀번호 누락")
    void customerSignupApiTest_Fail_BlankPassword() throws Exception {
        MemberCreateRequest request =
                new MemberCreateRequest("customer@example.com", "", "고객1", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/members/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("비밀번호는 필수 입력값입니다."))
                .andDo(
                        document(
                                "member-signup-fail-invalid-password",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    @Test
    @DisplayName("고객 회원가입 실패 - 회원 이름 누락")
    void customerSignupApiTest_Fail_BlankName() throws Exception {
        MemberCreateRequest request =
                new MemberCreateRequest("customer@example.com", "password123", "", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/members/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("이름은 필수 입력값입니다."))
                .andDo(
                        document(
                                "member-signup-fail-invalid-name",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    @Test
    @DisplayName("고객 회원가입 실패 - 회원 연락처 누락")
    void customerSignupApiTest_Fail_BlankContact() throws Exception {
        MemberCreateRequest request =
                new MemberCreateRequest("customer@example.com", "password123", "고객1", "");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/members/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("연락처는 필수 입력값입니다."))
                .andDo(
                        document(
                                "member-signup-fail-invalid-contact",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }
}
