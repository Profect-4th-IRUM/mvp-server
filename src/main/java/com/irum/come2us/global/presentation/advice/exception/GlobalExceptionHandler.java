package com.irum.come2us.global.presentation.advice.exception;

import com.irum.come2us.global.presentation.advice.exception.errorcode.BaseErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.GlobalErrorCode;
import com.irum.come2us.global.presentation.advice.response.CommonResponse;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(basePackages = "com.irum")
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<CommonResponse> handleCustomException(CommonException e) {
        final BaseErrorCode errorCode = e.getErrorCode();
        final ErrorResponse errorResponse =
                ErrorResponse.of(errorCode.errorClassName(), errorCode.getMessage());
        final CommonResponse response =
                CommonResponse.onFailure(errorCode.getHttpStatus().value(), errorResponse);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @SneakyThrows
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        final String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        final ErrorResponse errorResponse =
                ErrorResponse.of(e.getClass().getSimpleName(), errorMessage);
        final CommonResponse<ErrorResponse> response =
                CommonResponse.onFailure(HttpStatus.BAD_REQUEST.value(), errorResponse);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<CommonResponse> handleException(Exception e) {
        final BaseErrorCode errorCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
        final ErrorResponse errorResponse =
                ErrorResponse.of(e.getClass().getSimpleName(), errorCode.getMessage());
        final CommonResponse response =
                CommonResponse.onFailure(errorCode.getHttpStatus().value(), errorResponse);

        return ResponseEntity.status(errorCode.getHttpStatus().value()).body(response);
    }
}
