package com.irum.come2us.domain.discount.presentation.controller;

import com.irum.come2us.domain.discount.application.service.DiscountService;
import com.irum.come2us.domain.discount.presentation.dto.request.DiscountInfoUpdateRequest;
import com.irum.come2us.domain.discount.presentation.dto.request.DiscountRegisterRequest;
import com.irum.come2us.domain.discount.presentation.dto.response.DiscountInfoListResponse;
import com.irum.come2us.domain.discount.presentation.dto.response.DiscountInfoResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping("/discounts")
    public ResponseEntity<Void> registerDiscount(
            @Valid @RequestBody DiscountRegisterRequest request) {
        discountService.createDiscount(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/products/{productId}/discounts")
    public DiscountInfoResponse getDiscountInfo(@PathVariable UUID productId) {
        return discountService.findDiscountInfoByProduct(productId);
    }

    @GetMapping("/stores/{storeId}/discounts")
    public DiscountInfoListResponse getDiscountListInfoByStore(
            @PathVariable UUID storeId,
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size) {
        return discountService.findDiscountInfoListByStore(storeId, cursor, size);
    }

    @PatchMapping("/discounts/{discountId}")
    public ResponseEntity<Void> updateDiscountInfo(
            @PathVariable UUID discountId, @Valid @RequestBody DiscountInfoUpdateRequest request) {
        discountService.changeDiscountInfo(discountId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/discounts/{discountId}")
    public ResponseEntity<Void> deleteDiscount(@PathVariable UUID discountId) {
        discountService.removeDiscount(discountId);
        return ResponseEntity.noContent().build();
    }
}
