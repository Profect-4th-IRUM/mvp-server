package com.irum.come2us.domain.store.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record StoreUpdateRequest(
        @NotBlank(message = "상점명은 필수 입력값입니다.") String name,
        @NotBlank(message = "연락처는 필수 입력값입니다.") String contact,
        @NotBlank(message = "주소는 필수 입력값입니다.") String address,
        @Min(value = 0, message = "배달비는 0원 이상이어야 합니다.") int deliveryFee) {}
