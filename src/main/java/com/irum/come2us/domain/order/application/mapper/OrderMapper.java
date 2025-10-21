package com.irum.come2us.domain.order.application.mapper;

import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderSummaryRow;
import com.irum.come2us.domain.order.presentation.dto.response.PaymentOrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    /**
     * Repository DTO -> Response DTO로 변환
     */

    public PaymentOrderResponse.ProductSummary toProductSummary(OrderDetailRow detail) {
        return new PaymentOrderResponse.ProductSummary(
                detail.orderDetailId().toString(),
                detail.productName(),
                detail.productCounts(),
                detail.productPrice(),
                detail.optionTitle()
        );
    }

    /**
     * Repository DTO -> Response DTO로 변환
     */
    public PaymentOrderResponse.OrderSummary toOrderSummary(OrderSummaryRow header, List<PaymentOrderResponse.ProductSummary> products) {
        return new PaymentOrderResponse.OrderSummary(
                header.orderId().toString(),
                header.recipientName(),
                header.recipientContact(),
                header.recipientAddress(),
                header.orderDate(),
                header.totalProductPrice(),
                header.discountAmount(),
                header.payingAmount(),
                header.deliveryFee(),
                products // 이미 변환된 ProductSummary 리스트를 받음
        );
    }
}
