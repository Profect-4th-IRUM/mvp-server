package com.irum.come2us.domain.discount.presentation.controller;

import com.irum.come2us.domain.discount.application.service.DiscountService;
import com.irum.come2us.domain.discount.presentation.dto.request.DiscountCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/discounts")
@RequiredArgsConstructor
@Slf4j
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<Void> registerDiscount(
            @Valid @RequestBody DiscountCreateRequest request) {
        discountService.createDiscount(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
