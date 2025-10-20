package com.irum.come2us.domain.coupon.domain.repository;

import com.irum.come2us.domain.coupon.domain.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, String> {}
