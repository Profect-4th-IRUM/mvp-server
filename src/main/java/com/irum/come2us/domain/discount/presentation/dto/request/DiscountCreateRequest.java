package com.irum.come2us.domain.discount.presentation.dto.request;

import java.util.UUID;

public record DiscountCreateRequest(String name, int amount, UUID productId) {}
