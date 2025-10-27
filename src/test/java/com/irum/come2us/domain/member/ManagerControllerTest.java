package com.irum.come2us.domain.member;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.member.application.service.ManagerService;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.domain.member.presentation.controller.ManagerController;
import com.irum.come2us.domain.member.presentation.dto.request.MemberCreateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberInfoUpdateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberPasswordUpdateRequest;
import com.irum.come2us.domain.member.presentation.dto.response.MemberInfoListResponse;
import com.irum.come2us.domain.member.presentation.dto.response.MemberInfoResponse;
import com.irum.come2us.global.config.SecurityTestConfig;
import com.irum.come2us.global.config.TestConfig;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ManagerController.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, TestConfig.class})
public class ManagerControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ManagerService managerService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("매니저 회원가입 API")
    void managerSignupApiTest() throws Exception {

        MemberCreateRequest request =
                new MemberCreateRequest(
                        "manager@example.com", "password123!", "매니저1", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);

        doNothing().when(managerService).createManager(any(MemberCreateRequest.class));

        mockMvc.perform(
                        post("/managers/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.CREATED.value()))
                .andDo(
                        document(
                                "manager-signup",
                                requestFields(
                                        fieldWithPath("email").description("가입할 이메일 (아이디)"),
                                        fieldWithPath("password").description("가입할 비밀번호"),
                                        fieldWithPath("name").description("매니저 이름"),
                                        fieldWithPath("contact")
                                                .description("회원 연락처 ex) 010-1234-5678"))));
    }

    @Test
    @DisplayName("매니저 정보 조회 API")
    void managerInfoGetApiTest() throws Exception {

        long managerId = 1L;
        MemberInfoResponse response =
                new MemberInfoResponse(
                        managerId, "manager@example.com", "매니저1", "010-1234-5678", Role.MANAGER);
        when(managerService.findManagerInfo(eq(managerId))).thenReturn(response);

        mockMvc.perform(get("/managers/{managerId}/info", managerId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.memberId").value(response.memberId()))
                .andExpect(jsonPath("$.data.email").value(response.email()))
                .andExpect(jsonPath("$.data.name").value(response.name()))
                .andExpect(jsonPath("$.data.contact").value(response.contact()))
                .andExpect(jsonPath("$.data.role").value(response.role().name()))
                .andDo(
                        document(
                                "manager-info-get",
                                pathParameters(
                                        parameterWithName("managerId").description("조회할 매니저 ID")),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.memberId").description("회원 식별 아이디"),
                                        fieldWithPath("data.email").description("이메일 (아이디)"),
                                        fieldWithPath("data.name").description("회원 이름"),
                                        fieldWithPath("data.contact").description("회원 연락처"),
                                        fieldWithPath("data.role")
                                                .description("사용자 권한 (MANAGER)"))));
    }

    @Test
    @DisplayName("매니저 목록 조회 API")
    void managerInfoListGetApiTest() throws Exception {
        List<MemberInfoResponse> managerList =
                List.of(
                        new MemberInfoResponse(
                                2L, "manager2@example.com", "매니저2", "010-2222-2222", Role.MANAGER),
                        new MemberInfoResponse(
                                1L, "manager1@example.com", "매니저1", "010-1111-1111", Role.MANAGER));

        MemberInfoListResponse response = new MemberInfoListResponse(managerList, null, false);

        when(managerService.findManagerInfoList(any(), eq(10))).thenReturn(response);

        mockMvc.perform(get("/managers").param("size", "10").with(csrf().asHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.nextCursor").isEmpty())
                .andExpect(jsonPath("$.data.memberInfoList[0].memberId").value(2L))
                .andDo(
                        document(
                                "manager-list-get",
                                queryParameters(
                                        parameterWithName("lastId")
                                                .description("마지막으로 조회된 매니저 ID (첫 페이지는 생략)")
                                                .optional(),
                                        parameterWithName("size")
                                                .description("조회할 페이지 크기 (기본값 10)")
                                                .optional()),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.memberInfoList[]")
                                                .description("매니저 정보 목록"),
                                        fieldWithPath("data.memberInfoList[].memberId")
                                                .description("회원 식별 아이디"),
                                        fieldWithPath("data.memberInfoList[].email")
                                                .description("이메일 (아이디)"),
                                        fieldWithPath("data.memberInfoList[].name")
                                                .description("회원 이름"),
                                        fieldWithPath("data.memberInfoList[].contact")
                                                .description("회원 연락처"),
                                        fieldWithPath("data.memberInfoList[].role")
                                                .description("사용자 권한"),
                                        fieldWithPath("data.nextCursor")
                                                .description(
                                                        "다음 페이지 조회를 위한 커서 ID (다음 페이지 없으면 null)"),
                                        fieldWithPath("data.hasNext")
                                                .description("다음 페이지 존재 여부"))));
    }

    @Test
    @DisplayName("매니저 정보 수정 API")
    void managerInfoUpdateApiTest() throws Exception {

        long managerId = 1L;
        MemberInfoUpdateRequest request = new MemberInfoUpdateRequest("매니저1 수정", "010-9999-9999");
        String requestJson = objectMapper.writeValueAsString(request);
        doNothing()
                .when(managerService)
                .changeManagerNameAndContact(eq(managerId), any(MemberInfoUpdateRequest.class));

        mockMvc.perform(
                        patch("/managers/{managerId}/info", managerId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "manager-info-update",
                                pathParameters(
                                        parameterWithName("managerId").description("수정할 매니저 ID")),
                                requestFields(
                                        fieldWithPath("name").description("변경할 회원 이름"),
                                        fieldWithPath("contact")
                                                .description("변경할 회원 연락처 ex) 010-1234-5678"))));
    }

    @Test
    @DisplayName("매니저 비밀번호 변경 API")
    void managerPasswordUpdateApiTest() throws Exception {
        // Given
        long managerId = 1L;
        MemberPasswordUpdateRequest request = new MemberPasswordUpdateRequest("original", "new");
        String requestJson = objectMapper.writeValueAsString(request);
        doNothing()
                .when(managerService)
                .changeManagerPassword(eq(managerId), any(MemberPasswordUpdateRequest.class));

        // When & Then
        mockMvc.perform(
                        patch("/managers/{managerId}/password", managerId)
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "manager-password-update",
                                pathParameters(
                                        parameterWithName("managerId").description("수정할 매니저 ID")),
                                requestFields(
                                        fieldWithPath("originalPassword").description("변경 전 비밀번호"),
                                        fieldWithPath("newPassword").description("변경할 비밀번호"))));
    }

    @Test
    @DisplayName("매니저 삭제 API")
    void managerDeleteApiTest() throws Exception {
        // Given
        long managerId = 1L;
        doNothing().when(managerService).removeManager(eq(managerId));

        // When & Then
        mockMvc.perform(delete("/managers/{managerId}", managerId).with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andDo(
                        document(
                                "manager-delete",
                                pathParameters(
                                        parameterWithName("managerId").description("삭제할 매니저 ID"))));
    }

    @Test
    @DisplayName("매니저 회원가입 실패 - 이메일 형식 오류")
    void managerSignupApiTest_Fail_InvalidEmailFormat() throws Exception {
        MemberCreateRequest request =
                new MemberCreateRequest("not-an-email", "password123!", "매니저1", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                        post("/managers/signup")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("이메일 형식에 맞지 않습니다."))
                .andDo(
                        document(
                                "manager-signup-fail-invalid-email",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    @Test
    @DisplayName("매니저 회원가입 실패 - 비밀번호 누락")
    void managerSignupApiTest_Fail_BlankPassword() throws Exception {
        MemberCreateRequest request =
                new MemberCreateRequest("manager@example.com", "", "매니저1", "010-1234-5678");
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                        post("/managers/signup") // URL 변경
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.data.errorClassName").value("MethodArgumentNotValidException"))
                .andExpect(jsonPath("$.data.message").value("비밀번호는 필수 입력값입니다."))
                .andDo(
                        document(
                                "manager-signup-fail-invalid-password",
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("400"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName").description("에러 종류"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    @Test
    @DisplayName("매니저 정보 조회 실패 - 존재하지 않는 매니저")
    void managerInfoGetApiTest_Fail_ManagerNotFound() throws Exception {
        long notFoundId = 999L;
        doThrow(new CommonException(MemberErrorCode.MEMBER_NOT_FOUND))
                .when(managerService)
                .findManagerInfo(eq(notFoundId));

        mockMvc.perform(get("/managers/{notFoundId}/info", notFoundId).with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(
                        jsonPath("$.status")
                                .value(MemberErrorCode.MEMBER_NOT_FOUND.getHttpStatus().value()))
                .andExpect(
                        jsonPath("$.data.errorClassName")
                                .value(MemberErrorCode.MEMBER_NOT_FOUND.name()))
                .andExpect(
                        jsonPath("$.data.message")
                                .value(MemberErrorCode.MEMBER_NOT_FOUND.getMessage()))
                .andDo(
                        document(
                                "manager-info-get-fail-not-found",
                                pathParameters(
                                        parameterWithName("notFoundId").description("조회할 매니저 ID")),
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("에러 상태 코드 (404)"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName")
                                                .description("에러 코드 (MEMBER_NOT_FOUND)"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }

    @Test
    @DisplayName("매니저 삭제 실패 - 매니저가 스스로 계정 삭제 시도")
    void managerDeleteApiTest_ManagerCannotWithdraw() throws Exception {
        long memberId = 2L;
        doThrow(new CommonException(MemberErrorCode.MANAGER_CANNOT_WITHDRAW))
                .when(managerService)
                .removeManager(eq(memberId));

        mockMvc.perform(delete("/managers/{memberId}", memberId).with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(
                        jsonPath("$.status")
                                .value(
                                        MemberErrorCode.MANAGER_CANNOT_WITHDRAW
                                                .getHttpStatus()
                                                .value()))
                .andExpect(
                        jsonPath("$.data.errorClassName")
                                .value(MemberErrorCode.MANAGER_CANNOT_WITHDRAW.name()))
                .andExpect(
                        jsonPath("$.data.message")
                                .value(MemberErrorCode.MANAGER_CANNOT_WITHDRAW.getMessage()))
                .andDo(
                        document(
                                "manager-delete-fail-self-delete",
                                pathParameters(
                                        parameterWithName("memberId").description("삭제 시도할 ID")),
                                responseFields(
                                        fieldWithPath("success").description("false"),
                                        fieldWithPath("status").description("에러 상태 코드 (403)"),
                                        fieldWithPath("timestamp").description("에러 발생 시간"),
                                        fieldWithPath("data.errorClassName")
                                                .description("에러 코드 (NOT_A_MANAGER)"),
                                        fieldWithPath("data.message").description("에러 메세지"))));
    }
}
