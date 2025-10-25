package com.irum.come2us.domain.product.presentation.controller;

import com.irum.come2us.domain.product.application.service.ProductImageService;
import com.irum.come2us.domain.product.presentation.dto.request.ProductImageCreateRequest;
import com.irum.come2us.domain.product.presentation.dto.response.ProductImageResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @PostMapping
    public ProductImageResponse addImage(
            @PathVariable UUID productId, @RequestBody ProductImageCreateRequest request) {
        return productImageService.addImage(productId, request);
    }

    @DeleteMapping("/{imageId}")
    public void deleteImage(@PathVariable UUID productId, @PathVariable UUID imageId) {
        productImageService.deleteImage(productId, imageId);
    }

    @PatchMapping("/{imageId}/default")
    public void setDefaultImage(@PathVariable UUID productId, @PathVariable UUID imageId) {
        productImageService.setDefaultImage(productId, imageId);
    }

    @GetMapping
    public List<ProductImageResponse> getImages(@PathVariable UUID productId) {
        return productImageService.getImages(productId);
    }
}
