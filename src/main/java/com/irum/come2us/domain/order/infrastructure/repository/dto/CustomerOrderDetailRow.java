package com.irum.come2us.domain.order.infrastructure.repository.dto;

import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import java.util.UUID;

/** 내부용 주문 상세 DTO */
public record CustomerOrderDetailRow(
        UUID orderId,
        UUID orderDetailId,
        String productName,
        String optionName,
        int quantity,
        int price,
        OrderStatus orderStatusIndi) {}
