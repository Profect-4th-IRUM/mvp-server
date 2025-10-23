package com.irum.come2us.domain.refund.domain.repository;

import com.irum.come2us.domain.refund.domain.entity.Refund;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefundRepository extends JpaRepository<Refund, UUID> {
    @Query("SELECT COUNT(r)>0 FROM Refund r WHERE r.order.orderId =: orderId")
    boolean existsByOrderId(@Param("orderId") UUID orderId);

    @Query("SELECT r FROM Refund r WHERE r.order.orderId =: orderId")
    Optional<Refund> findByOrderId(@Param("orderId") UUID orderId);
}
