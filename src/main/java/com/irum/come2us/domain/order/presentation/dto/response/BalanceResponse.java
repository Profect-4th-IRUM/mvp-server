package com.irum.come2us.domain.order.presentation.dto.response;

public record BalanceResponse(
        int totalSalesAmount, // 총 결제 금액 합계
        int totalRefundAmount, // 총 환불 금액 합계
        int settlementAmount // 정산 금액 (총 금액 - 환불 금액)
        ) {}
