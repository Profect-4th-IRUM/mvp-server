package com.irum.come2us.domain.order.presentation.controller;

import com.irum.come2us.domain.order.application.service.OrderService;
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
@RequestMapping("/orders/owner")
@RequiredArgsConstructor
@Slf4j
public class OwnerOrderController {
    private final OrderService orderService;
    private final SalesService salesService;

    /** [상점] 배송 준비 중 목록 조회 * */
    @GetMapping("/{storeId}/preparing")
    public OwnerOrderListResponse preparingOrderListGet(
            @PathVariable UUID storeId,
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size) {
        return orderService.getPreparingOrderList(storeId, cursor, size);
    }

    /** [상점] 부분 배송 중 목록 조회 * */
    @GetMapping("/{storeId}/partially-shipped")
    public OwnerOrderListResponse partiallyShippedOrderListGet(
            @PathVariable UUID storeId,
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size) {
        return orderService.getPartiallyShippedOrderList(storeId, cursor, size);
    }

    /** [상점] 부분 배송 완료 목록 조회 * */
    @GetMapping("/{storeId}/partially-delivered")
    public OwnerOrderListResponse partiallyDeliveredOrderListGet(
            @PathVariable UUID storeId,
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size) {
        return orderService.getPartiallyDeliveredOrderList(storeId, cursor, size);
    }

    /** [상점] 배송 완료 목록 조회 * */
    @GetMapping("/{storeId}/delivered")
    public OwnerOrderListResponse deliveredOrderListGet(
            @PathVariable UUID storeId,
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size) {
        return orderService.getDeliveredOrderList(storeId, cursor, size);
    }

    /** [상점] 주문 상태 변경 (배송준비중) * */
    @PatchMapping("/order-details/{orderDetailId}/preparing")
    public ResponseEntity<String> orderStatusToPreparingUpdate(@PathVariable UUID orderDetailId) {
        orderService.updateOrderStatusToPreparing(orderDetailId);
        return ResponseEntity.noContent().build();
    }

    /** [상점] 주문 상태 변경 (배송중) * */
    @PatchMapping("/order-details/{orderDetailId}/shipped")
    public ResponseEntity<String> orderStatusToShippedUpdate(
            @PathVariable UUID orderDetailId, @RequestBody OwnerOrderShippedRequest request) {
        orderService.updateOrderStatusToShipped(orderDetailId, request);
        return ResponseEntity.noContent().build();
    }

    /** [상점] 주문 상태 변경 (배송완료) * */
    @PatchMapping("/order-details/{orderDetailId}/delivered")
    public ResponseEntity<String> orderStatusToDeliveredUpdate(@PathVariable UUID orderDetailId) {
        orderService.updateOrderStatusToDelivered(orderDetailId);
        return ResponseEntity.noContent().build();
    }

    /* [상점] 주문 상세 */
    @GetMapping("/{order-id}")
    public ResponseEntity<OrderDetailResponse> ownerOrderDetail(@PathVariable UUID orderId) {
        OrderDetailResponse response = orderService.detailResponse(orderId);
        return ResponseEntity.ok(response);
    }

    //   정산 내역
    @GetMapping("/sales")
    public ResponseEntity<SalesResponse> salesResponse(@PathVariable UUID storeId) {
        SalesResponse response = salesService.getSalesList(storeId);
        return ResponseEntity.ok(response);
    }
}
