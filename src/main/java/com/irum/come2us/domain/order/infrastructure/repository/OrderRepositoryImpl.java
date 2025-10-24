package com.irum.come2us.domain.order.infrastructure.repository;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.QOrder;
import com.irum.come2us.domain.order.domain.entity.QOrderDetail;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.order.domain.repository.OrderRepositoryCustom;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderSummaryRow;
import com.irum.come2us.domain.order.presentation.dto.response.OrderListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private BooleanExpression ltCursor(UUID cursor, QOrder qOrder) {
        // 최신순 정렬에서 다음 페이지는 "현재 커서보다 더 작은(과거) uuid
        return cursor != null ? qOrder.orderId.lt(cursor) : null;
    }

    /** [상점] 주문 목록 조회 */
    @Override
    public List<OrderSummaryRow> fetchOrderHeaderList(
            UUID storeId, OrderStatus orderStatus, UUID cursor, int size) {
        QOrder o = QOrder.order;

        return queryFactory
                .select(
                        Projections.constructor(
                                OrderSummaryRow.class,
                                o.orderId,
                                o.deliveryAddress.recipientName,
                                o.deliveryAddress.recipientContact,
                                o.deliveryAddress.address,
                                o.createdAt,
                                o.totalPrice,
                                o.payment.totalDiscountAmount,
                                o.payment.amount,
                                o.deliveryFee))
                .from(o)
                .where(
                        ltCursor(cursor, o),
                        o.store.id.eq(storeId),
                        o.orderStatusAll.eq(orderStatus))
                .orderBy(o.orderId.desc())
                .limit(size + 1) // hasnext 판별을 위해
                .fetch();
    }

    /** [상점] 주문 상세 조회 */
    public List<OrderDetailRow> fetchOrderDetailList(List<UUID> orderIdList) {
        if (orderIdList.isEmpty()) {
            return List.of();
        }

        QOrderDetail od = QOrderDetail.orderDetail;

        return queryFactory
                .select(
                        Projections.constructor(
                                OrderDetailRow.class,
                                od.orderDetailId,
                                od.productName,
                                od.quantity,
                                od.price,
                                od.optionName))
                .from(od)
                .where(od.order.orderId.in(orderIdList))
                .fetch();
    }

    @Override
    public List<Order> fetchOrderHeaderListByMember(Member member, LocalDate startDate, LocalDate endDate,  UUID cursor, int size) {

        QOrder o = QOrder.order;

        return queryFactory
                .selectFrom(o)
                .where(
                        o.member.memberId.eq(member.getMemberId()),
                        ltCursor(cursor, o),
                        o.createdAt.after(startDate.atStartOfDay()),
                        o.createdAt.before(endDate.atStartOfDay())
                )
                .orderBy(o.orderId.desc())
                .limit(size+1)
                .fetch();
    }

    @Override
    public Map<UUID, List<OrderListResponse.OrderResponse>> fetchOrderDetailListByMember(List<UUID> orderIdList) {
        if (orderIdList.isEmpty()) {
            return Map.of();
        }
        QOrder o = QOrder.order;


        return queryFactory
                .from(o)
                .join(o., orderDetail)
                .where(order.orderId.in(orderIds))
                .transform(
                        GroupBy.groupBy(order.orderId).as(
                                Projections.constructor(
                                        OrderListResponse.OrderResponse.class,
                                        order.createdAt, // orderAt
                                        GroupBy.list(
                                                Projections.constructor(
                                                        OrderListResponse.OrderResponse.ProductResponse.class,
                                                        orderDetail.orderDetailId,
                                                        orderDetail.optionName,
                                                        orderDetail.quantity,
                                                        orderDetail.orderStatusIndi, // 개별 상태
                                                        // RefundStatus 없으면 null 주입
                                                        Expressions.nullExpression(RefundStatus.class),
                                                        orderDetail.price
                                                )
                                        )
                                )
                        )
                );


        return Map.of();
    }
}
