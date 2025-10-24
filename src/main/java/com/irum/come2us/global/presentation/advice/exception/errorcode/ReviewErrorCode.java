package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "리뷰 정보를 찾을 수 없습니다."),
    REVIEW_NOT_MODIFIED(HttpStatus.BAD_REQUEST, "리뷰 수정에 대한 변경된 내용이 없습니다."),
    REVIEW_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 리뷰입니다."),
    REVIEW_UNAUTHORIZED(HttpStatus.FORBIDDEN, "리뷰에 대한 수정 또는 삭제 권한이 없습니다."),
    REVIEW_IMAGE_PROCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "리뷰 이미지 처리 중 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
