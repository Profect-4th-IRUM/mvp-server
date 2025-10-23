package com.irum.come2us.domain.payment.application.service;

import com.irum.come2us.domain.payment.domain.entity.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class PaymentService {

    public Payment createPayment(int finalPaymentAmount) {

        return new Payment();
    }
}
