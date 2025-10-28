package com.irum.come2us.domain.discount.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record DiscountInfoListResponse(
        List<DiscountInfoResponse> discountInfoList, UUID nextCursor, boolean hasNext) {}
