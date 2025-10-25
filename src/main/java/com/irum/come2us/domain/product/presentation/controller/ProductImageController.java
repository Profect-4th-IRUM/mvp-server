package com.irum.come2us.domain.product.presentation.controller;

import com.irum.come2us.domain.product.application.service.ProductImageService;
import com.irum.come2us.domain.product.presentation.dto.request.ProductImageCreateRequest;
import com.irum.come2us.domain.product.presentation.dto.response.ProductImageResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products/{productId}/images")
@RequiredArgsConstructor
@Slf4j
public class ProductImageController {

    private final ProductImageService productImageService;

    /** 상품 이미지 추가 */
    @PostMapping
    public void addProductImage(
            @PathVariable UUID productId, @Valid @RequestBody ProductImageCreateRequest request) {
        log.info("상품 이미지 추가 요청: productId={}, isDefault={}", productId, request.isDefault());
        productImageService.addProductImage(productId, request);
    }

    /** 상품 이미지 삭제 */
    @DeleteMapping("/{imageId}")
    public void deleteProductImage(@PathVariable UUID productId, @PathVariable UUID imageId) {
        log.info("상품 이미지 삭제 요청: productId={}, imageId={}", productId, imageId);
        productImageService.deleteProductImage(productId, imageId);
    }

    /** 대표 이미지 변경 */
    @PatchMapping("/{imageId}/default")
    public void changeDefaultImage(@PathVariable UUID productId, @PathVariable UUID imageId) {
        log.info("대표 이미지 변경 요청: productId={}, imageId={}", productId, imageId);
        productImageService.changeDefaultImage(productId, imageId);
    }

    /** 상품 이미지 목록 조회 */
    @GetMapping
    public List<ProductImageResponse> getProductImages(@PathVariable UUID productId) {
        log.info("상품 이미지 목록 조회 요청: productId={}", productId);
        return productImageService.getProductImages(productId);
    }
}
