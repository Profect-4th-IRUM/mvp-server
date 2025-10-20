package com.irum.come2us.domain.coupon.domain.repository;

import com.irum.come2us.domain.coupon.domain.entity.AppliedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AppliedCouponRepository extends JpaRepository<AppliedCoupon, Long> {
    List<AppliedCoupon> findByCouponId(UUID couponId);
}