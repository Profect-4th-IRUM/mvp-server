package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DiscountErrorCode implements BaseErrorCode {
    DISCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 할인 정보를 찾을 수 없습니다."),
    DUPLICATE_DISCOUNT(HttpStatus.BAD_REQUEST, "동일한 상품에 대한 할인은 중복 생성될 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
