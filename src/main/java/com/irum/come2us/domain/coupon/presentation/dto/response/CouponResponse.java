package com.irum.come2us.domain.coupon.presentation.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.irum.come2us.domain.coupon.domain.entity.Coupon;
import java.time.LocalDateTime;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String name,
        int discountAmount,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 명시적 포맷 추가
                // 또는 요청 DTO와 동일하게 @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                LocalDateTime expiration) {
    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountAmount(),
                coupon.getExpiration());
    }
}
