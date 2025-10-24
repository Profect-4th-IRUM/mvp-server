package com.irum.come2us.domain.store.presentation.dto.response;

import com.irum.come2us.domain.store.domain.entity.Store;
import java.util.UUID;

public record StoreInfoResponse(
        UUID id,
        String name,
        String contact,
        String address,
        String businessRegistrationNumber,
        String telemarketingRegistrationNumber) {
    public static StoreInfoResponse from(Store store) {
        return new StoreInfoResponse(
                store.getId(),
                store.getName(),
                store.getContact(),
                store.getAddress(),
                store.getBusinessRegistrationNumber(),
                store.getTelemarketingRegistrationNumber());
    }
}
