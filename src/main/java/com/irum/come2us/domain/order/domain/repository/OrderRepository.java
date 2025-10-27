package com.irum.come2us.domain.order.domain.repository;

import com.irum.come2us.domain.order.domain.entity.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByOrderId(UUID orderId);

    @Query(
            "SELECT o FROM Order o JOIN FETCH o.payment p "
                    + "WHERE o.orderStatusAll = 'PENDING' AND o.createdAt < :cutoffTime")
    List<Order> findStalePendingOrdersWithPayment(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Order o SET o.orderStatusAll = 'FAILED' WHERE o.orderId IN :orderIds")
    int updateStatusToFailedByIds(@Param("orderIds") List<UUID> orderIds);
}
