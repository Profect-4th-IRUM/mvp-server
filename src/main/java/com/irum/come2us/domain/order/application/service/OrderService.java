package com.irum.come2us.domain.order.application.service;

import com.irum.come2us.domain.order.application.mapper.OrderMapper;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepositoryCustom;
import com.irum.come2us.domain.order.infrastructure.repository.OrderRepositoryImpl;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderSummaryRow;
import com.irum.come2us.domain.order.presentation.dto.response.OwnerOrderListResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final OrderRepositoryCustom orderRepositoryCustom;
    private final OrderMapper orderMapper;


    @Transactional(readOnly = true)
    public OwnerOrderListResponse getPreparingOrderList(UUID storeId, UUID cursor, Integer size) {
        // 1. size validation
        if (size == null || !(size == 10 || size == 30 || size == 50)) {
            log.warn("허용 되지 않은 size 요청 : {} -> 기본값 10으로 대체", size);
            size = 10;
        }

        return getOwnerOrderList(storeId, OrderStatus.PREPARING, cursor, size);

    }

    @Transactional(readOnly = true)
    public OwnerOrderListResponse getPartiallyShippedOrderList(UUID storeId, UUID cursor, Integer size) {
        if (size == null || !(size == 10 || size == 30 || size == 50)) {
            log.warn("허용 되지 않은 size 요청 : {} -> 기본값 10으로 대체", size);
            size = 10;
        }

        return getOwnerOrderList(storeId, OrderStatus.PARTIALLY_SHIPPED, cursor, size);
    }

    @Transactional(readOnly = true)
    public OwnerOrderListResponse getPartiallyDeliveredOrderList(UUID storeId, UUID cursor, Integer size) {
        if (size == null || !(size == 10 || size == 30 || size == 50)) {
            log.warn("허용 되지 않은 size 요청 : {} -> 기본값 10으로 대체", size);
            size = 10;
        }

        return getOwnerOrderList(storeId, OrderStatus.PARTIALLY_DELIVERED, cursor, size);
    }



    private OwnerOrderListResponse getOwnerOrderList(UUID storeId, OrderStatus orderStatus, UUID cursor, Integer size) {

        // 2. order list 검색
        var headerList = orderRepositoryCustom.fetchOrderHeaderList(storeId, orderStatus, cursor, size);

        boolean hasNext = headerList.size() > size;
        if (hasNext) {
            headerList = headerList.subList(0, size);
        }

        // 3. order detail 검색
        var orderIdList = headerList.stream().map(OrderSummaryRow::orderId).toList();
        var orderDetailList = orderRepositoryCustom.fetchOrderDetailList(orderIdList);

        // 4. orderId로 그룹핑 : productSummary 제작
        Map<UUID, List<OwnerOrderListResponse.ProductSummary>> detailMap = orderDetailList.stream()
                .collect(Collectors.groupingBy(
                        OrderDetailRow::orderId,
                        Collectors.mapping(
                                orderMapper::toProductSummary,
                                Collectors.toList()
                        )
                ));

        // 5. orderSummary 제작
        List<OwnerOrderListResponse.OrderSummary> orderSummaryList = headerList.stream()
                .filter(order -> detailMap.containsKey(order.orderId()))
                .map(order -> orderMapper.toOrderSummary(
                        order,
                        detailMap.getOrDefault(order.orderId(), List.of()) //order detail 없다면 빈 리스트
                ))
                .toList();

        // 6. next cursor계산
        String nextCursor = orderSummaryList.isEmpty() ? null : headerList.getLast().orderId().toString();

        return new OwnerOrderListResponse(orderSummaryList, nextCursor, hasNext);

    }


    @Transactional
    public void updateOrderStatusToPreparing(UUID orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findByOrderDetailId(orderDetailId)
                .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        orderDetail.updateStatusToPreparing();
    }

    @Transactional
    public void updateOrderStatusToShipped(UUID orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findByOrderDetailId(orderDetailId)
                .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        orderDetail.updateStatusToShipped();
    }

    @Transactional
    public void updateOrderStatusToDelivered(UUID orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findByOrderDetailId(orderDetailId)
                .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        orderDetail.updateStatusToDelivered();
    }
}
