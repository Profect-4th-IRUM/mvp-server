package com.irum.come2us.domain.deliverypolicy.presentation.controller;

import com.irum.come2us.domain.deliverypolicy.application.service.DeliveryPolicyService;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.request.DeliveryPolicyCreateRequest;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.request.DeliveryPolicyInfoUpdateRequest;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.response.DeliveryPolicyInfoResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/delivery-policies")
@RequiredArgsConstructor
@Slf4j
public class DeliveryPolicyController {
    private final DeliveryPolicyService deliveryPolicyService;

    @PostMapping
    public ResponseEntity<Void> registerDeliveryPolicy(
            @Valid @RequestBody DeliveryPolicyCreateRequest request) {
        log.info("배송비 정책 등록 요청: {}", request);
        deliveryPolicyService.createDeliveryPolicy(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{deliveryPolicyId}")
    public ResponseEntity<Void> updateDeliveryPolicy(
            @PathVariable UUID deliveryPolicyId,
            @Valid @RequestBody DeliveryPolicyInfoUpdateRequest request) {
        log.info("배송비 정책 수정: deliveryPolicyId={}, request={}", deliveryPolicyId, request);
        deliveryPolicyService.changeDeliveryPolicy(deliveryPolicyId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{deliveryPolicyId}")
    public ResponseEntity<Void> deleteDeliveryPolicy(@PathVariable UUID deliveryPolicyId) {
        log.info("배송비 정책 삭제 요청 : storeId={}", deliveryPolicyId);
        deliveryPolicyService.withdrawDeliveryPolicy(deliveryPolicyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{deliveryPolicyId}")
    public ResponseEntity<DeliveryPolicyInfoResponse> getDeliveryPolicy(
            @PathVariable UUID deliveryPolicyId) {
        log.info("배송비 정책 조회 요청: deliveryPolicyId={}", deliveryPolicyId);
        DeliveryPolicyInfoResponse response =
                deliveryPolicyService.findDeliveryPolicy(deliveryPolicyId);
        return ResponseEntity.ok(response);
    }
}
