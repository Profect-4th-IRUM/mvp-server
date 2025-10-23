package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CartErrorCode implements BaseErrorCode {
    CART_NOT_FOUND(HttpStatus.NOT_FOUND, "장바구니 정보를 찾을 수 없습니다."),
    CART_NOT_MODIFIED(HttpStatus.BAD_REQUEST, "장바구니 수정에 대한 변경된 내용이 없습니다."),
    OPTION_VALUE_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 옵션 정보를 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "장바구니 리소스에 대한 접근 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
