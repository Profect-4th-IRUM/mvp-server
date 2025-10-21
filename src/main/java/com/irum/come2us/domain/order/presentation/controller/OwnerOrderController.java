package com.irum.come2us.domain.order.presentation.controller;

import com.irum.come2us.domain.order.application.service.OrderService;
import com.irum.come2us.domain.order.presentation.dto.response.PaymentOrderResponse;
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

    @GetMapping("/preparing")
    public ResponseEntity<PaymentOrderResponse> preparingOrderListGet(
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size
    ) {
        UUID storeId = UUID.randomUUID(); //TODO : 실제 아이디로 변경
        PaymentOrderResponse response = orderService.getPreparingOrderList(storeId, cursor, size);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/order-details/{orderDetailId}/preparing")
    public ResponseEntity<String> orderStatusToPreparingUpdate(
            @PathVariable UUID orderDetailId
            ){
        orderService.updateOrderStatusToPreparing(orderDetailId);
        return ResponseEntity.ok( "주문 상태 준비중으로 업데이트 완료");
    }


    @PatchMapping("/order-details/{orderDetailId}/shipped")
    public ResponseEntity<String> orderStatusToShippedUpdate(
            @PathVariable UUID orderDetailId
    ){
        orderService.updateOrderStatusToShipped(orderDetailId);
        return ResponseEntity.ok( "주문 상태 배송 중으로 업데이트 완료");
    }

    @PatchMapping("/order-details/{orderDetailId}/delivered")
    public ResponseEntity<String> orderStatusToDeliveredUpdate(
            @PathVariable UUID orderDetailId
    ){
        orderService.updateOrderStatusToDelivered(orderDetailId);
        return ResponseEntity.ok( "주문 상태 배송 중으로 업데이트 완료");
    }
}
