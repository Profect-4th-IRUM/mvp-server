package com.irum.come2us.domain.coupon.application.service;

import com.irum.come2us.domain.coupon.domain.entity.AppliedCoupon;
import com.irum.come2us.domain.coupon.domain.entity.Coupon;
import com.irum.come2us.domain.coupon.domain.repository.AppliedCouponRepository;
import com.irum.come2us.domain.coupon.domain.repository.CouponRepository;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.payment.domain.entity.Payment;
import java.util.List;
import java.util.UUID;

import com.irum.come2us.domain.payment.domain.repository.PaymentRepository;
import com.irum.come2us.global.util.MemberUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppliedCouponService {
    private final AppliedCouponRepository appliedCouponRepository;
    private final CouponRepository couponRepository;
    private final MemberUtil memberUtil;

    /** 쿠폰 사용 처리 */
    public void createAppliedCouponList(Payment payment, List<UUID> couponIdList) {
        List<Coupon> couponList = couponRepository.findAllById(couponIdList);

        List<AppliedCoupon> appliedCouponList =
                couponList.stream()
                        .map(
                                coupon ->
                                        AppliedCoupon.builder()
                                                .payment(payment)
                                                .coupon(coupon)
                                                .build())
                        .toList();

        appliedCouponRepository.saveAll(appliedCouponList);
    }

    /** 롤백 */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rollbackAppliedCouponList(Payment payment) {
        appliedCouponRepository.deleteByPayment(payment);
    }
}
