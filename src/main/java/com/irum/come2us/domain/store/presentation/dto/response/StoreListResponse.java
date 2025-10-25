package com.irum.come2us.domain.store.presentation.dto.response;

import com.irum.come2us.domain.store.domain.entity.Store;
import java.util.UUID;

public record StoreListResponse(UUID id, String name, String contact, String address) {
    public static StoreListResponse from(Store store) {
        return new StoreListResponse(
                store.getId(), store.getName(), store.getContact(), store.getAddress());
    }
}
