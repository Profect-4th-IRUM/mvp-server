package com.irum.come2us.domain.store.presentation.controller;

import com.irum.come2us.domain.store.application.service.StoreService;
import com.irum.come2us.domain.store.presentation.dto.request.StoreCreateRequest;
import com.irum.come2us.domain.store.presentation.dto.request.StoreUpdateRequest;
import com.irum.come2us.domain.store.presentation.dto.response.StoreCreateResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores")
@Slf4j
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/store-create")
    public ResponseEntity<StoreCreateResponse> createStore(
            @Valid @RequestBody StoreCreateRequest request) {
        log.info("상점 생성 요청: {}", request);
        UUID storeId = storeService.createStore(request);
        return ResponseEntity.ok(new StoreCreateResponse(storeId));
    }

    // TODO: Security 적용

    @PatchMapping("/{storeId}")
    public ResponseEntity<Void> updateStore(
            @PathVariable UUID storeId, @Valid @RequestBody StoreUpdateRequest request) {
        log.info("상점 정보 수정 요청: storeId={}, request={}", storeId, request);
        storeService.updateStore(storeId, request);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    // TODO: Security 적용

    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(@PathVariable UUID storeId) {
        log.info("상점 삭제 요청 : storeId={}", storeId);
        storeService.deleteStore(storeId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
