package com.irum.come2us.domain.deliveryaddress.presentation.dto.request;

import com.irum.come2us.domain.deliveryaddress.domain.entity.Address;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;

public record DeliveryAddressRegisterRequest(
        @NotBlank(message = "우편번호는 필수 입력값입니다.") String postalCode,
        @NotBlank(message = "도시명은 필수 입력값입니다.") String city,
        @NotBlank(message = "시・군・구 정보는 필수 입력값입니다.") String sigungu,
        @NotBlank(message = "도로명은 필수 입력값입니다.") String roadName,
        @Nullable String addressDetail,
        @Nullable String recipientName,
        @Nullable String recipientContact) {
    public Address address() {
        return Address.create(
                this.postalCode, this.city, this.sigungu, this.roadName, this.addressDetail);
    }
}
