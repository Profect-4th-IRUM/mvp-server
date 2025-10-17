package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    OWNER_UPGRADE_REQUIRED(HttpStatus.CONFLICT, "일반 회원으로 가입된 계정이 존재합니다."),
    MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 회원입니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 형식입니다."),
    INVALID_CONTACT(HttpStatus.BAD_REQUEST, "유효하지 않은 연락처 형식입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "유효한 비밀번호가 아닙니다."),
    DUPLICATED_PASSWORD(HttpStatus.CONFLICT, "변경되는 비밀번호는 기존 비밀번호와 동일할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
