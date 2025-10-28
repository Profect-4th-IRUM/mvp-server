package com.irum.come2us.domain.cart.presentation.dto.response;

import com.irum.come2us.domain.cart.domain.entity.Cart;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CartResponse(
        UUID cartId,
        UUID optionValueId,
        String productName,
        String optionValueName,
        String imageUrl,
        int quantity,
        int basePrice, // 상품 기본가
        int extraPrice, // 옵션 추가금
        int unitPrice, // 단가 (base + extra)
        int lineTotal // 항목 총액 (unitPrice * quantity)
        ) {
    public static CartResponse from(Cart cart) {
        var optionValue = cart.getOptionValue();
        var product = optionValue.getOptionGroup().getProduct();

        int base = product.getPrice();
        int extra = optionValue.getExtraPrice() != null ? optionValue.getExtraPrice() : 0;
        int unit = base + extra;
        int total = unit * cart.getQuantity();

        String imageUrl =
                (product.getProductImages() != null && !product.getProductImages().isEmpty())
                        ? product.getProductImages().get(0).getImageUrl()
                        : null;

        return CartResponse.builder()
                .cartId(cart.getId())
                .optionValueId(optionValue.getId())
                .productName(product.getName())
                .optionValueName(optionValue.getName())
                .imageUrl(imageUrl)
                .quantity(cart.getQuantity())
                .basePrice(base)
                .extraPrice(extra)
                .unitPrice(unit)
                .lineTotal(total)
                .build();
    }
}
