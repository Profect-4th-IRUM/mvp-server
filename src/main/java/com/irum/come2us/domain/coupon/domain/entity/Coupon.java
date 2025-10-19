package com.irum.come2us.domain.coupon.domain.entity;

import com.irum.come2us.domain.member.domain.entity.Member;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

// 1. 엔티티

@Entity
@Table(name = "p_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

    @Id // 쿠폰 아이디
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "coupon_id", nullable = false)
    private UUID id;

    @Column(name = "name", length = 20) // 쿠폰명
    private String name;

    @Column(name = "discount_amount") // 할인 금액
    private Integer discountAmount;

    @Column(name = "member_id") // 회원 식별 아이디
    private Long memberId;

    @Column(name = "expiration") // 유효기간
    private LocalDateTime expiration;

    /*
    향후 쿠폰 사용 기능 추가시 주석 해제

        @Column(name = "is_used", nullable = false) // 쿠폰 사용 여부
        private boolean isUsed = false;

        @Column(name = "used_at") // 쿠폰 사용 시점
        private LocalDateTime usedAt;
    */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(insertable = false, updatable = false)
    private Member member;

    // 2. 생성자

    @Builder(access = AccessLevel.PRIVATE)
    private Coupon(String name, Integer discountAmount, LocalDateTime expiration, Member member) {
        this.name = name;
        this.discountAmount = discountAmount;
        this.expiration = expiration;
        this.member = member;
        //  this.isUsed = false;
    }

    // 3. 쿠폰 생성 정적 팩토리 메서드

    public static Coupon createCoupon(
            String name, Integer discountAmount, LocalDateTime expiration, Member member) {
        return Coupon.builder()
                .name(name)
                .discountAmount(discountAmount)
                .expiration(expiration)
                .member(member)
                .build();
    }
}
