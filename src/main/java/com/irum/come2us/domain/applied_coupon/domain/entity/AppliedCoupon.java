package com.irum.come2us.domain.applied_coupon.domain.entity;

import com.irum.come2us.domain.coupon.domain.entity.Coupon;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

// 1. 엔티티

@Entity
@Table(name = "p_applied_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class AppliedCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applied_coupon_id", nullable = false)
    private Long appliedCouponId;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "coupon_id", nullable = false)
    private UUID couponId;

    /* 결제 도메인과의 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", insertable = false, updatable = false)
    private Payment payment;
    */

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", insertable = false, updatable = false)
    private Coupon coupon;

    // 2. 생성자

    private AppliedCoupon(UUID paymentId, UUID couponId) {
        this.paymentId = paymentId;
        this.couponId = couponId;
    }

    // 3. 정적 팩토리 메서드

    public static AppliedCoupon of(UUID paymentId, UUID couponId) {
        return new AppliedCoupon(paymentId, couponId);
    }
}
