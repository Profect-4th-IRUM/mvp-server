package com.irum.come2us.domain.order.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.application.mapper.CustomerOrderMapper;
import com.irum.come2us.domain.order.application.mapper.OrderMapper;
import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailStatusResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderListResponse;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.repository.RefundRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;
import com.irum.come2us.global.util.MemberUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomerOrderService {
	private final OrderDetailRepository orderDetailRepository;
	private final OrderRepository orderRepository;
	private final RefundRepository refundRepository;
	private final MemberUtil memberUtil;

	@Transactional(readOnly = true)
	public OrderDetailStatusResponse getOrderDetailStatus(UUID orderDetailId){
		OrderDetail orderDetail = orderDetailRepository.findByOrderDetailId(orderDetailId)
			.orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

		return OrderDetailStatusResponse.from(orderDetail);
	}


	@Transactional(readOnly = true)
	public OrderDetailResponse getOrderDetail(UUID orderId){
		Order order = orderRepository.findByOrderId(orderId)
			.orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));

		List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);

		Refund refund = refundRepository.findByOrder(order).orElse(null);

		return CustomerOrderMapper.toOrderDetailResponse(order, orderDetailList, refund);

	}

	@Transactional
	public OrderListResponse getOrderList(){
		Member member = memberUtil.getCurrentMember();
		List<Order> orderList = orderRepository.findAllByMember(member);


	}
}
