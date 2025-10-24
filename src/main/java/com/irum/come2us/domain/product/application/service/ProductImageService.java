package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.entity.ProductImage;
import com.irum.come2us.domain.product.domain.repository.ProductImageRepository;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.product.presentation.dto.request.ProductImageCreateRequest;
import com.irum.come2us.domain.product.presentation.dto.response.ProductImageResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductImageErrorCode;
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
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // 상품 소유자 검증
        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        // 대표 이미지 중복 등록 방지
        if (request.isDefault()) {
            boolean alreadyHasDefault = productImageRepository.findByProductId(productId)
                    .stream()
                    .anyMatch(ProductImage::isDefault);
            if (alreadyHasDefault) {
                throw new CommonException(ProductImageErrorCode.DUPLICATE_DEFAULT_IMAGE);
            }
        }

        ProductImage image = ProductImage.create(product, request.imageUrl(), request.isDefault());
        productImageRepository.save(image);

        log.info("상품 이미지 추가 완료: productId={}, imageUrl={}, isDefault={}",
                productId, request.imageUrl(), request.isDefault());

        return ProductImageResponse.from(image);
    }

    public void deleteImage(UUID productId, UUID imageId) {
        Member member = memberUtil.getCurrentMember();
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new CommonException(ProductImageErrorCode.PRODUCT_IMAGE_NOT_FOUND));

        // 상품 소유자 검증
        memberUtil.assertMemberResourceAccess(image.getProduct().getStore().getMember());

        // 요청한 productId와 이미지가 속한 product 불일치 시 → 잘못된 요청
        if (!image.getProduct().getId().equals(productId)) {
            throw new CommonException(ProductImageErrorCode.INVALID_PRODUCT_IMAGE_RELATION);
        }

        try {
            productImageRepository.delete(image);
            log.info("상품 이미지 삭제 완료: imageId={}, productId={}", imageId, productId);
        } catch (Exception e) {
            throw new CommonException(ProductImageErrorCode.IMAGE_DELETE_FAILED);
        }
    }

    public void setDefaultImage(UUID productId, UUID imageId) {
        Member member = memberUtil.getCurrentMember();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        // 상품 소유자 검증
        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        List<ProductImage> images = productImageRepository.findByProductId(productId);

        boolean imageExists = images.stream().anyMatch(img -> img.getId().equals(imageId));
        if (!imageExists) {
            throw new CommonException(ProductImageErrorCode.INVALID_PRODUCT_IMAGE_RELATION);
        }

        images.forEach(img -> img.update(img.getImageUrl(), img.getId().equals(imageId)));

        log.info("대표 이미지 변경 완료: productId={}, newDefaultImageId={}", productId, imageId);
    }

    @Transactional(readOnly = true)
    public List<ProductImageResponse> getImages(UUID productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        if (images.isEmpty()) {
            throw new CommonException(ProductImageErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        }
        return images.stream().map(ProductImageResponse::from).toList();
    }
}
