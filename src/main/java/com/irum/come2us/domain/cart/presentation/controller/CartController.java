package com.irum.come2us.domain.cart.presentation.controller;

import com.irum.come2us.domain.cart.application.service.CartService;
import com.irum.come2us.domain.cart.presentation.dto.request.CartCreateRequest;
import com.irum.come2us.domain.cart.presentation.dto.request.CartUpdateRequest;
import com.irum.come2us.domain.cart.presentation.dto.response.CartResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @PostMapping
    public CartResponse createCart(@Valid @RequestBody CartCreateRequest request) {
        log.info(
                "장바구니 추가 요청: memberId={}, optionValueId={}, quantity={}",
                request.memberId(),
                request.optionValueId(),
                request.quantity());
        return cartService.createCart(request);
    }

    @PatchMapping("/{cartId}")
    public CartResponse updateCart(
            @PathVariable UUID cartId, @Valid @RequestBody CartUpdateRequest request) {
        log.info("장바구니 수정 요청: cartId={}, quantity={}", cartId, request.quantity());
        return cartService.updateCart(cartId, request);
    }

    @GetMapping("/members/{memberId}")
    public List<CartResponse> getCartListByMember(@PathVariable Long memberId) {
        log.info("회원 장바구니 조회 요청: memberId={}", memberId);
        return cartService.getCartListByMember(memberId);
    }

    @DeleteMapping("/{cartId}")
    public void deleteCart(@PathVariable UUID cartId) {
        log.info("장바구니 삭제 요청: cartId={}", cartId);
        cartService.deleteCart(cartId);
    }
}
