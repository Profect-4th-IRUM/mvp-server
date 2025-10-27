package com.irum.come2us.domain.discount.presentation.dto.response;

import com.irum.come2us.domain.discount.domain.entity.Discount;
import java.util.UUID;

public record DiscountInfoResponse(UUID discountId, String name, int amount, UUID productId) {
    public static DiscountInfoResponse of(Discount discount) {
        return new DiscountInfoResponse(
                discount.getId(),
                discount.getName(),
                discount.getAmount(),
                discount.getProduct().getId());
    }
}
