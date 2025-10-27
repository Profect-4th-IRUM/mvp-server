package com.irum.come2us.domain.store.domain.entity;

import com.irum.come2us.domain.deliverypolicy.domain.entity.DeliveryPolicy;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.global.constants.RegexConstants;
import com.irum.come2us.global.domain.BaseEntity;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.StoreErrorCode;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "p_store")
@SQLDelete(sql = "UPDATE p_store SET deleted_at = NOW() WHERE store_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Store extends BaseEntity {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @OneToOne(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private DeliveryPolicy deliveryPolicy;

    public void setDeliveryPolicy(DeliveryPolicy deliveryPolicy) {
        this.deliveryPolicy = deliveryPolicy;
        if (deliveryPolicy != null && deliveryPolicy.getStore() != this) {
            deliveryPolicy.setStore(this);
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Store(
            String name,
            String contact,
            String address,
            String businessRegistrationNumber,
            String telemarketingRegistrationNumber,
            Member member,
            DeliveryPolicy deliveryPolicy) {

        this.name = name;
        this.contact = validContact(contact);
        this.address = address;
        this.businessRegistrationNumber =
                validBusinessRegistrationNumber(businessRegistrationNumber);
        this.telemarketingRegistrationNumber =
                validTelemarketingRegistrationNumber(telemarketingRegistrationNumber);
        this.member = member;
        this.deliveryPolicy = null;
    }

    public static Store createStore(
            String name,
            String contact,
            String address,
            String businessRegistrationNumber,
            String telemarketingRegistrationNumber,
            Member member) {
        return Store.builder()
                .name(name)
                .contact(contact)
                .address(address)
                .businessRegistrationNumber(businessRegistrationNumber)
                .telemarketingRegistrationNumber(telemarketingRegistrationNumber)
                .member(member)
                .build();
    }

    public void updateBasicInfo(String name, String contact, String address) {
        this.name = name;
        this.contact = validContact(contact);
        this.address = address;
    }

    private static final Pattern PHONE_NUMBER_PATTERN =
            Pattern.compile(RegexConstants.PHONE_NUMBER);

    private static final Pattern TELEMARKETING_REGISTRATION_NUMBER_PATTERN =
            Pattern.compile(RegexConstants.TELEMARKETING_REGISTRATION_NUMBER);

    private static final Pattern BUSINESS_REGISTRATION_NUMBER_PATTERN =
            Pattern.compile(RegexConstants.BUSINESS_REGISTRATION_NUMBER);

    private static String validContact(String contact) {
        if (!PHONE_NUMBER_PATTERN.matcher(contact).matches()) {
            throw new CommonException(StoreErrorCode.INVALID_CONTACT);
        }
        return contact;
    }

    private static String validTelemarketingRegistrationNumber(
            String telemarketingRegistrationNumber) {
        if (!TELEMARKETING_REGISTRATION_NUMBER_PATTERN
                .matcher(telemarketingRegistrationNumber)
                .matches()) {
            throw new CommonException(StoreErrorCode.INVALID_TELEMARKETING_REGISTRATION_NUMBER);
        }
        return telemarketingRegistrationNumber;
    }

    private static String validBusinessRegistrationNumber(String businessRegistrationNumber) {
        if (!BUSINESS_REGISTRATION_NUMBER_PATTERN.matcher(businessRegistrationNumber).matches()) {
            throw new CommonException(StoreErrorCode.INVALID_BUSINESS_REGISTRATION_NUMBER);
        }
        return businessRegistrationNumber;
    }
}
