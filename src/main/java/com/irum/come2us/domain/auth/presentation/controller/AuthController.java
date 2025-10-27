package com.irum.come2us.domain.auth.presentation.controller;

import com.irum.come2us.domain.auth.application.service.AuthService;
import com.irum.come2us.domain.auth.presentation.dto.request.MemberLoginRequest;
import com.irum.come2us.domain.auth.presentation.dto.response.MemberLoginResponse;
import com.irum.come2us.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@RequestBody MemberLoginRequest request) {
        log.info("회원 로그인 요청: {}", request);
        MemberLoginResponse responseBody = authService.processMemberLogin(request);
        HttpHeaders cookieHeaders =
                cookieUtil.generateRefreshTokenCookie(responseBody.refreshToken());
        log.info("로그인 성공-토큰 발급 완료: {}", request.email());
        log.trace("access token issued: {}", responseBody.accessToken());
        log.trace("refresh token issued: {}", responseBody.refreshToken());
        return ResponseEntity.status(HttpStatus.OK).headers(cookieHeaders).body(responseBody);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        HttpHeaders headers = authService.processMemberLogout();
        response.addHeader(HttpHeaders.SET_COOKIE, headers.getFirst(HttpHeaders.SET_COOKIE));
        return ResponseEntity.noContent().build();
    }
}
