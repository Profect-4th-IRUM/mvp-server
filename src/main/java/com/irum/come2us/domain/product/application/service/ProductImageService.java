package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.entity.ProductImage;
import com.irum.come2us.domain.product.domain.repository.ProductImageRepository;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.product.presentation.dto.response.ProductImageResponse;
import com.irum.come2us.global.constants.FileStorageConstants;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductImageErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final MemberUtil memberUtil;
    private final FileStorageService fileStorageService;

    @Lazy @Autowired private ProductImageService self;

    private static final Pattern IMAGE_PATTERN =
            Pattern.compile(FileStorageConstants.IMAGE_EXTENSION_REGEX);

    /** 상품 이미지 업로드 */
    public void uploadProductImages(UUID productId, List<MultipartFile> files) {
        List<String> storedUrls = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                validateFile(file);
                storedUrls.add(fileStorageService.save(file));
            }
            self.saveProductImages(productId, storedUrls);

        } catch (Exception e) {
            storedUrls.forEach(fileStorageService::delete);
            log.warn("업로드 중 예외 발생. 저장된 파일 {}건 복구(삭제) 완료", storedUrls.size(), e);
            throw e;
        }
    }

    @Transactional
    public void saveProductImages(UUID productId, List<String> storedUrls) {
        Product product = findValidProduct(productId);
        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        boolean hasExistingImages = productImageRepository.existsByProductId(productId);

        for (int i = 0; i < storedUrls.size(); i++) {
            boolean isDefault = !hasExistingImages && i == 0;
            ProductImage productImage = ProductImage.create(product, storedUrls.get(i), isDefault);
            productImageRepository.save(productImage);
            log.info(
                    "상품 이미지 등록 완료: productId={}, storedUrl={}, isDefault={}",
                    productId,
                    storedUrls.get(i),
                    isDefault);
        }
    }

    @Transactional
    public void changeDefaultImage(UUID productId, UUID imageId) {
        Product product = findValidProduct(productId);
        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        List<ProductImage> images = productImageRepository.findByProductId(productId);
        ProductImage target =
                images.stream()
                        .filter(img -> img.getId().equals(imageId))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new CommonException(
                                                ProductImageErrorCode
                                                        .INVALID_PRODUCT_IMAGE_RELATION));

        images.stream()
                .filter(ProductImage::isDefault)
                .findFirst()
                .ifPresent(ProductImage::unmarkAsDefault);

        target.markAsDefault();
        log.info("대표 이미지 변경 완료: productId={}, newDefaultImageId={}", productId, imageId);
    }

    @Transactional
    public void deleteProductImage(UUID productId, UUID imageId) {
        ProductImage image = findValidImage(imageId);
        memberUtil.assertMemberResourceAccess(image.getProduct().getStore().getMember());

        if (!image.getProduct().getId().equals(productId)) {
            throw new CommonException(ProductImageErrorCode.INVALID_PRODUCT_IMAGE_RELATION);
        }

        boolean wasDefault = image.isDefault();

        fileStorageService.delete(image.getImageUrl());
        image.unmarkAsDefault();
        productImageRepository.delete(image);
        productImageRepository.flush();

        if (wasDefault) {
            productImageRepository
                    .findTopByProductIdOrderByCreatedAtDesc(productId)
                    .ifPresent(ProductImage::markAsDefault);
        }

        log.info("상품 이미지 삭제 완료: imageId={}, productId={}", imageId, productId);
    }

    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(UUID productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        if (images.isEmpty()) {
            log.info("상품 이미지 없음: productId={}", productId);
            return Collections.emptyList();
        }
        return images.stream().map(ProductImageResponse::from).toList();
    }

    private Product findValidProduct(UUID productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    private ProductImage findValidImage(UUID imageId) {
        return productImageRepository
                .findById(imageId)
                .orElseThrow(
                        () -> new CommonException(ProductImageErrorCode.PRODUCT_IMAGE_NOT_FOUND));
    }

    private void validateFile(MultipartFile file) {
        String originalName = file.getOriginalFilename();
        String cleanFilename = StringUtils.cleanPath(Objects.requireNonNull(originalName));

        log.info("orig={}", originalName);
        log.info("clean={}", cleanFilename);
        log.info("match={}", IMAGE_PATTERN.matcher(cleanFilename).matches());

        if (!IMAGE_PATTERN.matcher(cleanFilename).matches()) {
            throw new CommonException(ProductImageErrorCode.INVALID_FILE_FORMAT);
        }
        if (file.getSize() > FileStorageConstants.MAX_FILE_SIZE) {
            throw new CommonException(ProductImageErrorCode.FILE_TOO_LARGE);
        }
    }
}
