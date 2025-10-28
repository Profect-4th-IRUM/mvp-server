package com.irum.come2us.domain.discount.domain.repository;

import com.irum.come2us.domain.discount.domain.entity.Discount;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiscountRepository
        extends JpaRepository<Discount, UUID>, DiscountRepositoryCustom {

    @Query(
            "SELECT CASE WHEN COUNT(d) > 0 THEN TRUE ELSE FALSE END FROM Discount d WHERE d.product.id = :productId")
    boolean existsByProductId(@Param("productId") UUID productId);

    @Query("SELECT d FROM Discount d JOIN FETCH d.product p WHERE d.product.id = :productId")
    Optional<Discount> findByProductId(@Param("productId") UUID productId);
}
