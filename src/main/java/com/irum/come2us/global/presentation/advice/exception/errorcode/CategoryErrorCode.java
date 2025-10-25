package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements BaseErrorCode {
    INVALID_CATEGORY_DEPTH(HttpStatus.BAD_REQUEST, "상품은 최하위 카테고리에만 속할 수 있습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리 정보를 찾을 수 없습니다."),
    CATEGORY_NOT_MODIFIED(HttpStatus.BAD_REQUEST, "카테고리 수정에 대한 변경된 내용이 없습니다."),
    CATEGORY_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 카테고리입니다."),
    CATEGORY_DEPTH_EXCEEDED(HttpStatus.BAD_REQUEST, "카테고리 최대 깊이(3)를 초과할 수 없습니다."); // depth 제한 에러

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
