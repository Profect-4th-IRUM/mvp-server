package com.irum.come2us.domain.order.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;

import lombok.Builder;

@Builder
public record CustomerOrderListResponse(
	UUID nextCursor,
	Boolean hasNext,
	List<OrderResponse> orderList
){
	@Builder
	public record OrderResponse(
		UUID orderId,
		LocalDateTime orderAt,
        RefundStatus refundStatus,
		List<ProductResponse> productResponseList
	){}
	@Builder
	public record ProductResponse(
		UUID orderDetailId,
		String optionName,
		int quantity,
		OrderStatus orderStatus,
		RefundStatus refundStatus,
		int price
	){}

}
