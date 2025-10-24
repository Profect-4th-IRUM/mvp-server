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

    public static CustomerOrderResponse toCustomerOrderResponse(Order order, List<OrderDetail> orderDetailList) {
        List<CustomerOrderResponse.ProductSummary> productSummaryList = orderDetailList.stream()
                .map(CustomerOrderMapper::toProductSummary)
                .toList();

        return CustomerOrderResponse.builder()
                .orderId(order.getOrderId())
                .address(AddressResponse.from(order.getDeliveryAddress().getAddress()))
                .totalProductPrice(order.getTotalPrice())
                .totalDiscountAmount(order.getPayment().getTotalDiscountAmount())
                .totalPaymentAmount(order.getPayment().getAmount())
                .prodcutList(productSummaryList)
                .build();
    }

    private static CustomerOrderResponse.ProductSummary toProductSummary(OrderDetail orderDetail) {
        return CustomerOrderResponse.ProductSummary.builder()
                .orderDetailId(orderDetail.getOrderDetailId())
                .productName(orderDetail.getProductName())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .build();
    }
}
