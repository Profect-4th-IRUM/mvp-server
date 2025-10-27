package com.irum.come2us.domain.order.presentation.controller;

import com.irum.come2us.domain.order.application.service.SalesService;
import com.irum.come2us.domain.order.presentation.dto.response.SalesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class SalesController {
    private final SalesService salesService;

    //   정산 내역
    @GetMapping("/{storeId}/sales")
    public ResponseEntity<SalesResponse> salesResponse(@PathVariable UUID storeId) {
        SalesResponse response = salesService.getSalesList(storeId);
        return ResponseEntity.ok(response);
    }

}
