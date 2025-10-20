package com.irum.come2us.domain.auth.presentation.dto.request;

import com.irum.come2us.domain.member.domain.entity.enums.Role;

public record AccessTokenDto(Long memberId, Role role, String accessTokenValue) {
    public static AccessTokenDto of(Long memberId, Role role, String accessTokenValue) {
        return new AccessTokenDto(memberId, role, accessTokenValue);
    }
}
