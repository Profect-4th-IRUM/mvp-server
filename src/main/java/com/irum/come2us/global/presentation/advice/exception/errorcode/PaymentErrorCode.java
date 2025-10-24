package com.irum.come2us.global.presentation.advice.exception.errorcode;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements BaseErrorCode {
    PAYMENT_ERROR(HttpStatus.FORBIDDEN, "토스 페이먼츠 결제 승인 실패"),
    ;

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
