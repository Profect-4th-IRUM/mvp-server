package com.irum.come2us.domain.coupon.presentation.controller;

import com.irum.come2us.domain.coupon.application.service.CouponService;
import com.irum.come2us.domain.coupon.domain.entity.Coupon;
import com.irum.come2us.domain.coupon.presentation.dto.request.CouponGenerateRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {
    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<CouponGenerateRequest> createCoupon(
            @Valid @RequestBody CouponGenerateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        couponService.createCoupon(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<Coupon>> getCoupon(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long memberId = Long.parseLong(userDetails.getUsername());
        List<Coupon> coupons = couponService.getCouponByMember(memberId);
        return ResponseEntity.ok(coupons);
    }

    @DeleteMapping("/{coupon-id}")
    public ResponseEntity<Void> deleteCoupon(
            @PathVariable("coupon-id") UUID couponId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long memberId = Long.parseLong(userDetails.getUsername());
        couponService.deleteCoupon(couponId, memberId);
        return ResponseEntity.noContent().build();
    }
}
