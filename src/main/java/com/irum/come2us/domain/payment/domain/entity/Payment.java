package com.irum.come2us.domain.payment.domain.entity;

import com.irum.come2us.domain.payment.domain.entity.enums.PaymentCorp;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentMethod;
import com.irum.come2us.domain.payment.domain.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "p_payment")
public class Payment {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name="payment_id", columnDefinition = "uuid", nullable = false, updatable = false)
    private String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentCorp paymentCorp;

    // TODO: member @ManyToOne

}
