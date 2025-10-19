package com.irum.come2us.domain.store.domain.entity;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.global.constants.RegexConstants;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.StoreErrorCode;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import java.util.UUID;
import java.util.regex.Pattern;
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
            int deliveryFee,
            Member member) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.businessRegistrationNumber = businessRegistrationNumber;
        this.telemarketingRegistrationNumber = telemarketingRegistrationNumber;
        this.deliveryFee = deliveryFee;
        this.member = member;
    }

    public static Store createStore(
            String name,
            String contact,
            String address,
            String businessRegistrationNumber,
            String telemarketingRegistrationNumber,
            int deliveryFee,
            Member member) {
        return Store.builder()
                .name(name)
                .contact(contact)
                .address(address)
                .businessRegistrationNumber(businessRegistrationNumber)
                .telemarketingRegistrationNumber(telemarketingRegistrationNumber)
                .deliveryFee(deliveryFee)
                .member(member)
                .build();
    }

    public void updateBasicInfo(String name, String contact, String address) {
        this.name = name;
        this.contact = contact;
        this.address = address;
    }

    public void changeDeliveryFee(int deliveryFee) {
        this.deliveryFee = validDeliveryFee(deliveryFee);
    }

    private static final Pattern PHONE_NUMBER_PATTERN =
            Pattern.compile(RegexConstants.PHONE_NUMBER);

    private static final Pattern TELEMARKETING_REGISTRATION_NUMBER_PATTERN =
            Pattern.compile(RegexConstants.TELEMARKETING_REGISTRATION_NUMBER);

    private static final Pattern BUSINESS_REGISTRATION_NUMBER_PATTERN =
            Pattern.compile(RegexConstants.BUSINESS_REGISTRATION_NUMBER);

    private String validContact(String contact) {
        if (!PHONE_NUMBER_PATTERN.matcher(contact).matches()) {
            throw new CommonException(StoreErrorCode.INVALID_CONTACT);
        }
        return contact;
    }

    private String validTelemarketingRegistrationNumber(String telemarketingRegistrationNumber) {
        if (!TELEMARKETING_REGISTRATION_NUMBER_PATTERN
                .matcher(telemarketingRegistrationNumber)
                .matches()) {
            throw new CommonException(StoreErrorCode.INVALID_TELEMARKETING_REGISTRATION_NUMBER);
        }
        return telemarketingRegistrationNumber;
    }

    private String validBusinessRegistrationNumber(String businessRegistrationNumber) {
        if (!BUSINESS_REGISTRATION_NUMBER_PATTERN.matcher(businessRegistrationNumber).matches()) {
            throw new CommonException(StoreErrorCode.INVALID_BUSINESS_REGISTRATION_NUMBER);
        }
        return businessRegistrationNumber;
    }

    private static int validDeliveryFee(int deliveryFee) {
        if (deliveryFee < 0) {
            throw new CommonException(StoreErrorCode.INVALID_DELIVERY_FEE);
        }
        return deliveryFee;
    }
}
