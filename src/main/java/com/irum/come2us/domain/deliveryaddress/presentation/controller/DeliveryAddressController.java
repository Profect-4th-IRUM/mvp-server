package com.irum.come2us.domain.deliveryaddress.presentation.controller;

import com.irum.come2us.domain.deliveryaddress.application.service.DeliveryAddressService;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.AddressDetailUpdateRequest;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.DeliveryAddressRegisterRequest;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.RecipientUpdateRequest;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.response.DeliveryAddressInfoListResponse;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.response.DeliveryAddressInfoResponse;
import jakarta.annotation.Nullable;
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
            @RequestBody DeliveryAddressRegisterRequest request) {
        deliveryAddressService.createDeliveryAddress(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/info")
    public DeliveryAddressInfoResponse getDeliveryAddressInfo(
            @RequestParam UUID deliveryAddressId) {
        return deliveryAddressService.findDeliveryAddress(deliveryAddressId);
    }

    @GetMapping
    public DeliveryAddressInfoListResponse getDeliveryAddressInfoList(
            @Nullable @RequestParam UUID cursor, @RequestParam int pageSize) {
        return deliveryAddressService.findDeliveryAddressList(cursor, pageSize);
    }

    @PatchMapping("/recipient")
    public ResponseEntity<Void> updateRecipientInfo(@RequestBody RecipientUpdateRequest request) {
        deliveryAddressService.changeRecipientInfo(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/detail")
    public ResponseEntity<Void> updateAddressDetail(
            @RequestBody AddressDetailUpdateRequest request) {
        deliveryAddressService.changeAddressDetail(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/default")
    public ResponseEntity<Void> setDefaultDeliveryAddress(@RequestParam UUID deliveryAddressId) {
        deliveryAddressService.changeDefaultDeliveryAddress(deliveryAddressId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteDeliveryAddress(@RequestParam UUID deliveryAddressId) {
        deliveryAddressService.removeDeliveryAddress(deliveryAddressId);
        return ResponseEntity.noContent().build();
    }
}
