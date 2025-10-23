package com.irum.come2us.domain.order.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;

public record OrderListResponse (
	String nextCursor,
	Boolean hasNext,
	List<OrderResponse> orderList
){
	public record OrderResponse(
		LocalDateTime orderAt,
		List<ProductResponse> productResponseList
	){
		public record ProductResponse(
			UUID orderDetailId,
			String optionName,
			int quantity,
			OrderStatus orderStatus,
			RefundStatus refundStatus,
			int price
		){}
	}
}
