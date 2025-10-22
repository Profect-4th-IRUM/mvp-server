package com.irum.come2us.domain.deliverypolicy.presentation.controller;

import com.irum.come2us.domain.deliverypolicy.application.service.DeliveryPolicyService;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.request.DeliveryPolicyCreateRequest;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.response.DeliveryPolicyCreateResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deliver-policy")
@RequiredArgsConstructor
@Slf4j
public class DeliveryPolicyController {
    private final DeliveryPolicyService deliveryPolicyService;

    @PostMapping
    public ResponseEntity<DeliveryPolicyCreateResponse> registerStore(
            @Valid @RequestBody DeliveryPolicyCreateRequest request) {
        log.info("판매자 회원가입 요청: {}", request);
        UUID deliveryPolicyId = deliveryPolicyService.createDeliveryPolicy(request);
        return ResponseEntity.ok(new DeliveryPolicyCreateResponse(deliveryPolicyId));
    }
}
