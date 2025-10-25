package com.irum.come2us.domain.discount.infrastructure.repository;

import static com.irum.come2us.domain.discount.domain.entity.QDiscount.discount;
import static com.irum.come2us.domain.product.domain.entity.QProduct.product;
import static com.irum.come2us.domain.store.domain.entity.QStore.store;

import com.irum.come2us.domain.discount.domain.entity.QDiscount;
import com.irum.come2us.domain.discount.domain.repository.DiscountRepositoryCustom;
import com.irum.come2us.domain.discount.presentation.dto.response.DiscountInfoResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DiscountRepositoryImpl implements DiscountRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private BooleanExpression ltCursor(UUID cursor, QDiscount discount) {
        return cursor != null ? discount.id.lt(cursor) : null;
    }

    @Override
    public List<DiscountInfoResponse> findDiscountListByCursor(
            UUID storeId, UUID cursor, int pageSize) {
        return queryFactory
                .select(
                        Projections.constructor(
                                DiscountInfoResponse.class,
                                discount.id,
                                discount.name,
                                discount.amount,
                                discount.product))
                .join(discount.product, product)
                .fetchJoin()
                .join(product.store, store)
                .fetchJoin()
                .from(discount)
                .where(store.id.eq(storeId), ltCursor(cursor, discount))
                .orderBy(discount.id.desc())
                .limit(pageSize)
                .fetch();
    }
}
