package com.irum.come2us.domain.order.domain.repository;

import com.irum.come2us.domain.order.domain.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, UUID> {

    Optional<OrderDetail> findByOrderDetailId(UUID orderDetailId);
}
