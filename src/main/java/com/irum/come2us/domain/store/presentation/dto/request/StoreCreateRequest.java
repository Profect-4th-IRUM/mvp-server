package com.irum.come2us.domain.store.presentation.dto.request;

import jakarta.validation.constraints.*;

public record StoreCreateRequest(
        @NotBlank(message = "상점명은 필수 입력값입니다.") String name,
        @NotBlank(message = "연락처는 필수 입력값입니다.") String contact,
        @NotBlank(message = "주소는 필수 입력값입니다.") String address,
        @NotBlank(message = "사업자등록번호는 필수 입력값입니다.") String businessRegistrationNumber,
        @NotBlank(message = "통신판매업번호는 필수 입력값입니다.") String telemarketingRegistrationNumber) {}
