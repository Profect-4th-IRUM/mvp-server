package com.irum.come2us.domain.cart.application.service;

import com.irum.come2us.domain.cart.domain.entity.Cart;
import com.irum.come2us.domain.cart.domain.repository.CartRepository;
import com.irum.come2us.domain.cart.presentation.dto.request.CartCreateRequest;
import com.irum.come2us.domain.cart.presentation.dto.request.CartUpdateRequest;
import com.irum.come2us.domain.cart.presentation.dto.response.CartResponse;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.domain.product.domain.entity.ProductOptionValue;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.CartErrorCode;
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
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public CartResponse createCart(CartCreateRequest request) {
        Member member =
                memberRepository
                        .findByMemberId(request.memberId())
                        .orElseThrow(() -> new CommonException(CartErrorCode.MEMBER_NOT_FOUND));

        ProductOptionValue optionValue =
                productRepository
                        .findOptionValueById(request.optionValueId())
                        .orElseThrow(
                                () -> new CommonException(CartErrorCode.OPTION_VALUE_NOT_FOUND));

        Cart existing =
                cartRepository.findByMemberIdAndOptionValueId(
                        request.memberId(), request.optionValueId());

        Cart saved;
        if (existing != null) {
            existing.updateQuantity(existing.getQuantity() + request.quantity());
            saved = existing;
            log.info(
                    "장바구니 수량 합산: cartId={}, newQuantity={}",
                    existing.getId(),
                    existing.getQuantity());
        } else {
            Cart newCart = Cart.createCart(member, optionValue, request.quantity());
            saved = cartRepository.save(newCart);
            log.info(
                    "장바구니 신규 추가: memberId={}, optionValueId={}, quantity={}",
                    request.memberId(),
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

        if (cart.getQuantity().equals(request.quantity())) {
            log.warn("수정 불필요: 동일 수량 요청 cartId={}, quantity={}", cartId, request.quantity());
            throw new CommonException(CartErrorCode.CART_NOT_MODIFIED);
        }

        cart.updateQuantity(request.quantity());
        log.info("장바구니 수량 수정 완료: cartId={}, quantity={}", cartId, request.quantity());
        return CartResponse.from(cart);
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getCartListByMember(Long memberId) {
        List<Cart> carts = cartRepository.findAllByMemberId(memberId);
        log.info("장바구니 조회 완료: memberId={}, count={}", memberId, carts.size());
        return carts.stream().map(CartResponse::from).collect(Collectors.toList());
    }

    public void deleteCart(UUID cartId) {
        Cart cart =
                cartRepository
                        .findById(cartId)
                        .orElseThrow(() -> new CommonException(CartErrorCode.CART_NOT_FOUND));

        cartRepository.delete(cart);
        log.info("장바구니 삭제 완료: cartId={}", cartId);
    }
}
