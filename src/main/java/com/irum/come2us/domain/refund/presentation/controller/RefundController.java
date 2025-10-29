package com.irum.come2us.domain.refund.presentation.controller;

import com.irum.come2us.domain.refund.application.service.RefundService;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import com.irum.come2us.domain.refund.presentation.dto.request.RefundCreateRequest;
import com.irum.come2us.domain.refund.presentation.dto.request.StoreRefundStatusRequest;
import com.irum.come2us.domain.refund.presentation.dto.response.RefundDetailResponse;
import com.irum.come2us.domain.refund.presentation.dto.response.StoreRefundListResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refund")
@RequiredArgsConstructor
public class RefundController {
    private final RefundService refundService;

    // Common
    @PostMapping("/{orderId}")
    public ResponseEntity<Void> registerRefund(
            @PathVariable UUID orderId, @Valid @RequestBody RefundCreateRequest request) {
        refundService.createRefund(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{orderId}/detail")
    public RefundDetailResponse getRefundDetail(@PathVariable UUID orderId) {
        return refundService.findRefundDetail(orderId);
    }

    // Owner
    @PatchMapping("/{refund-id}") // 환불 상태 변경
    public ResponseEntity<Void> patchRefundStatus(
            @PathVariable("refund-id") UUID refundId,
            @RequestBody StoreRefundStatusRequest request) {
        refundService.changeRefundStatus(refundId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list/{refundStatus}") // 환불 요청 목록
    public StoreRefundListResponse getRefundList(
            @RequestParam(name = "status", required = false) RefundStatus status) {
        return refundService.findRefundListByStatus(status);
    }

    //    @GetMapping("/list/processing") // 환불 진행중 목록
    //    public ResponseEntity<StoreRefundListResponse> getRefundProcessingList() {
    //        return ResponseEntity.ok(refundService.getRefundListByStatus(RefundStatus.PENDING));
    //    }
    //
    //    @GetMapping("/list/complete") // 환불 완료 목록
    //    public ResponseEntity<StoreRefundListResponse> getRefundCompleteList() {
    //        return ResponseEntity.ok(refundService.getRefundListByStatus(RefundStatus.COMPLETED));
    //    }

}
