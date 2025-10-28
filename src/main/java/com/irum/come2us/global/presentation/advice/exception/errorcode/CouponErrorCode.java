package com.irum.come2us.global.presentation.advice.exception.errorcode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CouponErrorCode implements BaseErrorCode {
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    ONLY_OWNER_CAN_DELETE(HttpStatus.FORBIDDEN, "본인의 쿠폰만 삭제할 수 있습니다."),
    COUPON_NO_PERMISSION(HttpStatus.FORBIDDEN, "쿠폰 사용 권한이 없습니다"),
    COUPON_EXPIRATION(HttpStatus.BAD_REQUEST, "만료된 쿠폰입니다."),
    COUPON_ALREADY_USED(HttpStatus.BAD_REQUEST, "이미 사용한 쿠폰입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String errorClassName() {
        return this.name();
    }
}
