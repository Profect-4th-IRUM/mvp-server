package com.irum.come2us.domain.order.application.service;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.order.presentation.dto.response.SalesResponse;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import com.irum.come2us.domain.refund.domain.repository.RefundRepository;
import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.domain.store.domain.repository.StoreRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.StoreErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SalesService {
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final RefundRepository refundRepository;
    private final MemberUtil memberUtil;

    public SalesResponse getSalesList(UUID storeId) {
        Member member = memberUtil.getCurrentMember();
        Store store =
                storeRepository
                        .findById(storeId)
                        .orElseThrow(() -> new CommonException(StoreErrorCode.STORE_NOT_FOUND));
        memberUtil.assertMemberResourceAccess(store.getMember());

        List<Order> orders = orderRepository.findAllByStore_Id(storeId);
        List<SalesResponse.OrderSummary> orderList =
                orders.stream().map(this::toOrderSummary).toList();
        return new SalesResponse(orderList, null, false);
    }

    private SalesResponse.OrderSummary toOrderSummary(Order order) {
        String displayStatus = DisplayStatus(order);

        List<SalesResponse.ProductSummary> productList =
                order.getOrderDetails().stream()
                        .map(
                                detail ->
                                        new SalesResponse.ProductSummary(
                                                detail.getOrderDetailId(),
                                                detail.getProduct().getName(),
                                                detail.getQuantity(),
                                                detail.getPrice(),
                                                detail.getOptionName()))
                        .toList();

        return new SalesResponse.OrderSummary(
                order.getOrderId(),
                order.getDeliveryAddress().getRecipientName(),
                order.getDeliveryAddress().getRecipientContact(),
                order.getDeliveryAddress().getAddress().toString(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                0,
                order.getTotalPrice() + order.getDeliveryFee(),
                order.getDeliveryFee(),
                productList,
                displayStatus);
    }

    // 환불 존재시 환불 상태 반환, 환불 존재하지 않으면 주문 상태 반환
    private String DisplayStatus(Order order) {
        Optional<Refund> latestRefundOpt =
                refundRepository.findTopByOrderOrderByCreatedAtDesc(order);

        if (latestRefundOpt.isPresent()) {
            Refund latestRefund = latestRefundOpt.get();
            RefundStatus refundStatus = latestRefund.getRefundStatus();

            if (refundStatus != RefundStatus.REJECTED) {
                return refundStatus.name();
            }
        }
        return order.getOrderStatusAll().name();
    }
}
