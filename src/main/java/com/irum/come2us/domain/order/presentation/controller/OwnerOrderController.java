package com.irum.come2us.domain.order.presentation.controller;

import com.irum.come2us.domain.order.application.service.OwnerOrderService;
import com.irum.come2us.domain.order.application.service.SalesService;
import com.irum.come2us.domain.order.presentation.dto.request.OwnerOrderShippedRequest;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OwnerOrderListResponse;
import com.irum.come2us.domain.order.presentation.dto.response.SalesResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("orders/owner")
@RequiredArgsConstructor
@Slf4j
public class OwnerOrderController {
    private final OwnerOrderService ownerOrderService;
    private final SalesService salesService;

    /** [상점] 배송 준비 중 목록 조회 * */
    @GetMapping("/{storeId}/preparing")
    public OwnerOrderListResponse preparingOrderListGet(
            @PathVariable UUID storeId,
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size) {
        return ownerOrderService.getPreparingOrderList(storeId, cursor, size);
    }

    /** [상점] 부분 배송 중 목록 조회 * */
    @GetMapping("/{storeId}/partially-shipped")
    public OwnerOrderListResponse partiallyShippedOrderListGet(
            @PathVariable UUID storeId,
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size) {
        return ownerOrderService.getPartiallyShippedOrderList(storeId, cursor, size);
    }

    /** [상점] 부분 배송 완료 목록 조회 * */
    @GetMapping("/{storeId}/partially-delivered")
    public OwnerOrderListResponse partiallyDeliveredOrderListGet(
            @PathVariable UUID storeId,
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size) {
        return ownerOrderService.getPartiallyDeliveredOrderList(storeId, cursor, size);
    }

    /** [상점] 배송 완료 목록 조회 * */
    @GetMapping("/{storeId}/delivered")
    public OwnerOrderListResponse deliveredOrderListGet(
            @PathVariable UUID storeId,
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size) {
        return ownerOrderService.getDeliveredOrderList(storeId, cursor, size);
    }

    /** [상점] 주문 상태 변경 (배송준비중) * */
    @PatchMapping("/order-details/{orderDetailId}/preparing")
    public ResponseEntity<Void> orderStatusToPreparingUpdate(@PathVariable UUID orderDetailId) {
        ownerOrderService.updateOrderStatusToPreparing(orderDetailId);
        return ResponseEntity.noContent().build();
    }

    /** [상점] 주문 상태 변경 (배송중) * */
    @PatchMapping("/order-details/{orderDetailId}/shipped")
    public ResponseEntity<Void> orderStatusToShippedUpdate(
            @PathVariable UUID orderDetailId, @RequestBody OwnerOrderShippedRequest request) {
        ownerOrderService.updateOrderStatusToShipped(orderDetailId, request);
        return ResponseEntity.noContent().build();
    }

    /** [상점] 주문 상태 변경 (배송완료) * */
    @PatchMapping("/order-details/{orderDetailId}/delivered")
    public ResponseEntity<Void> orderStatusToDeliveredUpdate(@PathVariable UUID orderDetailId) {
        ownerOrderService.updateOrderStatusToDelivered(orderDetailId);
        return ResponseEntity.noContent().build();
    }

    /* [상점] 주문 상세 */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDetailResponse> ownerOrderDetail(@PathVariable UUID orderId) {
        OrderDetailResponse response = ownerOrderService.detailResponse(orderId);
        return ResponseEntity.ok(response);
    }

    //   정산 내역
    @GetMapping("/{storeId}/sales")
    public ResponseEntity<SalesResponse> salesResponse(@PathVariable UUID storeId) {
        SalesResponse response = salesService.getSalesList(storeId);
        return ResponseEntity.ok(response);
    }
}
