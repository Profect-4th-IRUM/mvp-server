package com.irum.come2us.domain.coupon.domain.repository;

import com.irum.come2us.domain.coupon.domain.entity.AppliedCoupon;
import com.irum.come2us.domain.payment.domain.entity.Payment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppliedCouponRepository extends JpaRepository<AppliedCoupon, UUID> {
    List<AppliedCoupon> findByCouponId(UUID couponId);

    boolean existsByCouponId(UUID couponId);

    void deleteByPayment(Payment payment);
}
