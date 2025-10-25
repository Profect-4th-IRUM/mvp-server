package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RefundErrorCode implements BaseErrorCode {
    REFUND_NOT_FOUND(HttpStatus.NOT_FOUND, "환불 정보를 찾을 수 없습니다."),
    REFUND_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 해당 주문에 대한 환불이 존재합니다."),
    // OrderErrorCode 충돌 회피 -> 추후 OrderErrorCode 생성시 업데이트 예정;
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "주문을 찾을 수 없습니다"),
    REFUND_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "해당 주문에 대한 취소가 불가능합니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
