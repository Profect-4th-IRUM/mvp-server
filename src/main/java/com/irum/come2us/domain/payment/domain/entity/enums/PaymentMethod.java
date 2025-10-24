package com.irum.come2us.domain.payment.domain.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
    CARD("카드"),
    VIRTUAL_ACCOUNT("가상계좌"),
    EASY_PAYMENT("간편결제"),
    MOBILE("휴대폰"),
    TRANSFER("계좌이체"),
    CULTURE_VOUCHER("문화상품권"),
    BOOK_VOUCHER("도서문화상품권"),
    GAME_VOUCHER("게임문화상품권");

    private final String description;

    // "카드" -> "CARD"
    @JsonCreator
    public static PaymentMethod from(String value) {
        return Arrays.stream(values())
                .filter(e -> e.getDescription().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 결제수단입니다: " + value));
    }
}
