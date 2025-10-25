package com.irum.come2us.domain.order.application.mapper;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.infrastructure.repository.dto.CustomerOrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.CustomerOrderSummaryRow;
import com.irum.come2us.domain.order.presentation.dto.response.AddressResponse;
import com.irum.come2us.domain.order.presentation.dto.response.CustomerOrderListResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailResponse;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CustomerOrderMapper {

    public static OrderDetailResponse toOrderDetailResponse(
            Order order, List<OrderDetail> orderDetailList, Refund refund) {
        List<OrderDetailResponse.ProductResponse> pList =
                orderDetailList.stream().map(CustomerOrderMapper::toProductResponse).toList();

        RefundStatus refundStatus;
        if (refund == null) {
            refundStatus = null;
        } else {
            refundStatus = refund.getRefundStatus();
        }

        return OrderDetailResponse.builder()
                .orderAt(order.getCreatedAt())
                .paymentStatus(order.getPayment().getPaymentStatus())
                .paymentMethod(order.getPayment().getPaymentMethod())
                .deliveryFee(order.getDeliveryFee())
                .discountAmount(order.getPayment().getTotalDiscountAmount())
                .totalProductPrice(order.getTotalPrice())
                .totalPaymentPrice(order.getPayment().getAmount())
                .orderStatusAll(order.getOrderStatusAll())
                .refundStatus(refundStatus)
                .deliveryRequest(order.getDeliveryRequest())
                .address(AddressResponse.from(order.getDeliveryAddress().getAddress()))
                .recipientContact(order.getDeliveryAddress().getRecipientContact())
                .recipientName(order.getDeliveryAddress().getRecipientName())
                .productResponseList(pList)
                .build();
    }

    private static OrderDetailResponse.ProductResponse toProductResponse(OrderDetail orderDetail) {

        return OrderDetailResponse.ProductResponse.builder()
                .receivedDate(orderDetail.getArrivedDate().toLocalDate())
                .productName(orderDetail.getProductName())
                .optionTitle(orderDetail.getOptionName())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .build();
    }

    public static CustomerOrderListResponse.OrderResponse toOrderResponse(
            CustomerOrderSummaryRow or, List<CustomerOrderListResponse.ProductResponse> pList) {

        return CustomerOrderListResponse.OrderResponse.builder()
                .orderId(or.orderId())
                .orderAt(or.orderAt())
                .refundStatus(or.refundStatus())
                .productResponseList(pList)
                .build();
    }

    public static CustomerOrderListResponse.ProductResponse toProductResponse(
            CustomerOrderDetailRow c) {
        return CustomerOrderListResponse.ProductResponse.builder()
                .orderStatus(c.orderStatusIndi())
                .orderDetailId(c.orderDetailId())
                .optionName(c.optionName())
                .price(c.price())
                .productName(c.productName())
                .quantity(c.quantity())
                .build();
    }
}
