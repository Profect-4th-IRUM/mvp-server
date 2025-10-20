package com.irum.come2us.global.presentation.advice.exception.errorcode;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OrderErrorCode implements BaseErrorCode {
    ORDER_DETAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "주문 상세를 찾을 수 없습니다."),


    ;


    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
