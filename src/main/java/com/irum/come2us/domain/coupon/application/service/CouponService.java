package com.irum.come2us.domain.coupon.application.service;

import com.irum.come2us.domain.coupon.domain.entity.Coupon;
import com.irum.come2us.domain.coupon.domain.repository.CouponRepository;
import com.irum.come2us.domain.coupon.presentation.dto.request.CouponGenerateRequest;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    @Transactional // 쿠폰 생성
    public void generateCoupon(CouponGenerateRequest request, Long memberId) {

        Member member =
                memberRepository
                        .findByMemberId(memberId)
                        .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다"));

        Coupon coupon =
                Coupon.createCoupon(
                        request.getName(),
                        request.getDiscountAmount(),
                        request.getExpiration(),
                        member);

        couponRepository.save(coupon);
    }

    public List<Coupon> getCouponByMember(Long memberId) {
        return couponRepository.findByMember_MemberId(memberId);
    }

    @Transactional
    public void deleteCoupon(UUID couponId, Long memberId) {

        Coupon coupon =
                couponRepository
                        .findById(couponId)
                        .orElseThrow(() -> new EntityNotFoundException("쿠폰을 찾을 수 없습니다."));

        if (!coupon.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("본인의 쿠폰만 삭제할 수 있습니다");
        }

        couponRepository.delete(coupon);
    }
}
