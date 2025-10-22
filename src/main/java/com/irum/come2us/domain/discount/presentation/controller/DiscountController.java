package com.irum.come2us.domain.discount.presentation.controller;

import com.irum.come2us.domain.discount.application.service.DiscountService;
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
@RequestMapping("/discounts")
@RequiredArgsConstructor
@Slf4j
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<Void> registerDiscount(
            @Valid @RequestBody DiscountRegisterRequest request) {
        discountService.createDiscount(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/info/{discountId}")
    public DiscountInfoResponse getDiscountInfo(@PathVariable UUID discountId) {
        return discountService.findDiscountInfoByProduct(discountId);
    }

    @GetMapping("/{storeId}/info/")
    public DiscountInfoListResponse getDiscountInfo(
            @PathVariable UUID storeId,
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer pageSize) {
        return discountService.findDiscountInfoListByStore(storeId, cursor, pageSize);
    }
}
