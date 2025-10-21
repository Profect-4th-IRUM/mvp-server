package com.irum.come2us.domain.order.domain.repository;

import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.order.infrastructure.repository.OrderRepositoryImpl;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderSummaryRow;

import java.util.List;
import java.util.UUID;

public interface OrderRepositoryCustom {
    List<OrderSummaryRow> fetchOrderHeaderList(UUID storeId, OrderStatus orderStatus, UUID cursor, int size);
    List<OrderDetailRow> fetchOrderDetailList(List<UUID> orderIdList);
}
