package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AiErrorCode implements BaseErrorCode {
    API_REQUEST_FAILED(HttpStatus.BAD_GATEWAY, "AI API 요청에 실패했습니다."),
    AI_PROCESS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI 응답 처리 중 오류가 발생했습니다."),
    EMPTY_RESPONSE(HttpStatus.BAD_GATEWAY, "AI API로부터 빈 응답을 받았습니다."),
    INVALID_PROMPT(HttpStatus.BAD_REQUEST, "유효하지 않은 프롬프트입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
