package com.irum.come2us.domain.refund.application.service;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.repository.RefundRepository;
import com.irum.come2us.domain.refund.presentation.dto.request.CustomerRefundCreateRequest;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.RefundErrorCode;
import com.irum.come2us.global.util.MemberUtil;
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

    public void createRefund(UUID orderId, CustomerRefundCreateRequest request) {
        assertNoRefundExistsByOrder(orderId);
        Order order = getValidOrder(orderId);
        refundRepository.save(
                Refund.create(
                        request.reason(), request.description(), order.getPayment().getAmount()));
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
}
