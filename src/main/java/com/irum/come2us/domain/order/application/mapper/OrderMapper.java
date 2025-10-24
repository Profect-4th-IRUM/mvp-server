package com.irum.come2us.domain.order.application.mapper;

import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderSummaryRow;
import com.irum.come2us.domain.order.presentation.dto.response.OwnerOrderListResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    /** Repository DTO -> Response DTO로 변환 */
    public OwnerOrderListResponse.ProductSummary toProductSummary(OrderDetailRow detail) {
        return new OwnerOrderListResponse.ProductSummary(
                detail.orderDetailId(),
                detail.productName(),
                detail.productCounts(),
                detail.productPrice(),
                detail.optionTitle());
    }

    /** Repository DTO -> Response DTO로 변환 */
    public OwnerOrderListResponse.OrderSummary toOrderSummary(
            OrderSummaryRow header, List<OwnerOrderListResponse.ProductSummary> products) {
        return new OwnerOrderListResponse.OrderSummary(
                header.orderId(),
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
