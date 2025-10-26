package com.irum.come2us.domain.refund.domain.repository;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.refund.domain.entity.Refund;
import com.irum.come2us.domain.refund.domain.entity.enums.RefundStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository extends JpaRepository<Refund, UUID> {
    List<Refund> findByRefundStatus(RefundStatus status);

    // RefundRepository 추후 orderRepository에서 가져오는 것으로 변경 예정
    // public interface OrderRepository extends JpaRepository<Order, UUID> {..}
    Optional<Order> findByOrder_OrderId(@Param("orderId") UUID orderId);

    Optional<Refund> findLatestByOrder(Order order);

}
