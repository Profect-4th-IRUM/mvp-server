package com.irum.come2us.domain.order.domain.repository;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {

    Optional<OrderDetail> findByOrderDetailId(UUID orderDetailId);

    @Query(
            """
       select od from OrderDetail od
       join fetch od.order o
       join fetch o.member m
       where od.orderDetailId = :orderDetailId
""")
    Optional<OrderDetail> findByOrderDetailIdWithOrderAndMember(
            @Param("orderDetailId") UUID orderDetailId);

    List<OrderDetail> findAllByOrder(Order order);

    @Modifying(clearAutomatically = true)
    @Query(
            "UPDATE OrderDetail od SET od.orderStatusIndi = 'FAILED' WHERE od.order.orderId IN :orderIds")
    int updateStatusToFailedByOrderIds(@Param("orderIds") List<UUID> orderIds);
}
