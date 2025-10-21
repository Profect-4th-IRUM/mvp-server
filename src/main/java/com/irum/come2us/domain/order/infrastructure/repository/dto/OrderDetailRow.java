package com.irum.come2us.domain.order.infrastructure.repository.dto;

import java.util.UUID;

/**
 * 내부용 주문 상세 DTO
 */
public record OrderDetailRow(
        UUID orderId,
        UUID orderDetailId,
        String productName,
        int productCounts,
        int productPrice,
        String optionTitle
) {}