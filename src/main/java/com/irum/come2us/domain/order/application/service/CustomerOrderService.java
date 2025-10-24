package com.irum.come2us.domain.order.application.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderSummaryRow;
import com.irum.come2us.domain.order.presentation.dto.response.OwnerOrderListResponse;
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
	public OrderListResponse getOrderList(UUID cursor, int size, LocalDate startDate, LocalDate endDate){
		Member member = memberUtil.getCurrentMember();
		List<Order> orderList = orderRepository.findAllByMember(member);

        // 2. order list 검색
        var headerList =
                orderRepository.fetchOrderHeaderListByMember(member, startDate, endDate, cursor, size);

        boolean hasNext = headerList.size() > size;
        if (hasNext) {
            headerList = headerList.subList(0, size);
        }

        // 3. order detail 검색
        List<UUID> orderIdList = headerList.stream().map(Order::getOrderId).toList();


        var orderDetailList = orderRepository.fetchOrderDetailList(orderIdList);

        // 4. orderId로 그룹핑 : productSummary 제작
        Map<UUID, List<OwnerOrderListResponse.ProductSummary>> detailMap =
                orderDetailList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        OrderDetailRow::orderId,
                                        Collectors.mapping(
                                                orderMapper::toProductSummary,
                                                Collectors.toList())));

        // 5. orderSummary 제작
        List<OwnerOrderListResponse.OrderSummary> orderSummaryList =
                headerList.stream()
                        .filter(order -> detailMap.containsKey(order.orderId()))
                        .map(
                                order ->
                                        orderMapper.toOrderSummary(
                                                order,
                                                detailMap.getOrDefault(
                                                        order.orderId(),
                                                        List.of()) // order detail 없다면 빈 리스트
                                        ))
                        .toList();

        // 6. next cursor계산
        UUID nextCursor = orderSummaryList.isEmpty() ? null : headerList.getLast().orderId();


	}
}
