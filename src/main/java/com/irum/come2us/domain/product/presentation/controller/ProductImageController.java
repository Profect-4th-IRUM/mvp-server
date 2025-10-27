package com.irum.come2us.domain.product.presentation.controller;

import com.irum.come2us.domain.product.application.service.ProductImageService;
import com.irum.come2us.domain.product.presentation.dto.request.ProductImageUploadRequest;
import com.irum.come2us.domain.product.presentation.dto.response.ProductImageResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products/{productId}/images")
@RequiredArgsConstructor
@Slf4j
public class ProductImageController {

    private final ProductImageService productImageService;

    /** 상품 이미지 업로드 */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadProductImages(
            @PathVariable UUID productId,
            @Valid @ModelAttribute ProductImageUploadRequest request) {

        log.info("상품 이미지 업로드 요청: productId={}, fileCount={}", productId, request.images().size());
        productImageService.uploadProductImages(productId, request.images());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 대표 이미지 변경 */
    @PatchMapping("/{imageId}/default")
    public ResponseEntity<Void> changeDefaultImage(
            @PathVariable UUID productId, @PathVariable UUID imageId) {

        log.info("대표 이미지 변경 요청: productId={}, imageId={}", productId, imageId);
        productImageService.changeDefaultImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    /** 상품 이미지 삭제 */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable UUID productId, @PathVariable UUID imageId) {

        log.info("상품 이미지 삭제 요청: productId={}, imageId={}", productId, imageId);
        productImageService.deleteProductImage(productId, imageId);
        return ResponseEntity.noContent().build();
    }

    /** 상품 이미지 목록 조회 */
    @GetMapping
    public ResponseEntity<List<ProductImageResponse>> getProductImages(
            @PathVariable UUID productId) {

        log.info("상품 이미지 목록 조회 요청: productId={}", productId);
        List<ProductImageResponse> responses = productImageService.getProductImages(productId);
        return ResponseEntity.ok(responses);
    }
}
