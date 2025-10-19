package com.irum.come2us.domain.auth.presentation.controller;

import com.irum.come2us.domain.auth.application.service.AuthService;
import com.irum.come2us.domain.auth.presentation.dto.request.MemberLoginRequest;
import com.irum.come2us.domain.auth.presentation.dto.response.MemberLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
