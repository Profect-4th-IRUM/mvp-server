package com.irum.come2us.domain.auth.application.service;

import com.irum.come2us.domain.auth.domain.entity.RefreshToken;
import com.irum.come2us.domain.auth.domain.repository.RefreshTokenRepository;
import com.irum.come2us.domain.auth.presentation.dto.request.AccessTokenDto;
import com.irum.come2us.domain.auth.presentation.dto.request.RefreshTokenDto;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.global.security.jwt.JwtUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public AccessTokenDto createAccessTokenDto(Long memberId, Role authority) {
        return jwtUtil.generateAccessTokenDto(memberId, authority);
    }

    public String createAccessToken(Long memberId, Role authority) {
        return jwtUtil.generateAccessToken(memberId, authority);
    }

    public RefreshTokenDto createRefreshTokenDto(Long memberId) {
        RefreshTokenDto refreshTokenDto = jwtUtil.generateRefreshTokenDto(memberId);
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .memberId(memberId)
                        .token(refreshTokenDto.refreshTokenValue())
                        .ttl(refreshTokenDto.ttl())
                        .build();
        refreshTokenRepository.save(refreshToken);

        return refreshTokenDto;
    }

    public String createRefreshToken(Long memberId) {
        String token = jwtUtil.generateRefreshToken(memberId);
        RefreshToken refreshToken =
                RefreshToken.builder()
                        .memberId(memberId)
                        .token(token)
                        .ttl(jwtUtil.getRefreshTokenExpirationTime())
                        .build();
        refreshTokenRepository.save(refreshToken);

        return token;
    }

    public AccessTokenDto retrieveAccessToken(String accessTokenValue) {
        try {
            return jwtUtil.parseAccessToken(accessTokenValue);
        } catch (Exception e) {
            return null;
        }
    }

    public RefreshTokenDto retrieveRefreshToken(String refreshTokenValue) {
        RefreshTokenDto refreshTokenDto = parseRefreshToken(refreshTokenValue);

        if (refreshTokenDto == null) {
            return null;
        }

        Optional<RefreshToken> refreshToken = getRefreshToken(refreshTokenDto.memberId());

        if (refreshToken.isPresent()
                && refreshTokenDto.refreshTokenValue().equals(refreshToken.get().getToken())) {
            return refreshTokenDto;
        }

        return null;
    }

    private RefreshTokenDto parseRefreshToken(String refreshTokenValue) {
        try {
            return jwtUtil.parseRefreshToken(refreshTokenValue);
        } catch (Exception e) {
            return null;
        }
    }

    private Optional<RefreshToken> getRefreshToken(Long memberId) {
        return refreshTokenRepository.findById(memberId);
    }
}
