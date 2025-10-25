package com.irum.come2us.domain.order.presentation.controller;

import com.irum.come2us.domain.order.application.service.CustomerOrderService;
import com.irum.come2us.domain.order.presentation.dto.response.CustomerOrderListResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailStatusResponse;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("orders/customer")
public class CustomerOrderController {
    private final CustomerOrderService customerOrderService;

    /** 주문 상태 조회 */
    @GetMapping("/order-detail/{orderDetailId}/delivery-status")
    public OrderDetailStatusResponse orderDetailStatusGet(
            @PathVariable(name = "orderDetailId") UUID orderDetailId) {
        return customerOrderService.getOrderDetailStatus(orderDetailId);
    }

    /** 주문 상세 조회 */
    @GetMapping("/{orderId}")
    public OrderDetailResponse orderDetailGet(@PathVariable(name = "orderId") UUID orderId) {
        return customerOrderService.getOrderDetail(orderId);
    }

    /** 주문 목록 조회 */
    @GetMapping
    public CustomerOrderListResponse orderListGet(
            @RequestParam(required = false) UUID cursor,
            @RequestParam int size,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        return customerOrderService.getOrderList(cursor, size, startDate, endDate);
    }
}
