package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.product.presentation.dto.request.ProductCreateRequest;
import com.irum.come2us.domain.product.presentation.dto.request.ProductCursorResponse;
import com.irum.come2us.domain.product.presentation.dto.request.ProductPublicUpdateRequest;
import com.irum.come2us.domain.product.presentation.dto.request.ProductUpdateRequest;
import com.irum.come2us.domain.product.presentation.dto.response.ProductDetailResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    // TODO: 상점 매핑 시, 같은 상점 내 같은 상품 중복 처리
    public ProductResponse createProduct(ProductCreateRequest request) {
        Product product =
                Product.createProduct(
                        request.name(),
                        request.description(),
                        request.detailDescription(),
                        request.price(),
                        request.isPublic());

        return ProductResponse.from(productRepository.save(product));
    }

    public ProductResponse updateProduct(UUID productId, ProductUpdateRequest request) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        if (request.name() == null
                && request.description() == null
                && request.detailDescription() == null
                && request.price() == null
                && request.isPublic() == null) {
            log.warn("상품 수정 실패: 변경된 필드가 없습니다. productId={}", productId);
            throw new CommonException(ProductErrorCode.PRODUCT_NOT_MODIFIED);
        }

        String updatedName = request.name() != null ? request.name() : product.getName();
        String updatedDescription =
                request.description() != null ? request.description() : product.getDescription();
        String updatedDetailDescription =
                request.detailDescription() != null
                        ? request.detailDescription()
                        : product.getDetailDescription();
        int updatedPrice = request.price() != null ? request.price() : product.getPrice();
        boolean updatedIsPublic =
                request.isPublic() != null ? request.isPublic() : product.isPublic();

        log.info("상품 수정 시작: productId={}", productId);

        if (!product.getName().equals(updatedName)) {
            log.info("상품명 변경: {} → {}", product.getName(), updatedName);
        }
        if (!product.getDescription().equals(updatedDescription)) {
            log.info("상품 설명 변경: {} → {}", product.getDescription(), updatedDescription);
        }
        if (!product.getDetailDescription().equals(updatedDetailDescription)) {
            log.info(
                    "상품 상세설명 변경: {} → {}",
                    product.getDetailDescription(),
                    updatedDetailDescription);
        }
        if (product.getPrice() != updatedPrice) {
            log.info("상품 가격 변경: {} → {}", product.getPrice(), updatedPrice);
        }
        if (product.isPublic() != updatedIsPublic) {
            log.info("공개여부 변경: {} → {}", product.isPublic(), updatedIsPublic);
        }

        product.updateProduct(
                updatedName,
                updatedDescription,
                updatedDetailDescription,
                updatedPrice,
                updatedIsPublic);

        log.info("상품 수정 완료: productId={}", productId);
        return ProductResponse.from(product);
    }

    public ProductResponse updateProductPublicStatus(
            UUID productId, ProductPublicUpdateRequest request) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        boolean newStatus = request.isPublic();
        boolean currentStatus = product.isPublic();

        if (newStatus == currentStatus) {
            log.warn("상품 공개 상태 변경 실패: 동일한 상태 요청 productId={}, isPublic={}", productId, newStatus);
            throw new CommonException(ProductErrorCode.PRODUCT_NOT_MODIFIED);
        }

        log.info("상품 공개 상태 변경: productId={}, {} -> {}", productId, currentStatus, newStatus);

        product.updateProduct(
                product.getName(),
                product.getDescription(),
                product.getDetailDescription(),
                product.getPrice(),
                newStatus);

        return ProductResponse.from(product);
    }

    public ProductCursorResponse getProductList(UUID cursor, Integer size, String keyword) {
        if (size == null || (size != 10 && size != 30 && size != 50)) {
            log.warn("허용되지 않은 size 요청: {} -> 기본값 10으로 대체", size);
            size = 10;
        }

        List<ProductResponse> products;

        if (keyword != null && !keyword.trim().isEmpty()) {
            log.info("상품 검색 요청: keyword={}, cursor={}, size={}", keyword, cursor, size);
            products = productRepository.findProductsByKeyword(cursor, size, keyword);
        } else {
            log.info("상품 목록 조회 요청: cursor={}, size={}", cursor, size);
            products = productRepository.findProductsByCursor(cursor, size);
        }

        log.info("상품 목록 조회 완료: keyword={}, count={}", keyword, products.size());
        return ProductCursorResponse.of(products);
    }

    public ProductDetailResponse getProductById(UUID productId) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        return ProductDetailResponse.from(product);
    }

    public void deleteProduct(UUID productId) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
        log.info("상품 삭제 완료: productId={}", productId);
    }
}
