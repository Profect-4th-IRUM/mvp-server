package com.irum.come2us.domain.cart.presentation.dto.response;

import com.irum.come2us.domain.cart.domain.entity.Cart;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CartResponse(
        UUID cartId,
        Long memberId,
        UUID optionValueId,
        String optionValueName,
        Integer quantity,
        Integer extraPrice,
        Integer totalPrice) {
    public static CartResponse from(Cart cart) {
        Integer extraPrice = cart.getOptionValue().getExtraPrice();
        int basePrice = cart.getOptionValue().getOptionGroup().getProduct().getPrice();
        int total = (basePrice + (extraPrice != null ? extraPrice : 0)) * cart.getQuantity();

        return CartResponse.builder()
                .cartId(cart.getId())
                .memberId(cart.getMember().getMemberId())
                .optionValueId(cart.getOptionValue().getId())
                .optionValueName(cart.getOptionValue().getName())
                .quantity(cart.getQuantity())
                .extraPrice(extraPrice)
                .totalPrice(total)
                .build();
    }
}
