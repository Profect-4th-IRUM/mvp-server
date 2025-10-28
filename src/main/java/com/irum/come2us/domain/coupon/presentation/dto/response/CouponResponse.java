package com.irum.come2us.domain.coupon.presentation.dto.response;

import com.irum.come2us.domain.coupon.domain.entity.Coupon;
import java.time.LocalDateTime;
import java.util.UUID;

public record CouponResponse(UUID id, String name, int discountAmount, LocalDateTime expiration) {
    public static CouponResponse from(Coupon coupon) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountAmount(),
                coupon.getExpiration());
    }
}
