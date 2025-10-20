package com.irum.come2us.domain.order.presentation.controller;

import com.irum.come2us.domain.order.application.service.OrderService;
import com.irum.come2us.global.presentation.advice.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("owner/orders")
@RequiredArgsConstructor
@Slf4j
public class OwnerOrderController {
    private final OrderService orderService;

    @PatchMapping("/order-details/{orderDetailId}/preparation")
    public ResponseEntity<String> updateOrderStatusToPreparation(
            @PathVariable UUID orderDetailId
            ){
        orderService.updateOrderStatusToPreparation(orderDetailId);
        return ResponseEntity.ok( "주문 상태 준비중으로 업데이트 완료");
    }



}
