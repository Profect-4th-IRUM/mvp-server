package com.irum.come2us.domain.store.presentation.dto.request;

import jakarta.validation.constraints.*;

public record StoreCreateRequest(
        @NotBlank(message = "상점명은 필수 입력값입니다.") @Size(max = 50, message = "상점명은 50자 이하여야 합니다.")
                String name,
        @NotBlank(message = "연락처는 필수 입력값입니다.")
                @Pattern(
                        regexp = "^[0-9]{2,3}-[0-9]{3,4}-[0-9]{4}$",
                        message = "연락처 형식이 올바르지 않습니다. 예: 010-1234-5678")
                String contact,
        @NotBlank(message = "주소는 필수 입력값입니다.") @Size(max = 50, message = "주소는 50자 이하여야 합니다.")
                String address,
        @NotBlank(message = "사업자등록번호는 필수 입력값입니다.")
                @Pattern(regexp = "^[0-9]{10}$", message = "사업자등록번호는 10자리입니다.")
                String businessRegistrationNumber,
        @NotBlank(message = "통신판매업번호는 필수 입력값입니다.")
                @Pattern(regexp = "^[0-9]{10}$", message = "통신판매번호는 10자리입니다.")
                String telemarketingRegistrationNumber,
        @Min(value = 0, message = "배달비는 0원 이상이어야 합니다.") int deliveryFee) {}
