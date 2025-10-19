package com.irum.come2us.domain.product.infrastructure.repository;

import com.irum.come2us.domain.product.domain.entity.QProduct;
import com.irum.come2us.domain.product.domain.repository.ProductRepositoryCustom;
import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ProductResponse> findProductsByCursor(UUID cursor, int size) {
        QProduct product = QProduct.product;

        var query =
                queryFactory
                        .selectFrom(product)
                        .where(product.isPublic.isTrue())
                        .orderBy(product.id.desc()) // Auditing 추가 후, createdAt 기반 생성일자 정렬
                        .limit(size);
        if (cursor != null) {
            query.where(product.id.lt(cursor));
        }

        return query.fetch().stream().map(ProductResponse::from).toList();
    }
}
