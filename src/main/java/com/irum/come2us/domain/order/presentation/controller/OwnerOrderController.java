package com.irum.come2us.domain.order.presentation.controller;

import com.irum.come2us.domain.order.application.service.OrderService;
import com.irum.come2us.domain.order.presentation.dto.response.OwnerOrderListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("owner/orders")
@RequiredArgsConstructor
@Slf4j
public class OwnerOrderController {
    private final OrderService orderService;

    /** [상점] 배송 준비 중 목록 조회 **/
    @GetMapping("/preparing")
    public ResponseEntity<OwnerOrderListResponse> preparingOrderListGet(
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size
    ) {
        OwnerOrderListResponse response = orderService.getPreparingOrderList(cursor, size);
        return ResponseEntity.ok(response);
    }

    /** [상점] 부분 배송 중 목록 조회 **/
    @GetMapping("/partially-shipped")
    public ResponseEntity<OwnerOrderListResponse> partiallyShippedOrderListGet(
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size
    ) {
        OwnerOrderListResponse response = orderService.getPartiallyShippedOrderList(cursor, size);
        return ResponseEntity.ok(response);
    }

    /** [상점] 부분 배송 완료 목록 조회 **/
    @GetMapping("/partially-delivered")
    public ResponseEntity<OwnerOrderListResponse> partiallyDeliveredOrderListGet(
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size
    ) {
        OwnerOrderListResponse response = orderService.getPartiallyDeliveredOrderList(cursor, size);
        return ResponseEntity.ok(response);
    }

    /** [상점] 배송 완료 목록 조회 **/
    @GetMapping("/delivered")
    public ResponseEntity<OwnerOrderListResponse> deliveredOrderListGet(
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size
    ) {
        OwnerOrderListResponse response = orderService.getDeliveredOrderList(cursor, size);
        return ResponseEntity.ok(response);
    }


    /** [상점] 주문 상태 변경 (배송준비중) **/
    @PatchMapping("/order-details/{orderDetailId}/preparing")
    public ResponseEntity<String> orderStatusToPreparingUpdate(
            @PathVariable UUID orderDetailId
            ){
        orderService.updateOrderStatusToPreparing(orderDetailId);
        return ResponseEntity.ok( "주문 상태 준비중으로 업데이트 완료");
    }

    /** [상점] 주문 상태 변경 (배송중) **/
    @PatchMapping("/order-details/{orderDetailId}/shipped")
    public ResponseEntity<String> orderStatusToShippedUpdate(
            @PathVariable UUID orderDetailId
    ){
        orderService.updateOrderStatusToShipped(orderDetailId);
        return ResponseEntity.ok( "주문 상태 배송 중으로 업데이트 완료");
    }

    /** [상점] 주문 상태 변경 (배송완료) **/
    @PatchMapping("/order-details/{orderDetailId}/delivered")
    public ResponseEntity<String> orderStatusToDeliveredUpdate(
            @PathVariable UUID orderDetailId
    ){
        orderService.updateOrderStatusToDelivered(orderDetailId);
        return ResponseEntity.ok( "주문 상태 배송 중으로 업데이트 완료");
    }
}
