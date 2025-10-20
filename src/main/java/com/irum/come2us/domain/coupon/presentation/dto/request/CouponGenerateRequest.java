package com.irum.come2us.domain.coupon.presentation.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

public class CouponGenerateRequest {
    @NotBlank (message = "쿠폰명은 필수입니다")
    private String name;

    @NotNull (message = "할인금액은 필수입니다")
    @Min(value = 0, message = "할인금액은 0 이상이어야 합니다")
    private Integer discountAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future(message = "유효기간은 현재 시간 이후여야 합니다")
    @NotNull(message = "유효기간은 필수입니다")
    private LocalDateTime expiration;

}
