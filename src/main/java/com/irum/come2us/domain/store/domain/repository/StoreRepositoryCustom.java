package com.irum.come2us.domain.store.domain.repository;

import com.irum.come2us.domain.store.presentation.dto.response.StoreListResponse;
import java.util.List;
import java.util.UUID;

public interface StoreRepositoryCustom {
    List<StoreListResponse> findStoresByCursor(UUID cursor, int size);
}
