package com.irum.come2us.domain.order.application.service;

import com.irum.come2us.domain.coupon.domain.repository.AppliedCouponRepository;
import com.irum.come2us.domain.order.application.mapper.OrderMapper;
import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.order.domain.repository.OrderDetailRepository;
import com.irum.come2us.domain.order.domain.repository.OrderRepository;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderSummaryRow;
import com.irum.come2us.domain.order.presentation.dto.request.OwnerOrderShippedRequest;
import com.irum.come2us.domain.order.presentation.dto.response.AddressResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OrderDetailResponse;
import com.irum.come2us.domain.order.presentation.dto.response.OwnerOrderListResponse;
import com.irum.come2us.domain.payment.domain.repository.PaymentRepository;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import com.irum.come2us.domain.refund.domain.repository.RefundRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.OrderErrorCode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OwnerOrderService {
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final OrderMapper orderMapper;
    private final AppliedCouponRepository appliedCouponRepository;

    @Transactional(readOnly = true)
    public OwnerOrderListResponse getPreparingOrderList(UUID storeId, UUID cursor, Integer size) {
        // 1. size validation
        if (size == null || !(size == 10 || size == 30 || size == 50)) {
            log.warn("허용 되지 않은 size 요청 : {} -> 기본값 10으로 대체", size);
            size = 10;
        }

        return getOwnerOrderList(storeId, OrderStatus.PREPARING, cursor, size);
    }

    @Transactional(readOnly = true)
    public OwnerOrderListResponse getPartiallyShippedOrderList(
            UUID storeId, UUID cursor, Integer size) {
        if (size == null || !(size == 10 || size == 30 || size == 50)) {
            log.warn("허용 되지 않은 size 요청 : {} -> 기본값 10으로 대체", size);
            size = 10;
        }

        return getOwnerOrderList(storeId, OrderStatus.PARTIALLY_SHIPPED, cursor, size);
    }

    @Transactional(readOnly = true)
    public OwnerOrderListResponse getPartiallyDeliveredOrderList(
            UUID storeId, UUID cursor, Integer size) {
        if (size == null || !(size == 10 || size == 30 || size == 50)) {
            log.warn("허용 되지 않은 size 요청 : {} -> 기본값 10으로 대체", size);
            size = 10;
        }

        return getOwnerOrderList(storeId, OrderStatus.PARTIALLY_DELIVERED, cursor, size);
    }

    @Transactional(readOnly = true)
    public OwnerOrderListResponse getDeliveredOrderList(UUID storeId, UUID cursor, Integer size) {
        if (size == null || !(size == 10 || size == 30 || size == 50)) {
            log.warn("허용 되지 않은 size 요청 : {} -> 기본값 10으로 대체", size);
            size = 10;
        }

        return getOwnerOrderList(storeId, OrderStatus.DELIVERED, cursor, size);
    }

    private OwnerOrderListResponse getOwnerOrderList(
            UUID storeId, OrderStatus orderStatus, UUID cursor, Integer size) {

        // 2. order list 검색
        var headerList = orderRepository.fetchOrderHeaderList(storeId, orderStatus, cursor, size);

        boolean hasNext = headerList.size() > size;
        if (hasNext) {
            headerList = headerList.subList(0, size);
        }

        // 3. order detail 검색
        var orderIdList = headerList.stream().map(OrderSummaryRow::orderId).toList();
        List<OrderDetailRow> orderDetailList = orderRepository.fetchOrderDetailList(orderIdList);

        // 4. orderId로 그룹핑 : productSummary 제작
        Map<UUID, List<OwnerOrderListResponse.ProductSummary>> detailMap =
                orderDetailList.stream()
                        .collect(
                                Collectors.groupingBy(
                                        OrderDetailRow::orderId,
                                        Collectors.mapping(
                                                orderMapper::toProductSummary,
                                                Collectors.toList())));

        // 5. orderSummary 제작
        List<OwnerOrderListResponse.OrderSummary> orderSummaryList =
                headerList.stream()
                        .filter(order -> detailMap.containsKey(order.orderId()))
                        .map(
                                order ->
                                        orderMapper.toOrderSummary(
                                                order,
                                                detailMap.getOrDefault(
                                                        order.orderId(),
                                                        List.of()) // order detail 없다면 빈 리스트
                                                ))
                        .toList();

        // 6. next cursor계산
        UUID nextCursor = orderSummaryList.isEmpty() ? null : headerList.getLast().orderId();

        return new OwnerOrderListResponse(orderSummaryList, nextCursor, hasNext);
    }

    public void updateOrderStatusToPreparing(UUID orderDetailId) {
        OrderDetail orderDetail =
                orderDetailRepository
                        .findByOrderDetailId(orderDetailId)
                        .orElseThrow(
                                () -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        orderDetail.updateStatusToPreparing();

        // Order 전체 상태 업데이트
        Order order =
                orderRepository
                        .findByOrderId(orderDetail.getOrder().getOrderId())
                        .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));
        OrderStatus newOrderStatus = aggregateOrderStatus(orderDetail);
        order.updateOrderStatus(newOrderStatus);
    }

    public void updateOrderStatusToShipped(UUID orderDetailId, OwnerOrderShippedRequest request) {
        OrderDetail orderDetail =
                orderDetailRepository
                        .findByOrderDetailId(orderDetailId)
                        .orElseThrow(
                                () -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        orderDetail.updateStatusToShipped(request.trackingNumber());

        // Order 전체 상태 업데이트
        Order order =
                orderRepository
                        .findByOrderId(orderDetail.getOrder().getOrderId())
                        .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));
        OrderStatus newOrderStatus = aggregateOrderStatus(orderDetail);
        order.updateOrderStatus(newOrderStatus);
    }

    public void updateOrderStatusToDelivered(UUID orderDetailId) {
        OrderDetail orderDetail =
                orderDetailRepository
                        .findByOrderDetailId(orderDetailId)
                        .orElseThrow(
                                () -> new CommonException(OrderErrorCode.ORDER_DETAIL_NOT_FOUND));

        orderDetail.updateStatusToDelivered();

        // Order 전체 상태 업데이트
        Order order =
                orderRepository
                        .findByOrderId(orderDetail.getOrder().getOrderId())
                        .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));
        OrderStatus newOrderStatus = aggregateOrderStatus(orderDetail);
        order.updateOrderStatus(newOrderStatus);
    }

    /** OrderDetail 상태 목록을 기반으로 집계된(Aggregated) Order의 상태를 결정 */
    private OrderStatus aggregateOrderStatus(OrderDetail orderDetail) {

        //        Order order =
        //                orderRepository
        //                        .findByOrderId(orderDetail.getOrder().getOrderId())
        //                        .orElseThrow(() -> new
        // CommonException(OrderErrorCode.ORDER_NOT_FOUND));
        Order order = orderDetail.getOrder();
        List<OrderDetail> orderDetailList = orderDetailRepository.findAllByOrder(order);
        List<OrderStatus> orderStatusList =
                orderDetailList.stream().map(OrderDetail::getOrderStatusIndi).toList();

        if (orderStatusList.isEmpty()) {
            // 주문 상세가 없는 경우 : 준비중
            return OrderStatus.PREPARING;
        }

        int totalCount = orderStatusList.size();

        // 1. 각 상태별로 개수
        long preparingCount =
                orderStatusList.stream().filter(s -> s == OrderStatus.PREPARING).count();
        long shippingCount = orderStatusList.stream().filter(s -> s == OrderStatus.SHIPPED).count();
        long deliveredCount =
                orderStatusList.stream().filter(s -> s == OrderStatus.DELIVERED).count();

        // "배송 완료" (모든 상품이 '배송완료')
        if (deliveredCount == totalCount) {
            return OrderStatus.DELIVERED;
        }
        // "부분 배송완료" ('배송완료'가 1개 이상)
        else if (deliveredCount > 0) {
            return OrderStatus.PARTIALLY_DELIVERED;
        }
        // "부분 배송중" ('배송중'과 '준비중'이 혼재)
        else if (shippingCount > 0 && preparingCount > 0) {
            return OrderStatus.PARTIALLY_SHIPPED;
        }
        // "배송중" ('배송중'만 있음)
        else if (shippingCount > 0) {
            return OrderStatus.SHIPPED;
        }
        // "준비중" (모든 상품이 '준비중')
        else if (preparingCount == totalCount) {
            return OrderStatus.PREPARING;
        }

        // 모든 정책에 해당하지 않는 경우
        return OrderStatus.PREPARING;
    }

    @Transactional(readOnly = true)
    public OrderDetailResponse detailResponse(UUID orderId) {
        Order order =
                orderRepository
                        .findByOrderId(orderId)
                        .orElseThrow(() -> new CommonException(OrderErrorCode.ORDER_NOT_FOUND));

        List<OrderDetailResponse.ProductResponse> productList =
                order.getOrderDetails().stream()
                        .map(
                                od ->
                                        OrderDetailResponse.ProductResponse.builder()
                                                .productName(od.getProductName())
                                                .optionTitle(od.getOptionName())
                                                .quantity(od.getQuantity())
                                                .price(od.getPrice())
                                                .receivedDate(
                                                        od.getArrivedDate() != null
                                                                ? od.getArrivedDate().toLocalDate()
                                                                : null)
                                                .build())
                        .toList();
        String couponName = getCouponName(order.getPayment().getPaymentId());
        int discountAmount = getDiscountAmount(order.getPayment().getPaymentId());
        // 아직 결제 상태 Field 없음
        String trackingNumber =
                order.getOrderDetails().stream()
                        .map(od -> od.getTrackingNumber())
                        .filter(Objects::nonNull)
                        .findFirst()
                        .map(dt -> dt.toString())
                        .orElse(null);

        String arrivedDate =
                order.getOrderDetails().stream()
                        .map(od -> od.getArrivedDate())
                        .filter(Objects::nonNull)
                        .findFirst()
                        .map(dt -> dt.toString())
                        .orElse(null);

        int deliveryFee = order.getDeliveryFee() != null ? order.getDeliveryFee() : 0;
        int totalProductPrice = order.getTotalPrice() != null ? order.getTotalPrice() : 0;
        int totalPaymentPrice = totalProductPrice + deliveryFee - discountAmount;

        RefundStatus refundStatus =
                refundRepository
                        .findFirstByOrderOrderByCreatedAtDesc(order)
                        .map(Refund::getRefundStatus)
                        .orElse(null);

        AddressResponse address = AddressResponse.from(order.getDeliveryAddress().getAddress());

        return new OrderDetailResponse(
                order.getCreatedAt(),
                order.getPayment() != null ? order.getPayment().getPaymentStatus() : null,
                order.getPayment() != null ? order.getPayment().getPaymentMethod() : null,
                deliveryFee,
                discountAmount,
                totalProductPrice,
                totalPaymentPrice,
                order.getOrderStatusAll(),
                refundStatus,
                order.getDeliveryRequest(),
                address,
                order.getDeliveryAddress().getRecipientContact(),
                order.getDeliveryAddress().getRecipientName(),
                productList);
    }

    private String getCouponName(UUID paymentId) {
        return appliedCouponRepository.findByPayment_PaymentId(paymentId).stream()
                .findFirst()
                .map(ac -> ac.getCoupon().getName())
                .orElse(null);
    }

    private int getDiscountAmount(UUID paymentId) {
        Integer sum = paymentRepository.getTotalDiscountByPaymentId(paymentId);
        return sum != null ? sum : 0;
    }
}
