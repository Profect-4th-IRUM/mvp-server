package com.irum.come2us.domain.refund.application.service;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import com.irum.come2us.domain.refund.domain.repository.RefundRepository;
import com.irum.come2us.domain.refund.presentation.dto.request.StoreRefundCreateRequest;
import com.irum.come2us.domain.refund.presentation.dto.request.StoreRefundStatusRequest;
import com.irum.come2us.domain.refund.presentation.dto.response.*;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.RefundErrorCode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StoreRefundService {
    private final RefundRepository refundRepository;

    public StoreRefundListResponse getRefundListByStatus(RefundStatus status) {
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

    @Transactional
    public StoreRefundResponse createRefund(UUID orderId, StoreRefundCreateRequest request) {
        Order order =
                refundRepository
                        .findByOrderId(orderId) // orderRepository.findById(orderId)로 변경 예정
                        .orElseThrow(() -> new CommonException(RefundErrorCode.ORDER_NOT_FOUND));

        Refund refund =
                Refund.createRefund(
                        request.refundReason(),
                        request.description(),
                        order.getTotalPrice(),
                        order);

        return StoreRefundResponse.from(refundRepository.save(refund));
    }

    @Transactional
    public void patchRefundStatus(UUID refundId, StoreRefundStatusRequest request) {
        Refund refund =
                refundRepository
                        .findById(refundId)
                        .orElseThrow(() -> new CommonException(RefundErrorCode.REFUND_NOT_FOUND));

        refund.updateStatus(request.refundStatus());
    }

    @Transactional(readOnly = true)
    public StoreRefundDetailResponse getRefundDetail(UUID refundId) {
        Refund refund =
                refundRepository
                        .findById(refundId)
                        .orElseThrow(
                                () ->
                                        new CommonException(
                                                RefundErrorCode.REFUND_NOT_FOUND)); // 에러 코드 변경 예정

        Order order = refund.getOrder();

        var productList = List.<StoreRefundDetailResponse.ProductDetailDto>of();

        return new StoreRefundDetailResponse(
                productList,
                order.getDeliveryAddress().getRecipientName(),
                order.getDeliveryAddress().getRecipientContact(),
                order.getDeliveryAddress().getAddress(),
                order.getCreatedAt().toString(),
                refund.getCreatedAt().toString(),
                refund.getRefundId().toString(),
                refund.getRefundStatus().name(),
                order.getTotalPrice(),
                null, // TODO: 쿠폰명 - AppliedCoupon 연동 필요
                order.getDeliveryFee(),
                0, // TODO: 할인 금액 계산
                order.getTotalPrice(),
                refund.getPrice());
    }
}
