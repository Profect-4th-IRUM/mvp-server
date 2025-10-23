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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductOptionValueRepository productOptionValueRepository;
    private final MemberUtil memberUtil;

    public CartResponse createCart(CartCreateRequest request) {
        Member currentMember = memberUtil.getCurrentMember();

        // 옵션값 검증
        ProductOptionValue optionValue =
                productOptionValueRepository
                        .findById(request.optionValueId())
                        .orElseThrow(
                                () -> new CommonException(CartErrorCode.OPTION_VALUE_NOT_FOUND));

        // 기존 동일 옵션 Cart 존재 시 수량 합산
        Cart existing =
                cartRepository.findByMemberIdAndOptionValueId(
                        currentMember.getMemberId(), request.optionValueId());

        Cart saved;
        if (existing != null) {
            int oldQuantity = existing.getQuantity();
            int updatedQuantity = oldQuantity + request.quantity();
            existing.updateQuantity(updatedQuantity);
            saved = existing;

            log.info(
                    "장바구니 수량 합산: memberId={}, optionValueId={}, oldQuantity={}, newQuantity={}",
                    currentMember.getMemberId(),
                    request.optionValueId(),
                    oldQuantity,
                    updatedQuantity);
        } else {
            Cart newCart = Cart.createCart(currentMember, optionValue, request.quantity());
            saved = cartRepository.save(newCart);

            log.info(
                    "장바구니 신규 추가: memberId={}, optionValueId={}, quantity={}",
                    currentMember.getMemberId(),
                    request.optionValueId(),
                    request.quantity());
        }

        return CartResponse.from(saved);
    }

    public CartResponse updateCart(UUID cartId, CartUpdateRequest request) {
        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseThrow(() -> new CommonException(CartErrorCode.CART_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(cart.getMember());

        if (cart.getQuantity().equals(request.quantity())) {
            throw new CommonException(CartErrorCode.CART_NOT_MODIFIED);
        }

        cart.updateQuantity(request.quantity());
        log.info("장바구니 수정 완료: cartId={}, updatedQuantity={}", cartId, request.quantity());
        return CartResponse.from(cart);
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getCartListByMember() {
        Member currentMember = memberUtil.getCurrentMember();

        List<Cart> carts = cartRepository.findAllWithProductByMemberId(currentMember.getMemberId());
        log.info("장바구니 조회 완료: memberId={}, count={}", currentMember.getMemberId(), carts.size());

        return carts.stream().map(CartResponse::from).collect(Collectors.toList());
    }

    public void deleteCart(UUID cartId) {
        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseThrow(() -> new CommonException(CartErrorCode.CART_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(cart.getMember());
        cartRepository.delete(cart);

        log.info("장바구니 삭제 완료: cartId={}", cartId);
    }
}
