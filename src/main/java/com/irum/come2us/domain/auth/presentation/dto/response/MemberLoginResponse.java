package com.irum.come2us.domain.auth.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record MemberLoginResponse(String accessToken, @JsonIgnore String refreshToken) {
    public static MemberLoginResponse of(String accessToken, String refreshToken) {
        return new MemberLoginResponse(accessToken, refreshToken);
    }
}
