package com.irum.come2us.domain.order.domain.repository;

import com.irum.come2us.domain.order.domain.entity.Order;
import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {

    Optional<OrderDetail> findByOrderDetailId(UUID orderDetailId);
    List<OrderDetail> findAllByOrder(Order order);
}
