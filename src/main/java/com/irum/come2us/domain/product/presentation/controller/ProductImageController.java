package com.irum.come2us.domain.product.presentation.controller;

import com.irum.come2us.domain.product.application.service.ProductImageService;
import com.irum.come2us.domain.product.presentation.dto.response.ProductImageResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/products/{productId}/images")
@RequiredArgsConstructor
@Slf4j
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadProductImages(
            @PathVariable UUID productId, @RequestPart("files") List<MultipartFile> files) {
        log.info("상품 이미지 업로드 요청: productId={}, fileCount={}", productId, files.size());
        productImageService.uploadProductImages(productId, files);
    }

    @PatchMapping("/{imageId}/default")
    public void changeDefaultImage(@PathVariable UUID productId, @PathVariable UUID imageId) {
        log.info("대표 이미지 변경 요청: productId={}, imageId={}", productId, imageId);
        productImageService.changeDefaultImage(productId, imageId);
    }

    @DeleteMapping("/{imageId}")
    public void deleteProductImage(@PathVariable UUID productId, @PathVariable UUID imageId) {
        log.info("상품 이미지 삭제 요청: productId={}, imageId={}", productId, imageId);
        productImageService.deleteProductImage(productId, imageId);
    }

    @GetMapping
    public List<ProductImageResponse> getProductImages(@PathVariable UUID productId) {
        log.info("상품 이미지 목록 조회 요청: productId={}", productId);
        return productImageService.getProductImages(productId);
    }
}
