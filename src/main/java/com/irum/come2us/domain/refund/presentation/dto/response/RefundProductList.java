package com.irum.come2us.domain.refund.presentation.dto.response;

import java.util.UUID;

public record RefundProductList(
        UUID orderDetailId,
        String productName,
        Integer quantity,
        Integer productPrice,
        String optionTitle) {}
