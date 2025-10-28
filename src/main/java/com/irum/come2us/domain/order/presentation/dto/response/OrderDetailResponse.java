package com.irum.come2us.domain.order.presentation.dto.response;

import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentStatus;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record OrderDetailResponse(
        LocalDateTime orderAt,
        PaymentStatus paymentStatus,
        PaymentMethod paymentMethod,
        int deliveryFee,
        int discountAmount,
        int totalProductPrice,
        int totalPaymentPrice,
        OrderStatus orderStatusAll,
        RefundStatus refundStatus,
        String deliveryRequest,
        AddressResponse address,
        String recipientContact,
        String recipientName,
        List<ProductResponse> productResponseList) {

    @Builder
    public record ProductResponse(
            String productName,
            String optionTitle,
            int quantity,
            int price,
            LocalDate receivedDate) {}


}
