package com.irum.come2us.domain.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.irum.come2us.domain.auth.application.service.AuthService;
import com.irum.come2us.domain.auth.application.service.JwtTokenService;
import com.irum.come2us.domain.auth.presentation.controller.AuthController;
import com.irum.come2us.domain.auth.presentation.dto.request.MemberLoginRequest;
import com.irum.come2us.domain.auth.presentation.dto.response.MemberLoginResponse;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.global.config.SecurityTestConfig;
import com.irum.come2us.global.config.TestConfig;
import com.irum.come2us.global.util.CookieUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs
@Import({SecurityTestConfig.class, TestConfig.class})
public class AuthControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired AuthService authService;
    @Autowired CookieUtil cookieUtil;
    @Autowired JwtTokenService jwtTokenService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원 로그인 API 테스트")
    void memberLoginApiTest() throws Exception {
        String mockAccessToken = "access-token";
        String mockRefreshToken = "refresh-token";

        MemberLoginRequest request = new MemberLoginRequest("customer@example.com", "password123!");

        MemberLoginResponse response = new MemberLoginResponse(mockAccessToken, mockRefreshToken);
        String requestJson = objectMapper.writeValueAsString(request);

        HttpHeaders mockHeaders = new HttpHeaders();
        mockHeaders.add(
                HttpHeaders.SET_COOKIE,
                "refreshToken=" + mockRefreshToken + "; Path=/; Secure; HttpOnly; SameSite=None");

        when(jwtTokenService.createAccessToken(anyLong(), any(Role.class)))
                .thenReturn(mockAccessToken);
        when(cookieUtil.generateRefreshTokenCookie(any(String.class))).thenReturn(mockHeaders);
        when(jwtTokenService.createRefreshToken(anyLong())).thenReturn(mockRefreshToken);
        when(authService.processMemberLogin(any(MemberLoginRequest.class))).thenReturn(response);

        mockMvc.perform(
                        post("/auth/login")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.OK.value()))
                .andExpect(jsonPath("$.data.accessToken").value(response.accessToken()))
                .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", mockRefreshToken))
                .andDo(
                        document(
                                "member-login",
                                requestFields(
                                        fieldWithPath("email").description("가입할 이메일 (아이디)"),
                                        fieldWithPath("password").description("가입할 비밀번호")),
                                responseFields(
                                        fieldWithPath("success").description("true"),
                                        fieldWithPath("status").description("200"),
                                        fieldWithPath("timestamp").description("응답 시간"),
                                        fieldWithPath("data.accessToken")
                                                .description("가입할 이메일 (아이디)")),
                                responseHeaders(
                                        headerWithName(HttpHeaders.SET_COOKIE)
                                                .description(
                                                        "Refresh Token 쿠키 (이름: `refreshToken`). <br/> "
                                                                + "정책: **HttpOnly**, **Secure**, Path=/, SameSite=None"))));
    }

    @Test
    @DisplayName("회원 로그아웃 API 테스트")
    void memberLogoutApiTest() throws Exception {

        String deletedCookieValue = ""; // 삭제 시 쿠키 값 = 빈 문자열
        HttpHeaders deleteHeaders = new HttpHeaders();
        deleteHeaders.add(
                HttpHeaders.SET_COOKIE,
                "refreshToken="
                        + deletedCookieValue
                        + "; Max-Age=0; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/; Secure; HttpOnly; SameSite=None");

        when(authService.processMemberLogout()).thenReturn(deleteHeaders);

        // When & Then
        mockMvc.perform(
                        post("/auth/logout")
                                .with(csrf())
                                // 인증 정보 추가(Authorization)
                                .with(user("1").roles("CUSTOMER")))
                .andExpect(status().isNoContent()) // 204 No Content 기대
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(HttpStatus.NO_CONTENT.value()))
                .andExpect(cookie().exists("refreshToken"))
                .andExpect(cookie().value("refreshToken", ""))
                .andExpect(cookie().maxAge("refreshToken", 0))
                .andDo(
                        document(
                                "member-logout",
                                responseHeaders(
                                        headerWithName(HttpHeaders.SET_COOKIE)
                                                .description(
                                                        "Refresh Token 쿠키를 삭제하기 위한 헤더. <br/> "
                                                                + "정책: **Max-Age=0** 설정으로 즉시 만료 및 삭제됩니다."))));
    }
}
