package com.irum.come2us.domain.store.presentation.controller;

import com.irum.come2us.domain.store.application.service.StoreService;
import com.irum.come2us.domain.store.presentation.dto.request.StoreCreateRequest;
import com.irum.come2us.domain.store.presentation.dto.response.StoreCreateResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/store-create")
    public ResponseEntity<StoreCreateResponse> createStore(
            @Valid @RequestBody StoreCreateRequest request) {

        UUID storeId = storeService.createStore(request);
        return ResponseEntity.ok(new StoreCreateResponse(storeId));
    }
}
