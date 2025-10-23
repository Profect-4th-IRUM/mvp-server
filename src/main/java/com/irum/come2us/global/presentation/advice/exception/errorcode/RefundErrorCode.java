package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RefundErrorCode implements BaseErrorCode {
    REFUND_NOT_FOUND(HttpStatus.NOT_FOUND, "환불 정보를 찾을 수 없습니다."),
    REFUND_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 해당 주문에 대한 환불이 존재합니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
