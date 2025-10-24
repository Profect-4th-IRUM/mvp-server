package com.irum.come2us.domain.order.application.mapper;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.presentation.dto.request.CustomerOrderRequest;
import com.irum.come2us.domain.order.presentation.dto.response.AddressResponse;
import com.irum.come2us.domain.order.presentation.dto.response.CustomerOrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class CustomerOrderMapper {

    public CustomerOrderResponse toCustomerOrderResponse(Order order, List<OrderDetail> orderDetailList) {
        List<CustomerOrderResponse.ProductSummary> productSummaryList = orderDetailList.stream()
                .map(this::toProductSummary)
                .toList();

        return CustomerOrderResponse.builder()
                .orderId(order.getOrderId())
                .address(AddressResponse.from(order.getDeliveryAddress().getAddress()))
                .totalPrice(order.getTotalPrice())
                .prodcutList(productSummaryList)
                .build();
    }

    private CustomerOrderResponse.ProductSummary toProductSummary(OrderDetail orderDetail) {
        return CustomerOrderResponse.ProductSummary.builder()
                .orderDetailId(orderDetail.getOrderDetailId())
                .productName(orderDetail.getProductName())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .build();
    }
}
