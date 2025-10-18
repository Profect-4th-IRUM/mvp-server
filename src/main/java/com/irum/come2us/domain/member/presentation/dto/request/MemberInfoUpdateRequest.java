package com.irum.come2us.domain.member.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberInfoUpdateRequest(
        @NotBlank(message = "이름은 필수 입력값입니다.") String name,
        @NotBlank(message = "연락처는 필수 입력값입니다.") String contact) {}
