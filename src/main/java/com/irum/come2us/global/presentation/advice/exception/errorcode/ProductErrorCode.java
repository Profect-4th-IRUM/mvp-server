package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductErrorCode implements BaseErrorCode {
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 정보를 찾을 수 없습니다."),
    PRODUCT_NOT_MODIFIED(HttpStatus.BAD_REQUEST, "상품 수정에 대한 변경된 내용이 없습니다."),
    PRODUCT_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 상품입니다."),
    PRODUCT_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "재고가 부족합니다"),

    PRODUCT_OPTION_VALUE_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 옵션 정보를 찾을 수 없습니다."),

    OPTION_GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 옵션 그룹을 찾을 수 없습니다."),
    OPTION_VALUE_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 옵션 값을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
