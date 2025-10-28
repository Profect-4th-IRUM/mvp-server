package com.irum.come2us.domain.order.infrastructure.repository.dto;

import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/** 내부용 주문 헤더 DTO (productList 없는 버전) */
public record CustomerOrderSummaryRow(
        UUID orderId, LocalDateTime orderAt, RefundStatus refundStatus) {}
