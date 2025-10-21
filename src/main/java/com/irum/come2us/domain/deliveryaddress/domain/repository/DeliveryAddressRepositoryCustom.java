package com.irum.come2us.domain.deliveryaddress.domain.repository;

import com.irum.come2us.domain.deliveryaddress.presentation.dto.response.DeliveryAddressInfoResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DeliveryAddressRepositoryCustom {
    List<DeliveryAddressInfoResponse> findDeliveryAddressByCursor(
            Long memberId, LocalDateTime cursorCreatedAt, UUID cursorId, int pageSize);
}
