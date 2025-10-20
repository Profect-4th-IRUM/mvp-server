package com.irum.come2us.domain.order.application.service;

import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderDetailRepository orderDetailRepository;


    public void updateOrderStatusToPreparation(UUID orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findByOrderDetailId(orderDetailId)
                .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        orderDetail.updateStatusToPreparation();
    }

    public void updateOrderStatusToShipment(UUID orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findByOrderDetailId(orderDetailId)
                .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        orderDetail.updateStatusToShipment();
    }

    public void updateOrderStatusToComplete(UUID orderDetailId) {
        OrderDetail orderDetail = orderDetailRepository.findByOrderDetailId(orderDetailId)
                .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        orderDetail.updateStatusToComplete();
    }
}
