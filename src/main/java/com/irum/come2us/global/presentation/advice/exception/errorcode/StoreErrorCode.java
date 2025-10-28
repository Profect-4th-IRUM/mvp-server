package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StoreErrorCode implements BaseErrorCode {
    //    INVALID_MEMBER_ROLE(HttpStatus.FORBIDDEN, "상점을 생성할 권한이 없습니다."),
    STORE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 상점을 보유하고 있습니다."),
    BUSINESS_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "사업자등록번호가 이미 존재합니다."),
    TELEMARKETING_NUMBER_DUPLICATED(HttpStatus.CONFLICT, "통신판매업번호가 이미 존재합니다."),
    INVALID_BUSINESS_REGISTRATION_NUMBER(HttpStatus.BAD_REQUEST, "유효하지 않은 사업자등록번호 형식입니다."),
    INVALID_TELEMARKETING_REGISTRATION_NUMBER(HttpStatus.BAD_REQUEST, "유효하지 않은 통신판매업번호 형식입니다."),
    INVALID_CONTACT(HttpStatus.BAD_REQUEST, "유효하지 않은 연락처 형식입니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 상점을 찾을 수 없습니다."),
    UNAUTHORIZED_STORE_ACCESS(HttpStatus.FORBIDDEN, "본인의 상점이 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
