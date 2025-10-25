package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DeliveryPolicyErrorCode implements BaseErrorCode {
    ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 상점의 배송비 정책이 존재합니다."),
    DELIVERY_POLICY_NOT_FOUND(HttpStatus.NOT_FOUND, "배송비 정책을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
