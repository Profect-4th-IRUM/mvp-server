package com.irum.come2us.domain.order.application.service;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
        List<OrderStatus> orderStatuses =
                List.of(
                        OrderStatus.DELIVERED,
                        OrderStatus.PARTIALLY_DELIVERED,
                        OrderStatus.DELIVERED,
                        OrderStatus.SHIPPED,
                        OrderStatus.PREPARING);
        List<RefundStatus> refundStatuses =
                List.of(
                        RefundStatus.APPROVED,
                        RefundStatus.COMPLETED,
                        RefundStatus.PENDING,
                        RefundStatus.REJECTED,
                        RefundStatus.REQUESTED);
        List<Order> orders = orderRepository.findSalesAll(storeId);
        List<SalesResponse.OrderSummary> orderList =
                orders.stream().map(this::toOrderSummary).toList();
        return new SalesResponse(orderList, null, false);
    }

    private SalesResponse.OrderSummary toOrderSummary(Order order) {
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
        Optional<Refund> latestRefund = refundRepository.findLatestByOrder(order);

        String displayStatus = latestRefund
                .map(r -> r.getRefundStatus().name())
                .orElse(order.getOrderStatusAll().name());


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
}
