package com.irum.come2us.domain.store.domain.entity;

import com.irum.come2us.domain.member.domain.entity.Member;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_store")
public class Store {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "store_id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "store_name", nullable = false, length = 50)
    private String name;

    @Column(name = "contact", nullable = false, columnDefinition = "char(13)")
    private String contact;

    @Column(name = "address", nullable = false, length = 50)
    private String address;

    @Column(name = "business_registration_number", nullable = false, columnDefinition = "char(10)")
    private String businessRegistrationNumber;

    @Column(
            name = "telemarketing_registration_number",
            nullable = false,
            columnDefinition = "char(10)")
    private String telemarketingRegistrationNumber;

    @Column(name = "delivery_fee", nullable = false)
    private int deliveryFee;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Builder(access = AccessLevel.PRIVATE)
    private Store(
            String name,
            String contact,
            String address,
            String businessRegistrationNumber,
            String telemarketingRegistrationNumber,
            int deliveryFee) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.telemarketingRegistrationNumber = telemarketingRegistrationNumber;
        this.deliveryFee = deliveryFee;
    }
}
