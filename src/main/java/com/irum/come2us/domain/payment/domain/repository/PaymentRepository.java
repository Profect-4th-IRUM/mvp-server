package com.irum.come2us.domain.payment.domain.repository;

import com.irum.come2us.domain.payment.domain.entity.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {}
