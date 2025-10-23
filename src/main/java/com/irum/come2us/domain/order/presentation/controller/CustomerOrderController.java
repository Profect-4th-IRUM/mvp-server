package com.irum.come2us.domain.order.presentation.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.irum.come2us.domain.order.application.service.CustomerOrderService;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailStatusResponse;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("orders/customer")
public class CustomerOrderController {
	private final CustomerOrderService customerOrderService;

	/**주문 상태 조회*/
	@GetMapping("/order-detail/{orderDetailId}/delivery-status")
	public OrderDetailStatusResponse orderDetailStatusGet(
		@PathVariable(name="orderDetailId") UUID orderDetailId
	){
		return customerOrderService.getOrderDetailStatus(orderDetailId);
	}

	/**주문 상세 조회*/
	@GetMapping("/{orderId}")
	public OrderDetailResponse orderDetailGet(
		@PathVariable(name="orderId") UUID orderId
	) {
		return customerOrderService.getOrderDetail(orderId);
	}

}
