package com.irum.come2us.domain.refund.presentation.dto.response;

import com.irum.come2us.domain.deliveryaddress.domain.entity.DeliveryAddress;
import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundReason;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CustomerRefundDetailResponse(
        UUID orderId,
        String orderNum,
        List<ProductInfoDto> productList,
        RecipientAddressDto recipientAddress,
        LocalDateTime cancelDate,
        RefundStatus refundStatus,
        int refundPrice,
        RefundReason refundReason) {
    public record ProductInfoDto(
            UUID orderDetailId,
            int productPrice,
            String productName,
            String optionTitle,
            int quantity) {
        public static ProductInfoDto fromEntity(OrderDetail orderDetail) {
            return new ProductInfoDto(
                    orderDetail.getOrderDetailId(),
                    orderDetail.getPrice(),
                    orderDetail.getProductName(),
                    orderDetail.getOptionName(),
                    orderDetail.getQuantity());
        }
    }

    public record RecipientAddressDto(
            String postalCode,
            String city,
            String sigungu,
            String RoadName,
            String AddressDetail,
            String recipientName,
            String recipientContact) {
        public static RecipientAddressDto fromEntity(DeliveryAddress deliveryAddress) {
            return new RecipientAddressDto(
                    deliveryAddress.getAddress().getPostalCode(),
                    deliveryAddress.getAddress().getCity(),
                    deliveryAddress.getAddress().getSigungu(),
                    deliveryAddress.getAddress().getRoadName(),
                    deliveryAddress.getAddress().getAddressDetail(),
                    deliveryAddress.getRecipientName(),
                    deliveryAddress.getRecipientContact());
        }
    }

    public static CustomerRefundDetailResponse of(
            Order order, List<OrderDetail> orderDetails, Refund refund) {
        List<ProductInfoDto> productList =
                orderDetails.stream().map(ProductInfoDto::fromEntity).toList();
        return new CustomerRefundDetailResponse(
                order.getOrderId(),
                order.getOrderNum(),
                productList,
                RecipientAddressDto.fromEntity(order.getDeliveryAddress()),
                refund.getCreatedAt(),
                refund.getRefundStatus(),
                refund.getPrice(),
                refund.getReason());
    }
}
