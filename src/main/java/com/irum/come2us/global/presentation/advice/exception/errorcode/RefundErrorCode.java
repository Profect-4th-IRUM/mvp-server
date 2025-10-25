package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RefundErrorCode implements BaseErrorCode {
    // OrderErrorCode 충돌 회피 -> 추후 OrderErrorCode 생성시 업데이트 예정;
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다"),
    REFUND_NOT_FOUND(HttpStatus.NOT_FOUND, "환불을 찾을 수 없습니다");
    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
