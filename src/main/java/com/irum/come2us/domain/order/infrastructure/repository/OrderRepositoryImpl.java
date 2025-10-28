package com.irum.come2us.domain.order.infrastructure.repository;

import static com.querydsl.core.group.GroupBy.*;
import static java.util.Collections.*;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.entity.QMember;
import com.irum.come2us.domain.order.domain.entity.QOrder;
import com.irum.come2us.domain.order.domain.entity.QOrderDetail;
import com.irum.come2us.domain.order.domain.entity.enums.OrderStatus;
import com.irum.come2us.domain.order.domain.repository.OrderRepositoryCustom;
import com.irum.come2us.domain.order.infrastructure.repository.dto.CustomerOrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.CustomerOrderSummaryRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderDetailRow;
import com.irum.come2us.domain.order.infrastructure.repository.dto.OrderSummaryRow;
import com.irum.come2us.domain.refund.domain.entity.QRefund;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
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

    /** createdAt >= startDate(00:00:00). null이면 필터 없음 */
    private BooleanExpression geStartDate(LocalDate startDate, QOrder o) {
        return (startDate == null) ? null : o.createdAt.goe(startDate.atStartOfDay());
    }

    /** createdAt < (endDate + 1일 00:00:00). null이면 필터 없음 */
    private BooleanExpression ltEndDateExclusive(LocalDate endDate, QOrder o) {
        return (endDate == null) ? null : o.createdAt.lt(endDate.plusDays(1).atStartOfDay());
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
                                od.order.orderId,
                                od.orderDetailId,
                                od.productName,
                                od.quantity,
                                od.price,
                                od.optionName))
                .from(od)
                .where(od.order.orderId.in(orderIdList))
                .fetch();
    }

    /** 주목 목록 및 refundstatus 조회, pageing적용 */
    @Override
    public List<CustomerOrderSummaryRow> fetchOrderListByMember(
            Member member, LocalDate startDate, LocalDate endDate, UUID cursor, int size) {

        QOrder o = QOrder.order;
        QRefund r = QRefund.refund;
        QMember m = QMember.member;

        return queryFactory
                .select(
                        Projections.constructor(
                                CustomerOrderSummaryRow.class,
                                o.orderId,
                                o.createdAt,
                                r.refundStatus))
                .from(o)
                .leftJoin(r)
                .on(r.order.eq(o))
                .leftJoin(o.member, m)
                .where(
                        ltCursor(cursor, o),
                        m.memberId.eq(member.getMemberId()),
                        geStartDate(startDate, o),
                        ltEndDateExclusive(endDate, o))
                .orderBy(o.orderId.desc())
                .limit(size + 1)
                .fetch();
    }

    @Override
    public List<CustomerOrderDetailRow> fetchOrderDetailListByMember(List<UUID> orderIdList) {
        if (orderIdList.isEmpty()) {
            return List.of();
        }
        QOrderDetail od = QOrderDetail.orderDetail;

        return queryFactory
                .select(
                        Projections.constructor(
                                CustomerOrderDetailRow.class,
                                od.order.orderId,
                                od.orderDetailId,
                                od.productName,
                                od.optionName,
                                od.quantity,
                                od.price,
                                od.orderStatusIndi))
                .from(od)
                .where(od.order.orderId.in(orderIdList))
                .fetch();
    }
}
