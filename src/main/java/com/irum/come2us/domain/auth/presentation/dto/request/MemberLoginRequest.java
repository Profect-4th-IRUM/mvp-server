package com.irum.come2us.domain.auth.presentation.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberLoginRequest(
        @NotBlank(message = "이메일은 필수 입력값입니다.") @Email(message = "이메일 형식에 맞지 않습니다.") String email,
        @NotBlank(message = "비밀번호는 필수 입력값입니다.") String password) {}
