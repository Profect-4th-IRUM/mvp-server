package com.irum.come2us.domain.refund.application.service;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import com.irum.come2us.domain.refund.domain.repository.RefundRepository;
import com.irum.come2us.domain.refund.presentation.dto.request.RefundCreateRequest;
import com.irum.come2us.domain.refund.presentation.dto.request.StoreRefundStatusRequest;
import com.irum.come2us.domain.refund.presentation.dto.response.*;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;
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
public class RefundService {

    private final MemberUtil memberUtil;
    private final RefundRepository refundRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    // Customer
    public void createRefund(UUID orderId, RefundCreateRequest request) {
        assertNoRefundExistsByOrder(orderId);
        Order order = getValidOrder(orderId);
        refundRepository.save(
                Refund.create(
                        request.reason(),
                        request.description(),
                        order.getPayment().getAmount())); // Payment 도메인 Getter 삽입 필요
    }

    @Transactional(readOnly = true)
    public RefundDetailResponse findRefundDetail(UUID orderId) {
        Order order = getValidOrderWithAddress(orderId);
        Refund refund = getValidRefund(orderId);
        List<OrderDetail> orderDetails = orderDetailRepository.findAllByOrder(order);
        return RefundDetailResponse.of(order, orderDetails, refund);
    }

    // Owner
    @Transactional(readOnly = true)
    public StoreRefundListResponse findRefundListByStatus(RefundStatus status) {
        List<Refund> refunds = refundRepository.findByRefundStatus(status);

        var orderList =
                refunds.stream()
                        .map(
                                refund -> {
                                    var order = refund.getOrder();
                                    var products = List.<RefundProductList>of();

                                    return new RefundOrderList(
                                            order.getOrderId(),
                                            order.getDeliveryAddress().getRecipientName(),
                                            order.getCreatedAt(),
                                            refund.getCreatedAt(),
                                            refund.getRefundStatus(),
                                            refund.getPrice(),
                                            products);
                                })
                        .toList();

        int total = refunds.stream().mapToInt(Refund::getPrice).sum();

        return new StoreRefundListResponse(orderList, String.valueOf(total), false, null);
    }

    public void changeRefundStatus(UUID refundId, StoreRefundStatusRequest request) {
        Refund refund =
                refundRepository
                        .findById(refundId)
                        .orElseThrow(() -> new CommonException(RefundErrorCode.REFUND_NOT_FOUND));

        refund.updateStatus(request.refundStatus());
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
