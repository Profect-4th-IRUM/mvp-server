package com.irum.come2us.domain.deliveryaddress.presentation.controller;

import com.irum.come2us.domain.deliveryaddress.application.service.DeliveryAddressService;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.AddressDetailUpdateRequest;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.DeliveryAddressRegisterRequest;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.RecipientUpdateRequest;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.response.DeliveryAddressInfoListResponse;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.response.DeliveryAddressInfoResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class DeliveryAddressController {
    private final DeliveryAddressService deliveryAddressService;

    @PostMapping
    public ResponseEntity<Void> registerDeliveryAddress(
            @Valid @RequestBody DeliveryAddressRegisterRequest request) {
        deliveryAddressService.createDeliveryAddress(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{deliveryAddressId}/info")
    public DeliveryAddressInfoResponse getDeliveryAddressInfo(
            @PathVariable UUID deliveryAddressId) {
        return deliveryAddressService.findDeliveryAddress(deliveryAddressId);
    }

    @GetMapping
    public DeliveryAddressInfoListResponse getDeliveryAddressInfoList(
            @Nullable @RequestParam UUID cursor, @RequestParam Integer size) {
        return deliveryAddressService.findDeliveryAddressList(cursor, size);
    }

    @PatchMapping("/{deliveryAddressId}/recipient")
    public ResponseEntity<Void> updateRecipientInfo(
            @PathVariable UUID deliveryAddressId, @RequestBody RecipientUpdateRequest request) {
        deliveryAddressService.changeRecipientInfo(deliveryAddressId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{deliveryAddressId}/detail")
    public ResponseEntity<Void> updateAddressDetail(
            @PathVariable UUID deliveryAddressId,
            @Valid @RequestBody AddressDetailUpdateRequest request) {
        deliveryAddressService.changeAddressDetail(deliveryAddressId, request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{deliveryAddressId}/default")
    public ResponseEntity<Void> setDefaultDeliveryAddress(@PathVariable UUID deliveryAddressId) {
        deliveryAddressService.changeDefaultDeliveryAddress(deliveryAddressId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{deliveryAddressId}")
    public ResponseEntity<Void> deleteDeliveryAddress(@PathVariable UUID deliveryAddressId) {
        deliveryAddressService.removeDeliveryAddress(deliveryAddressId);
        return ResponseEntity.noContent().build();
    }
}
