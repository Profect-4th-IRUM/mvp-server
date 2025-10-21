package com.irum.come2us.domain.deliveryaddress.infrastructure.repository;

import com.irum.come2us.domain.deliveryaddress.domain.entity.QDeliveryAddress;
import com.irum.come2us.domain.deliveryaddress.domain.repository.DeliveryAddressRepositoryCustom;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.response.DeliveryAddressInfoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DeliveryAddressRepositoryImpl implements DeliveryAddressRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private BooleanExpression belongsToMember(Long memberId, QDeliveryAddress deliveryAddress) {
        return deliveryAddress.member.memberId.eq(memberId);
    }

    private BooleanExpression ltCursor(
            LocalDateTime cursorCreatedAt, UUID cursorId, QDeliveryAddress deliveryAddress) {
        if (cursorCreatedAt == null || cursorId == null) {
            return null; // 커서가 없으면 조건 무시
        }

        return deliveryAddress
                .createdAt
                .lt(cursorCreatedAt)
                .or(
                        deliveryAddress
                                .createdAt
                                .eq(cursorCreatedAt)
                                .and(deliveryAddress.deliveryAddressId.lt(cursorId)));
    }

    @Override
    public List<DeliveryAddressInfoResponse> findDeliveryAddressByCursor(
            Long memberId, LocalDateTime cursorCreatedAt, UUID cursorId, int pageSize) {
        QDeliveryAddress deliveryAddress = QDeliveryAddress.deliveryAddress;

        return queryFactory
                .select(
                        Projections.constructor(
                                DeliveryAddressInfoResponse.class,
                                deliveryAddress.deliveryAddressId,
                                deliveryAddress.address,
                                deliveryAddress.recipientName,
                                deliveryAddress.recipientContact,
                                deliveryAddress.isDefault))
                .from(deliveryAddress)
                .where(
                        belongsToMember(memberId, deliveryAddress),
                        ltCursor(cursorCreatedAt, cursorId, deliveryAddress))
                .orderBy(deliveryAddress.createdAt.desc(), deliveryAddress.deliveryAddressId.desc())
                .limit(pageSize)
                .fetch();
    }
}
