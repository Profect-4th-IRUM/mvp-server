package com.irum.come2us.domain.order.application.service;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.payment.domain.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderBatchService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final OrderDetailRepository orderDetailRepository;

    private static final int TIMEOUT_MINUTES = 5; // 5분 기준

    public void processStalePendingOrders() {
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(TIMEOUT_MINUTES);

        // 타임아웃된 주문, 결제 조회
        List<Order> staleOrders = orderRepository.findStalePendingOrdersWithPayment(cutoffTime);

        if (staleOrders.isEmpty()) {
            return; // 처리할 주문 없음
        }

        // 대상 ID 수집
        List<UUID> orderIds = staleOrders.stream().map(Order::getOrderId).toList();

        List<UUID> paymentIds =
                staleOrders.stream().map(order -> order.getPayment().getPaymentId()).toList();

        // OrderDetail 상태 변경
        int detailCount = orderDetailRepository.updateStatusToFailedByOrderIds(orderIds);

        // Payment 상태 변경
        int paymentCount = paymentRepository.updateStatusToFailedByIds(paymentIds);

        // Order 상태 변경
        int orderCount = orderRepository.updateStatusToFailedByIds(orderIds);

        log.info(
                "[주문 타임아웃 배치] {}개 주문, {}개 결제, {}개 주문상세 'FAILED' 처리 완료",
                orderCount,
                paymentCount,
                detailCount);
    }
}
