package com.irum.come2us.domain.order.application.service;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.application.mapper.CustomerOrderMapper;
import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.order.infrastructure.repository.dto.CustomerOrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.CustomerOrderSummaryRow;
import com.irum.come2us.domain.order.presentation.dto.response.CustomerOrderListResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailStatusResponse;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.repository.RefundRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public OrderDetailStatusResponse getOrderDetailStatus(UUID orderDetailId) {
        Member member = memberUtil.getCurrentMember();

        OrderDetail orderDetail =
                orderDetailRepository
                        .findByOrderDetailIdWithOrderAndMember(orderDetailId)
                        .orElseThrow(
                                () -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        if (member.equals(orderDetail.getOrder().getMember()))
            throw new CommonException(OrderErrorCode.ORDER_FORBIDDEN);

        return OrderDetailStatusResponse.from(orderDetail);
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderDetail(UUID orderId) {
        Member member = memberUtil.getCurrentMember();

        // order 조회 및 member 검증
        Order order =
                orderRepository
                        .findByOrderIdAndMember(orderId, member)
                        .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));

        List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);

        Refund refund = refundRepository.findByOrder(order).orElse(null);

        return CustomerOrderMapper.toOrderDetailResponse(order, orderDetailList, refund);
    }

    @Transactional
    public CustomerOrderListResponse getOrderList(
            UUID cursor, int size, LocalDate startDate, LocalDate endDate) {

        Member member = memberUtil.getCurrentMember();
        log.info("member {}", member.getMemberId());

        // 2. order list 검색
        List<CustomerOrderSummaryRow> headerList =
                orderRepository.fetchOrderListByMember(member, startDate, endDate, cursor, size);
        log.info("order list {}", headerList);

        boolean hasNext = headerList.size() > size;
        if (hasNext) {
            headerList = headerList.subList(0, size);
        }
        log.info("hasNext {}", hasNext);

        // 3. order id list
        List<UUID> orderIdList = headerList.stream().map(CustomerOrderSummaryRow::orderId).toList();
        List<CustomerOrderDetailRow> orderDetailList =
                orderRepository.fetchOrderDetailListByMember(orderIdList);
        log.info("orderDetailList {}", orderDetailList);

        // 4. orderId로 그룹핑 : productSummary 제작
        Map<UUID, List<CustomerOrderListResponse.ProductResponse>> detailMap =
                orderDetailList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        CustomerOrderDetailRow::orderId,
                                        Collectors.mapping(
                                                CustomerOrderMapper::toProductResponse,
                                                Collectors.toList())));

        // 5. orderResponse 제작
        List<CustomerOrderListResponse.OrderResponse> orderResponseList =
                headerList.stream()
                        .filter(order -> detailMap.containsKey(order.orderId()))
                        .map(
                                order ->
                                        CustomerOrderMapper.toOrderResponse(
                                                order,
                                                detailMap.getOrDefault(
                                                        order.orderId(),
                                                        List.of()) // order detail 없다면 빈 리스트
                                                ))
                        .toList();

        // 6. next cursor계산
        UUID nextCursor = orderResponseList.isEmpty() ? null : headerList.getLast().orderId();

        return CustomerOrderListResponse.builder()
                .orderList(orderResponseList)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .build();
    }
}
