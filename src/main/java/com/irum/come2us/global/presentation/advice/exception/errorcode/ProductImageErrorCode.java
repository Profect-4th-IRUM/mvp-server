package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ProductImageErrorCode implements BaseErrorCode {
    PRODUCT_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 이미지를 찾을 수 없습니다."),
    INVALID_PRODUCT_IMAGE_RELATION(HttpStatus.BAD_REQUEST, "요청한 상품과 이미지가 일치하지 않습니다."),
    DUPLICATE_DEFAULT_IMAGE(HttpStatus.CONFLICT, "이미 대표 이미지가 존재합니다."),
    INVALID_FILE_FORMAT(HttpStatus.BAD_REQUEST, "허용되지 않은 이미지 형식입니다."),
    FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "파일 크기가 허용 범위를 초과했습니다."),
    FILE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
