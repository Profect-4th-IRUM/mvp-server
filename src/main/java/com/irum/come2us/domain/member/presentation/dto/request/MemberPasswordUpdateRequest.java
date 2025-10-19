package com.irum.come2us.domain.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberPasswordUpdateRequest(
        @NotBlank(message = "기존 비밀번호는 필수 입력값입니다.") String originalPassword,
        @NotBlank(message = "신규 비밀번호는 필수 입력값입니다.") String newPassword) {}
