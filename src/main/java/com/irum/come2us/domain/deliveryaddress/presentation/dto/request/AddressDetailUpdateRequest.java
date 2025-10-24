package com.irum.come2us.domain.deliveryaddress.presentation.dto.request;

import java.util.UUID;

public record AddressDetailUpdateRequest(UUID deliveryAddressId, String newAddressDetail) {}
