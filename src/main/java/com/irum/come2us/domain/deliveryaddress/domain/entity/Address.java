package com.irum.come2us.domain.deliveryaddress.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {

    @Column(name = "postal_code", nullable = false, length = 5)
    private String postalCode;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "sigungu", nullable = false)
    private String sigungu;

    @Column(name = "road_name", nullable = false)
    private String roadName;

    @Column(name = "address_detail")
    private String addressDetail;

    @Builder
    private Address(
            String postalCode, String city, String sigungu, String roadName, String addressDetail) {
        this.postalCode = postalCode;
        this.city = city;
        this.sigungu = sigungu;
        this.roadName = roadName;
        this.addressDetail = addressDetail;
    }

    public static Address create(
            String postalCode, String city, String sigungu, String roadName, String addressDetail) {
        return Address.builder()
                .postalCode(postalCode)
                .city(city)
                .sigungu(sigungu)
                .roadName(roadName)
                .addressDetail(addressDetail)
                .build();
    }

    public void updateAddressDetail(String addressDetail) {
        this.addressDetail = addressDetail;
    }
}
