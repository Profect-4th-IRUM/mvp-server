package com.irum.come2us.domain.order.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailStatusResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerOrderService {
	private final OrderDetailRepository orderDetailRepository;

	public OrderDetailStatusResponse getOrderDetailStatus(UUID orderDetailId){
		OrderDetail orderDetail = orderDetailRepository.findByOrderDetailId(orderDetailId)
			.orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

		return OrderDetailStatusResponse.from(orderDetail);
	}
}
