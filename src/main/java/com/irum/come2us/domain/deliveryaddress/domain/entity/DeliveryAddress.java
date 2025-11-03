package com.irum.come2us.domain.deliveryaddress.domain.entity;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.global.domain.BaseEntity;
import jakarta.persistence.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Table(name = "p_delivery_address")
@Where(clause = "deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeliveryAddress extends BaseEntity {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "delivery_address_id", updatable = false, nullable = false)
    private UUID deliveryAddressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Embedded private Address address;

    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_contact", nullable = false)
    private String recipientContact;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Builder
    private DeliveryAddress(
            Member member,
            Address address,
            String recipientName,
            String recipientContact,
            Boolean isDefault) {
        this.member = member;
        this.address = address;
        this.recipientName = recipientName;
        this.recipientContact = recipientContact;
        this.isDefault = isDefault;
    }

    public static DeliveryAddress create(
            Member member, Address address, String recipientName, String recipientContact) {
        return DeliveryAddress.builder()
                .member(member)
                .address(address)
                .recipientName(recipientName)
                .recipientContact(recipientContact)
                .isDefault(false)
                .build();
    }

    public void updateAddressDetail(String addressDetail) {
        if (this.address != null) {
            this.address.updateAddressDetail(addressDetail);
        }
    }

    public void updateRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public void updateRecipientContact(String recipientContact) {
        this.recipientContact = recipientContact;
    }

    public void markAsDefault() {
        this.isDefault = true;
    }

    public void unmarkAsDefault() {
        this.isDefault = false;
    }
}
