package com.irum.come2us.domain.coupon.domain.entity;

import com.irum.come2us.domain.payment.domain.entity.Payment;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

// 1. 엔티티

@Entity
@Table(name = "p_applied_coupon")
@Getter
@SQLDelete(sql = "UPDATE p_applied_coupon SET deleted_at = NOW() WHERE applied_coupon_id = ?")
@Where(clause = "deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AppliedCoupon extends BaseEntity {

    @Id // 적용 쿠폰 아이디
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "applied_coupon_id", nullable = false)
    private UUID appliedCouponId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;


    // 2. 생성자
    public AppliedCoupon(Payment payment, Coupon coupon) {
        this.payment = payment;
        this.coupon = coupon;
    }
}
