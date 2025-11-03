package com.irum.come2us.domain.coupon.application.service;

import com.irum.come2us.domain.coupon.domain.entity.Coupon;
import com.irum.come2us.domain.coupon.domain.repository.AppliedCouponRepository;
import com.irum.come2us.domain.coupon.domain.repository.CouponRepository;
import com.irum.come2us.domain.coupon.presentation.dto.request.CouponGenerateRequest;
import com.irum.come2us.domain.coupon.presentation.dto.response.CouponResponse;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.CouponErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.time.LocalDateTime;
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
    private final AppliedCouponRepository appliedCouponRepository;
    private final MemberUtil memberUtil;

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
    public List<CouponResponse> getCouponByMember(Long memberId) {
        return couponRepository.findByMember_MemberId(memberId).stream()
                .map(CouponResponse::from)
                .toList();
    }

    public void deleteCoupon(UUID couponId, Long memberId) {

        Coupon coupon =
                couponRepository
                        .findById(couponId)
                        .orElseThrow(() -> new CommonException(CouponErrorCode.COUPON_NOT_FOUND));
        if (!coupon.getMember().getMemberId().equals(memberId)) {
            throw new CommonException(CouponErrorCode.ONLY_OWNER_CAN_DELETE);
        }
        memberUtil.assertMemberResourceAccess(coupon.getMember());
        coupon.softDelete(memberUtil.getCurrentMember().getMemberId());
    }

    /** 쿠폰 유효성 검증 및 할인 금액 계산 */
    public int validAndCalCoupon(List<UUID> couponIdList, int calculatedTotalPrice, Member member) {
        if (couponIdList.isEmpty()) {
            return 0;
        }

        int totalDiscount = 0;
        List<Coupon> couponList = couponRepository.findAllById(couponIdList);

        for (Coupon coupon : couponList) {
            // 권한 검사
            if (!coupon.getMember().getMemberId().equals(member.getMemberId())) {
                throw new CommonException(CouponErrorCode.COUPON_NO_PERMISSION);
            }
            // 만료일 검사
            if (coupon.getExpiration().isBefore(LocalDateTime.now())) {
                throw new CommonException(CouponErrorCode.COUPON_EXPIRATION);
            }
            // 사용 여부 검사
            if (appliedCouponRepository.existsByCouponId(coupon.getId())) {
                throw new CommonException(CouponErrorCode.COUPON_ALREADY_USED);
            }
            totalDiscount += coupon.getDiscountAmount();
        }

        if (totalDiscount > calculatedTotalPrice) {
            totalDiscount = calculatedTotalPrice;
        }

        return totalDiscount;
    }
}
