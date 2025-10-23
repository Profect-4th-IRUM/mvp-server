package com.irum.come2us.domain.order.presentation.controller;

import com.irum.come2us.domain.order.application.service.CustomerOrderService;
import com.irum.come2us.domain.order.application.service.OwnerOrderService;
import com.irum.come2us.domain.order.presentation.dto.request.CustomerOrderRequest;
import com.irum.come2us.domain.order.presentation.dto.response.CustomerOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("orders/customer")
@RequiredArgsConstructor
@Slf4j
public class CustomerOrderController {
    private final CustomerOrderService customerOrderService;

    @PostMapping("")
    public CustomerOrderResponse orderCreate(
            @RequestBody CustomerOrderRequest request
    ){
        return customerOrderService.createOrder(request);
    }
}
