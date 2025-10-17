package com.irum.come2us.domain.member.presentation.dto.request;

public record MemberCreateRequest(String email, String password, String name, String contact) {}
