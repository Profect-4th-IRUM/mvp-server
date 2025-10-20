package com.irum.come2us.domain.coupon.domain.repository;

import com.irum.come2us.domain.coupon.domain.entity.Coupon;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    List<Coupon> findByMember_MemberId(Long memberId);

    @Query("SELECT c FROM Coupon c WHERE c.member.memberId = :memberId AND c.expiration > :now")
    List<Coupon> findValidCouponByMemberId(
            @Param("memberId") Long memberId, @Param("now") LocalDateTime now);
}
