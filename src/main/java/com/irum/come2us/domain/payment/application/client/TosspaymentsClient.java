package com.irum.come2us.domain.payment.application.client;

import com.irum.come2us.domain.payment.application.client.config.FeignConfig;
import com.irum.come2us.domain.payment.application.client.dto.TossPaymentsRequest;
import com.irum.come2us.domain.payment.application.client.dto.TossPaymentsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "toss-api",
        url = "https://api.tosspayments.com",
        configuration = FeignConfig.class)
public interface TosspaymentsClient {
    @PostMapping("/v1/payments/confirm")
    TossPaymentsResponse confirmPayment(
            @RequestHeader("Authorization") String auth, @RequestBody TossPaymentsRequest request);
}
