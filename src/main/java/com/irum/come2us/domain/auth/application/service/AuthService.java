package com.irum.come2us.domain.auth.application.service;

import com.irum.come2us.domain.auth.presentation.dto.request.MemberLoginRequest;
import com.irum.come2us.domain.auth.presentation.dto.response.MemberLoginResponse;
import com.irum.come2us.domain.member.application.util.MemberValidator;
import com.irum.come2us.domain.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberValidator memberValidator;
    private final JwtTokenService jwtTokenService;

    public MemberLoginResponse processMemberLogin(MemberLoginRequest request) {
        Member member = memberValidator.getMemberByEmail(request.email());
        memberValidator.assertPassword(request.password(), member);
        String accessToken =
                jwtTokenService.createAccessToken(member.getMemberId(), member.getRole());
        String refreshToken = jwtTokenService.createRefreshToken(member.getMemberId());
        return MemberLoginResponse.of(accessToken, refreshToken);
    }
}
