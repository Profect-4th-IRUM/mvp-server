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
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductImageService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final MemberUtil memberUtil;

    /** 상품 이미지 업로드 */
    public void uploadProductImages(UUID productId, List<MultipartFile> files, Boolean isDefault) {
        Member member = memberUtil.getCurrentMember();
        Product product = findValidProduct(productId);

        // 상품 소유자 검증
        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        // 대표 이미지 등록 시 기존 대표 해제
        if (Boolean.TRUE.equals(isDefault)) {
            productImageRepository.findByProductId(productId).stream()
                    .filter(ProductImage::isDefault)
                    .findFirst()
                    .ifPresent(ProductImage::unmarkAsDefault);
        }

        for (MultipartFile file : files) {
            // TODO: 실제 파일 저장 로직 ( S3 업로드 후 URL 반환)
            String imageUrl = file.getOriginalFilename();
            ProductImage productImage = ProductImage.create(product, imageUrl, isDefault);
            productImageRepository.save(productImage);
            log.info(
                    "상품 이미지 업로드 완료: productId={}, filename={}, isDefault={}",
                    productId,
                    file.getOriginalFilename(),
                    isDefault);
        }
    }

    /** 대표 이미지 변경 */
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

    /** 상품 이미지 삭제 */
    public void deleteProductImage(UUID productId, UUID imageId) {
        ProductImage image = findValidImage(imageId);
        memberUtil.assertMemberResourceAccess(image.getProduct().getStore().getMember());

        if (!image.getProduct().getId().equals(productId)) {
            throw new CommonException(ProductImageErrorCode.INVALID_PRODUCT_IMAGE_RELATION);
        }

        // 대표 이미지 삭제 시: 최신 이미지 자동 대표 지정
        if (image.isDefault()) {
            productImageRepository
                    .findTopByProductIdOrderByCreatedAtDesc(productId)
                    .ifPresent(ProductImage::markAsDefault);
        }

        productImageRepository.delete(image);
        log.info("상품 이미지 삭제 완료: imageId={}, productId={}", imageId, productId);
    }

    /** 상품 이미지 조회 */
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(UUID productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        if (images.isEmpty())
            throw new CommonException(ProductImageErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        return images.stream().map(ProductImageResponse::from).toList();
    }

    /** 내부 유효성 검증 */
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
}
