package com.irum.come2us.domain.auth.presentation.controller;

import com.irum.come2us.domain.auth.application.service.AuthService;
import com.irum.come2us.domain.auth.presentation.dto.request.MemberLoginRequest;
import com.irum.come2us.domain.auth.presentation.dto.response.MemberLoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public MemberLoginResponse login(@RequestBody MemberLoginRequest request) {
        log.info("회원 로그인 요청: {}", request);
        return authService.processMemberLogin(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        HttpHeaders headers = authService.processMemberLogout();
        response.addHeader(HttpHeaders.SET_COOKIE, headers.getFirst(HttpHeaders.SET_COOKIE));
        return ResponseEntity.noContent().build();
    }
}
