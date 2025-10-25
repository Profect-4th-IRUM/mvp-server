package com.irum.come2us.domain.order.infrastructure.repository.dto;

import java.util.UUID;

import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;

/** 내부용 주문 상세 DTO */
public record CustomerOrderDetailRow(
        UUID orderId,
		UUID orderDetailId,
		String productName,
		String optionName,
		int quantity,
		int price,
		OrderStatus orderStatusIndi
) {}
