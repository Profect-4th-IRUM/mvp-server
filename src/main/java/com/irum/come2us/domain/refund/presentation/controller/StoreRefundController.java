package com.irum.come2us.domain.refund.presentation.controller;

import com.irum.come2us.domain.refund.application.service.StoreRefundService;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import com.irum.come2us.domain.refund.presentation.dto.request.StoreRefundCreateRequest;
import com.irum.come2us.domain.refund.presentation.dto.request.StoreRefundStatusRequest;
import com.irum.come2us.domain.refund.presentation.dto.response.StoreRefundDetailResponse;
import com.irum.come2us.domain.refund.presentation.dto.response.StoreRefundListResponse;
import com.irum.come2us.domain.refund.presentation.dto.response.StoreRefundResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owner/refund")
@RequiredArgsConstructor
public class StoreRefundController {
    private final StoreRefundService refundService;

    @PatchMapping("/{refund-id}") // 환불 상태 변경
    public ResponseEntity<Void> patchRefundStatus(
            @PathVariable("refund-id") UUID refundId,
            @RequestBody StoreRefundStatusRequest request) {
        refundService.patchRefundStatus(refundId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/list/requested") // 환불 요청 목록
    public ResponseEntity<StoreRefundListResponse> getRefundRequestList() {
        return ResponseEntity.ok(refundService.getRefundListByStatus(RefundStatus.REQUESTED));
    }

    @GetMapping("/list/processing") // 환불 진행중 목록
    public ResponseEntity<StoreRefundListResponse> getRefundProcessingList() {
        return ResponseEntity.ok(refundService.getRefundListByStatus(RefundStatus.PENDING));
    }

    @GetMapping("/list/complete") // 환불 완료 목록
    public ResponseEntity<StoreRefundListResponse> getRefundCompleteList() {
        return ResponseEntity.ok(refundService.getRefundListByStatus(RefundStatus.COMPLETED));
    }

    @PostMapping("/{order-id}") // 환불 요청
    public ResponseEntity<StoreRefundResponse> createRefund(
            @PathVariable("order-id") UUID orderId, @RequestBody StoreRefundCreateRequest request) {
        StoreRefundResponse response = refundService.createRefund(orderId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{refund-id}") // 환불 상세 보기
    public ResponseEntity<StoreRefundDetailResponse> getRefundDatil(
            @PathVariable("refund-id") UUID refundId) {
        StoreRefundDetailResponse response = refundService.getRefundDetail(refundId);
        return ResponseEntity.ok(response);
    }
}
