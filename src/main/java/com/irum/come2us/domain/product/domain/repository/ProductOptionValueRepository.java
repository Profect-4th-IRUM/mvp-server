package com.irum.come2us.domain.product.domain.repository;

import com.irum.come2us.domain.product.domain.entity.ProductOptionValue;
import feign.Param;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, UUID> {

    /** 락 획득까지 최대 3초 대기 TODO : 대기 시간 정책 정하기 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")})
    @Query("select pov from ProductOptionValue pov where pov.id in :ids")
    List<ProductOptionValue> findAllByIdInWithLock(@Param("id") List<UUID> ids);
}
