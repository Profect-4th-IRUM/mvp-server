package com.irum.come2us.domain.payment.presentation.controller;

import com.irum.come2us.domain.payment.application.service.PaymentService;
import com.irum.come2us.domain.payment.presentation.dto.request.PaymentRequest;
import com.irum.come2us.domain.payment.presentation.dto.response.PaymentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@Slf4j
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("")
    public PaymentResponse paymentCreate(@RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }
}
