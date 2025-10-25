package com.irum.come2us.domain.product.infrastructure.repository;

import com.irum.come2us.domain.product.domain.entity.QProduct;
import com.irum.come2us.domain.product.domain.repository.ProductRepositoryCustom;
import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private BooleanExpression ltCursor(UUID cursor, QProduct product) {
        return cursor != null ? product.id.lt(cursor) : null;
    }

    private BooleanExpression containsKeyword(String keyword, QProduct product) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        return product.name.containsIgnoreCase(keyword);
    }

    @Override
    public List<ProductResponse> findProductsByCursor(UUID cursor, int size) {
        QProduct product = QProduct.product;

        return queryFactory
                .select(
                        Projections.constructor(
                                ProductResponse.class,
                                product.id,
                                product.name,
                                product.description,
                                product.detailDescription,
                                product.price,
                                product.isPublic,
                                product.avgRating,
                                product.reviewCount,
                                product.category.categoryId,
                                product.category.name))
                .from(product)
                .where(product.isPublic.isTrue(), ltCursor(cursor, product))
                .orderBy(product.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<ProductResponse> findProductsByKeyword(UUID cursor, int size, String keyword) {
        QProduct product = QProduct.product;

        return queryFactory
                .select(
                        Projections.constructor(
                                ProductResponse.class,
                                product.id,
                                product.name,
                                product.description,
                                product.detailDescription,
                                product.price,
                                product.isPublic,
                                product.avgRating,
                                product.reviewCount,
                                product.category.categoryId,
                                product.category.name))
                .from(product)
                .where(
                        product.isPublic.isTrue(),
                        ltCursor(cursor, product),
                        containsKeyword(keyword, product))
                .orderBy(product.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<ProductResponse> findProductsByStoreWithCursor(
            UUID storeId, UUID cursor, int size) {
        QProduct product = QProduct.product;

        return queryFactory
                .select(
                        Projections.constructor(
                                ProductResponse.class,
                                product.id,
                                product.name,
                                product.description,
                                product.detailDescription,
                                product.price,
                                product.isPublic,
                                product.avgRating,
                                product.reviewCount))
                .from(product)
                .where(product.store.id.eq(storeId), ltCursor(cursor, product))
                .orderBy(product.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<ProductResponse> findProductsByCategoryIds(
            UUID cursor, int size, List<UUID> categoryIds) {
        QProduct product = QProduct.product;

        return queryFactory
                .select(
                        Projections.constructor(
                                ProductResponse.class,
                                product.id,
                                product.name,
                                product.description,
                                product.detailDescription,
                                product.price,
                                product.isPublic,
                                product.avgRating,
                                product.reviewCount,
                                product.category.categoryId,
                                product.category.name))
                .from(product)
                .where(
                        product.isPublic.isTrue(),
                        ltCursor(cursor, product),
                        product.category.categoryId.in(categoryIds))
                .orderBy(product.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<ProductResponse> findProductsByCategoryIdsAndKeyword(
            UUID cursor, int size, List<UUID> categoryIds, String keyword) {
        QProduct product = QProduct.product;

        return queryFactory
                .select(
                        Projections.constructor(
                                ProductResponse.class,
                                product.id,
                                product.name,
                                product.description,
                                product.detailDescription,
                                product.price,
                                product.isPublic,
                                product.avgRating,
                                product.reviewCount,
                                product.category.categoryId,
                                product.category.name))
                .from(product)
                .where(
                        product.isPublic.isTrue(),
                        ltCursor(cursor, product),
                        product.category.categoryId.in(categoryIds),
                        containsKeyword(keyword, product))
                .orderBy(product.id.desc())
                .limit(size)
                .fetch();
    }
}
