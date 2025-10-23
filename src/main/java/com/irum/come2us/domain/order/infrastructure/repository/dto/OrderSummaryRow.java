package com.irum.come2us.domain.order.infrastructure.repository.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/** 내부용 주문 헤더 DTO (productList 없는 버전) */
public record OrderSummaryRow(
        UUID orderId,
        String recipientName,
        String recipientContact,
        String recipientAddress,
        LocalDateTime orderDate,
        int totalProductPrice,
        int discountAmount,
        int payingAmount,
        int deliveryFee) {}
