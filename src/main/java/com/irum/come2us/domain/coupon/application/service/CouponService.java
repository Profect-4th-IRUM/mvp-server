package com.irum.come2us.domain.coupon.application.service;

import com.irum.come2us.domain.coupon.domain.entity.Coupon;
import com.irum.come2us.domain.coupon.domain.repository.CouponRepository;
import com.irum.come2us.domain.coupon.presentation.dto.request.CouponGenerateRequest;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.CouponErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {
    private final CouponRepository couponRepository;
    private final MemberRepository memberRepository;

    public void createCoupon(CouponGenerateRequest request, Long memberId) {

        Member member =
                memberRepository
                        .findByMemberId(memberId)
                        .orElseThrow(() -> new CommonException(MemberErrorCode.MEMBER_NOT_FOUND));

        Coupon coupon =
                Coupon.createCoupon(
                        request.name(), request.discountAmount(), request.expiration(), member);

        couponRepository.save(coupon);
    }

    @Transactional(readOnly = true)
    public List<Coupon> getCouponByMember(Long memberId) {
        return couponRepository.findByMember_MemberId(memberId);
    }

    public void deleteCoupon(UUID couponId, Long memberId) {

        Coupon coupon =
                couponRepository
                        .findById(couponId)
                        .orElseThrow(() -> new CommonException(CouponErrorCode.COUPON_NOT_FOUND));
        if (!coupon.getMember().getMemberId().equals(memberId)) {
            throw new CommonException(CouponErrorCode.ONLY_OWNER_CAN_DELETE);
        }
        couponRepository.delete(coupon);
    }
}
