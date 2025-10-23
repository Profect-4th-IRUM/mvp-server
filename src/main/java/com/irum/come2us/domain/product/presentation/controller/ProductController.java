package com.irum.come2us.domain.product.presentation.controller;

import com.irum.come2us.domain.product.application.service.ProductService;
import com.irum.come2us.domain.product.presentation.dto.request.*;
import com.irum.come2us.domain.product.presentation.dto.response.ProductDetailResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductOptionGroupResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductOptionValueResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request) {
        log.info("상품 등록 요청: {}", request);
        ProductResponse response = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

        // TODO: Security 적용
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID productId, @RequestBody ProductUpdateRequest request) {
        log.info("상품 정보 수정 요청: productId={}, request={}", productId, request);
        ProductResponse response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(response);

        // TODO: Security 적용
    }

    @PatchMapping("/{productId}/public")
    public ResponseEntity<ProductResponse> updateProductPublicStatus(
            @PathVariable UUID productId, @Valid @RequestBody ProductPublicUpdateRequest request) {
        log.info("상품 공개 상태 변경 요청: productId={}, isPublic={}", productId, request.isPublic());
        ProductResponse response = productService.updateProductPublicStatus(productId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ProductCursorResponse> getProductList(
            @RequestParam(required = false) UUID cursor,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String keyword) {
        log.info("상품 목록 조회 요청: cursor={}, size={}, keyword={}", cursor, size, keyword);
        ProductCursorResponse response = productService.getProductList(cursor, size, keyword);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable UUID productId) {
        log.info("상품 상세 조회 요청: productId={}", productId);

        ProductDetailResponse response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID productId) {
        log.info("상품 삭제 요청: productId={}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/options")
    public ResponseEntity<Void> createProductOptionGroup(
            @PathVariable UUID productId, @Valid @RequestBody ProductOptionGroupRequest request) {
        log.info("상품 옵션 그룹 추가 요청: productId={}, groupName={}", productId, request.name());
        productService.createOptionGroup(productId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/options/{optionGroupId}/values")
    public ResponseEntity<Void> createProductOptionValue(
            @PathVariable UUID optionGroupId,
            @Valid @RequestBody ProductOptionValueRequest request) {
        log.info("옵션 값 추가 요청: optionGroupId={}, valueName={}", optionGroupId, request.name());
        productService.createOptionValue(optionGroupId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/options/{optionGroupId}")
    public ResponseEntity<ProductOptionGroupResponse> updateProductOptionGroup(
            @PathVariable UUID optionGroupId,
            @Valid @RequestBody ProductOptionGroupRequest request) {
        log.info("상품 옵션 그룹 수정 요청: groupId={}", optionGroupId);
        ProductOptionGroupResponse response =
                productService.updateProductOptionGroup(optionGroupId, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/options/values/{optionValueId}")
    public ResponseEntity<ProductOptionValueResponse> updateProductOptionValue(
            @PathVariable UUID optionValueId,
            @Valid @RequestBody ProductOptionValueUpdateRequest request) {
        log.info("상품 옵션 값 수정 요청: valueId={}", optionValueId);
        ProductOptionValueResponse response =
                productService.updateProductOptionValue(optionValueId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/options/{optionGroupId}")
    public ResponseEntity<Void> deleteProductOptionGroup(
            @PathVariable UUID optionGroupId){
        log.info("상품 옵션 그룹 삭제 요청: groupId={}", optionGroupId);
        productService.deleteProductOptionGroup(optionGroupId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/options/values/{optionValueId}")
    public ResponseEntity<Void> deleteProductOptionValue(
            @PathVariable UUID optionValueId) {
        log.info("상품 옵션 값 삭제 요청: valueId={}", optionValueId);
        productService.deleteProductOptionValue(optionValueId);
        return ResponseEntity.noContent().build();
    }
}
