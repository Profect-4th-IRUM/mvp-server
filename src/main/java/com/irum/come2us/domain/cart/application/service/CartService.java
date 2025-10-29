package com.irum.come2us.domain.cart.application.service;

import com.irum.come2us.domain.cart.domain.entity.Cart;
import com.irum.come2us.domain.cart.domain.repository.CartRepository;
import com.irum.come2us.domain.cart.presentation.dto.request.CartCreateRequest;
import com.irum.come2us.domain.cart.presentation.dto.request.CartUpdateRequest;
import com.irum.come2us.domain.cart.presentation.dto.response.CartResponse;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.product.domain.entity.ProductOptionValue;
import com.irum.come2us.domain.product.domain.repository.ProductOptionValueRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.CartErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final MemberUtil memberUtil;

    @Transactional
    public CartResponse createCart(CartCreateRequest request) {
        Member currentMember = memberUtil.getCurrentMember();

        ProductOptionValue optionValue =
                productOptionValueRepository
                        .findById(request.optionValueId())
                        .orElseThrow(
                                () -> new CommonException(CartErrorCode.OPTION_VALUE_NOT_FOUND));

        Cart existing =
                cartRepository.findByMemberIdAndOptionValueId(
                        currentMember.getMemberId(), request.optionValueId());

        Cart target;
        if (existing != null) {
            int updatedQuantity = existing.getQuantity() + request.quantity();
            existing.updateQuantity(updatedQuantity);
            target = existing;
        } else {
            target = Cart.createCart(currentMember, optionValue, request.quantity());
            cartRepository.save(target);
        }

        return CartResponse.from(target);
    }

    public void updateCart(UUID cartId, CartUpdateRequest request) {
        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseThrow(() -> new CommonException(CartErrorCode.CART_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(cart.getMember());
        cart.updateQuantity(request.quantity());
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getCartListByMember() {
        Member currentMember = memberUtil.getCurrentMember();
        List<Cart> carts = cartRepository.findAllWithProductByMemberId(currentMember.getMemberId());
        return carts.stream().map(CartResponse::from).collect(Collectors.toList());
    }

    public void deleteCart(UUID cartId) {
        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseThrow(() -> new CommonException(CartErrorCode.CART_NOT_FOUND));
        memberUtil.assertMemberResourceAccess(cart.getMember());
        cartRepository.delete(cart);
    }
}
