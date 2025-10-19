package com.irum.come2us.domain.member.domain.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    CUSTOMER("ROLE_CUSTOMER"),
    OWNER("ROLE_OWNER"),
    MANAGER("ROLE_MANAGER"),
    MASTER("ROLE_MASTER");

    private final String authority;
}
