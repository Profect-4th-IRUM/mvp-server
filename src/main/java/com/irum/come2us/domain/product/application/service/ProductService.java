package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.product.presentation.dto.request.ProductCreateRequest;
import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
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
}
