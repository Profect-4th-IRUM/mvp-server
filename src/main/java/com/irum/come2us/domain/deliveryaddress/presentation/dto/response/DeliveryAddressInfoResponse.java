package com.irum.come2us.domain.deliveryaddress.presentation.dto.response;

import com.irum.come2us.domain.deliveryaddress.domain.entity.Address;
import java.util.UUID;

public record DeliveryAddressInfoResponse(
        UUID id,
        Address address,
        String recipientName,
        String recipientContact,
        Boolean isDefault) {
    public static DeliveryAddressInfoResponse of(
            UUID id,
            Address address,
            String recipientName,
            String recipientContact,
            Boolean isDefault) {
        return new DeliveryAddressInfoResponse(
                id, address, recipientName, recipientContact, isDefault);
    }
}
