package com.irum.come2us.domain.refund.presentation.controller;

import com.irum.come2us.domain.refund.application.service.CustomerRefundService;
import com.irum.come2us.domain.refund.presentation.dto.request.CustomerRefundCreateRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/refund/customer")
@RequiredArgsConstructor
public class CustomerRefundController {
    private final CustomerRefundService customerRefundService;

    @PostMapping("/{orderId}")
    public ResponseEntity<Void> registerRefund(
            @PathVariable UUID orderId, @Valid @RequestBody CustomerRefundCreateRequest request) {
        customerRefundService.createRefund(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
