package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.entity.ProductImage;
import com.irum.come2us.domain.product.domain.repository.ProductImageRepository;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.product.presentation.dto.request.ProductImageCreateRequest;
import com.irum.come2us.domain.product.presentation.dto.response.ProductImageResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final MemberUtil memberUtil;

    public ProductImageResponse addImage(UUID productId, ProductImageCreateRequest request) {
        Member member = memberUtil.getCurrentMember();
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        ProductImage image = ProductImage.create(product, request.imageUrl(), request.isDefault());
        productImageRepository.save(image);
        log.info("상품 이미지 추가: productId={}, imageUrl={}", productId, request.imageUrl());

        if (request.isDefault()) {
            productImageRepository.findByProductId(productId).stream()
                    .filter(img -> !img.getId().equals(image.getId()) && img.isDefault())
                    .forEach(img -> img.update(img.getImageUrl(), false));
        }

        return ProductImageResponse.from(image);
    }

    public void deleteImage(UUID productId, UUID imageId) {
        Member member = memberUtil.getCurrentMember();
        ProductImage image =
                productImageRepository
                        .findById(imageId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(image.getProduct().getStore().getMember());

        if (!image.getProduct().getId().equals(productId)) {
            throw new CommonException(MemberErrorCode.UNAUTHORIZED_ACCESS);
        }

        productImageRepository.delete(image);
        log.info("상품 이미지 삭제 완료: imageId={}, productId={}", imageId, productId);
    }

    public void setDefaultImage(UUID productId, UUID imageId) {
        Member member = memberUtil.getCurrentMember();
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        List<ProductImage> images = productImageRepository.findByProductId(productId);
        images.forEach(img -> img.update(img.getImageUrl(), img.getId().equals(imageId)));

        log.info("대표 이미지 변경 완료: productId={}, newDefaultImageId={}", productId, imageId);
    }

    @Transactional(readOnly = true)
    public List<ProductImageResponse> getImages(UUID productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        return images.stream().map(ProductImageResponse::from).toList();
    }
}
