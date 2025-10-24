package com.irum.come2us.domain.store.infrastructure.repository;

import com.irum.come2us.domain.store.domain.entity.QStore;
import com.irum.come2us.domain.store.domain.repository.StoreRepositoryCustom;
import com.irum.come2us.domain.store.presentation.dto.response.StoreListResponse;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<StoreListResponse> findStoresByCursor(UUID cursor, int size) {
        QStore store = QStore.store;

        var query = queryFactory.selectFrom(store).orderBy(store.id.desc()).limit(size);

        if (cursor != null) {
            query.where(store.id.lt(cursor));
        }

        return query.fetch().stream().map(StoreListResponse::from).toList();
    }
}
