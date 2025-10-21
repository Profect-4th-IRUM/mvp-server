package com.irum.come2us.domain.deliveryaddress.presentation.dto.response;

import java.util.List;

public record DeliveryAddressInfoListResponse(
        List<DeliveryAddressInfoResponse> memberInfoList, String nextCursor, boolean hasNext) {}
