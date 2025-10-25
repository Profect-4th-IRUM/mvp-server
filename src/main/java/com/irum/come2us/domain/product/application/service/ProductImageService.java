package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.entity.ProductImage;
import com.irum.come2us.domain.product.domain.repository.ProductImageRepository;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.product.presentation.dto.response.ProductImageResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductImageErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final MemberUtil memberUtil;
    private final FileStorageService fileStorageService; // 신규 추가

    /** 상품 이미지 업로드 */
    @Transactional
    public void uploadProductImages(UUID productId, List<MultipartFile> files, Boolean isDefault) {
        Member member = memberUtil.getCurrentMember();
        Product product = findValidProduct(productId);

        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        if (Boolean.TRUE.equals(isDefault)) {
            productImageRepository.findByProductId(productId).stream()
                    .filter(ProductImage::isDefault)
                    .findFirst()
                    .ifPresent(ProductImage::unmarkAsDefault);
        }

        for (MultipartFile file : files) {
            // 확장자 및 크기 검증
            validateFile(file);

            // 실제 파일 저장 (로컬 또는 S3)
            String storedUrl = fileStorageService.save(file);

            ProductImage productImage = ProductImage.create(product, storedUrl, isDefault);
            productImageRepository.save(productImage);

            log.info(
                    "상품 이미지 업로드 완료: productId={}, storedUrl={}, isDefault={}",
                    productId,
                    storedUrl,
                    isDefault);
        }
    }

    /** 대표 이미지 변경 */
    @Transactional
    public void changeDefaultImage(UUID productId, UUID imageId) {
        Product product = findValidProduct(productId);
        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        List<ProductImage> images = productImageRepository.findByProductId(productId);
        ProductImage target =
                images.stream()
                        .filter(img -> img.getId().equals(imageId))
                        .findFirst()
                        .orElseThrow(() -> new CommonException(ProductImageErrorCode.INVALID_PRODUCT_IMAGE_RELATION));

        images.stream()
                .filter(ProductImage::isDefault)
                .findFirst()
                .ifPresent(ProductImage::unmarkAsDefault);

        target.markAsDefault();
        log.info("대표 이미지 변경 완료: productId={}, newDefaultImageId={}", productId, imageId);
    }

    /** 상품 이미지 삭제 */
    @Transactional
    public void deleteProductImage(UUID productId, UUID imageId) {
        ProductImage image = findValidImage(imageId);
        memberUtil.assertMemberResourceAccess(image.getProduct().getStore().getMember());

        if (!image.getProduct().getId().equals(productId)) {
            throw new CommonException(ProductImageErrorCode.INVALID_PRODUCT_IMAGE_RELATION);
        }

        if (image.isDefault()) {
            productImageRepository.findTopByProductIdOrderByCreatedAtDesc(productId)
                    .ifPresent(ProductImage::markAsDefault);
        }

        fileStorageService.delete(image.getImageUrl());
        productImageRepository.delete(image);

        log.info("상품 이미지 삭제 완료: imageId={}, productId={}", imageId, productId);
    }

    /** 상품 이미지 조회 */
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(UUID productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        if (images.isEmpty()) {
            log.info("상품 이미지 없음: productId={}", productId);
            return Collections.emptyList();
        }
        return images.stream().map(ProductImageResponse::from).toList();
    }

    /** 내부 유효성 검증 */
    private Product findValidProduct(UUID productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    private ProductImage findValidImage(UUID imageId) {
        return productImageRepository.findById(imageId)
                .orElseThrow(() -> new CommonException(ProductImageErrorCode.PRODUCT_IMAGE_NOT_FOUND));
    }

    private void validateFile(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.matches(".*\\.(jpg|jpeg|png)$")) {
            throw new CommonException(ProductImageErrorCode.INVALID_FILE_FORMAT);
        }
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new CommonException(ProductImageErrorCode.FILE_TOO_LARGE);
        }
    }
}
