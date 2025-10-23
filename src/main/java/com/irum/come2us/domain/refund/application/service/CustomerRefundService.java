package com.irum.come2us.domain.refund.application.service;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.repository.RefundRepository;
import com.irum.come2us.domain.refund.presentation.dto.request.CustomerRefundCreateRequest;
import com.irum.come2us.domain.refund.presentation.dto.response.CustomerRefundDetailResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.RefundErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CustomerRefundService {
    private final MemberUtil memberUtil;
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public void createRefund(UUID orderId, CustomerRefundCreateRequest request) {
        assertNoRefundExistsByOrder(orderId);
        Order order = getValidOrder(orderId);
        refundRepository.save(
                Refund.create(
                        request.reason(), request.description(), order.getPayment().getAmount()));
    }

    @Transactional(readOnly = true)
    public CustomerRefundDetailResponse findRefundDetail(UUID orderId) {
        Order order = getValidOrderWithAddress(orderId);
        Refund refund = getValidRefund(orderId);
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order);
        return CustomerRefundDetailResponse.of(order, orderDetails, refund);
    }

    private void assertNoRefundExistsByOrder(UUID orderId) {
        if (refundRepository.existsByOrderId(orderId))
            throw new CommonException(RefundErrorCode.REFUND_ALREADY_EXISTS);
    }

    private Order getValidOrder(UUID orderId) {
        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));
        memberUtil.assertMemberResourceAccess(order.getMember());
        return order;
    }

    private Order getValidOrderWithAddress(UUID orderId) {
        Order order =
                orderRepository
                        .findOrderWithAddress(orderId)
                        .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));
        memberUtil.assertMemberResourceAccess(order.getMember());
        return order;
    }

    private Refund getValidRefund(UUID orderId) {
        return refundRepository
                .findByOrderId(orderId)
                .orElseThrow(() -> new CommonException(RefundErrorCode.REFUND_NOT_FOUND));
    }
}
