package com.irum.come2us.domain.deliveryaddress.presentation.dto.response;

import com.irum.come2us.domain.deliveryaddress.domain.entity.Address;
import java.time.LocalDateTime;
import java.util.UUID;

public record DeliveryAddressInfoResponse(
        UUID id,
        Address address,
        String recipientName,
        String recipientContact,
        Boolean isDefault,
        LocalDateTime createdAt) {
    public static DeliveryAddressInfoResponse of(
            UUID id,
            Address address,
            String recipientName,
            String recipientContact,
            Boolean isDefault,
            LocalDateTime createdAt) {
        return new DeliveryAddressInfoResponse(
                id, address, recipientName, recipientContact, isDefault, createdAt);
    }
}
