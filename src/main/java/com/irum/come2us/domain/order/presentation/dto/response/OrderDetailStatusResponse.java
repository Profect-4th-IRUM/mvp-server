package com.irum.come2us.domain.order.presentation.dto.response;

import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;

public record OrderDetailStatusResponse(
        String productName,
        int quantity,
        String optionName,
        AddressResponse address,
        String recipientName,
        OrderStatus orderStatus,
        String trackingNumber,
        String deliveryRequest) {
    public static OrderDetailStatusResponse from(OrderDetail orderDetail) {
        return new OrderDetailStatusResponse(
                orderDetail.getProductName(),
                orderDetail.getQuantity(),
                orderDetail.getOptionName(),
                AddressResponse.from(orderDetail.getOrder().getDeliveryAddress().getAddress()),
                orderDetail.getOrder().getDeliveryAddress().getRecipientName(),
                orderDetail.getOrderStatusIndi(),
                orderDetail.getTrackingNumber(),
                orderDetail.getOrder().getDeliveryRequest());
    }
}
