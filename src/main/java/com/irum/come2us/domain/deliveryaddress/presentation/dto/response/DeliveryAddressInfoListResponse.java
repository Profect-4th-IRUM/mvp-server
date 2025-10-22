package com.irum.come2us.domain.deliveryaddress.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record DeliveryAddressInfoListResponse(
        List<DeliveryAddressInfoResponse> deliveryAddressInfoList,
        UUID nextCursor,
        boolean hasNext) {}
