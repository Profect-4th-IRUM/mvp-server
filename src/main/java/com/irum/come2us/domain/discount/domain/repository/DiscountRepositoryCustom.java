package com.irum.come2us.domain.discount.domain.repository;

import com.irum.come2us.domain.discount.presentation.dto.response.DiscountInfoResponse;
import java.util.List;
import java.util.UUID;

public interface DiscountRepositoryCustom {
    List<DiscountInfoResponse> findDiscountListByCursor(UUID storeId, UUID cursor, int pageSize);
}
