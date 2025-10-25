package com.irum.come2us.domain.order.domain.repository;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.order.infrastructure.repository.dto.CustomerOrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.CustomerOrderSummaryRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderSummaryRow;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface OrderRepositoryCustom {
    List<OrderSummaryRow> fetchOrderHeaderList(
            UUID storeId, OrderStatus orderStatus, UUID cursor, int size);

    List<OrderDetailRow> fetchOrderDetailList(List<UUID> orderIdList);

    List<CustomerOrderSummaryRow> fetchOrderListByMember(
            Member member, LocalDate startDate, LocalDate endDate, UUID cursor, int size);

    List<CustomerOrderDetailRow> fetchOrderDetailListByMember(List<UUID> orderIdList);
}
