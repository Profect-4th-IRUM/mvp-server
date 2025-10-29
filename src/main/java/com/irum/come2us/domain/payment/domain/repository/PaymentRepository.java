package com.irum.come2us.domain.payment.domain.repository;

import com.irum.come2us.domain.payment.domain.entity.Payment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Payment p SET p.paymentStatus = 'FAILED' WHERE p.paymentId IN :paymentIds")
    int updateStatusToFailedByIds(@Param("paymentIds") List<UUID> paymentIds);

    @Query("SELECT p.totalDiscountAmount FROM Payment p WHERE p.paymentId = :paymentId")
    Integer getTotalDiscountByPaymentId(@Param("paymentId") UUID paymentId);
}
