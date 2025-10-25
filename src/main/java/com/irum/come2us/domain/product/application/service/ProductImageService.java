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

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final MemberUtil memberUtil;

    /** 상품 이미지 추가 */
    public void addProductImage(UUID productId, ProductImageCreateRequest request) {
        Member member = memberUtil.getCurrentMember();
        Product product = findValidProduct(productId);

        // 상품 소유자 검증
        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        // 대표 이미지 설정 요청 시 기존 대표 이미지 해제
        if (request.isDefault()) {
            productImageRepository.findByProductId(productId).stream()
                    .filter(ProductImage::isDefault)
                    .findFirst()
                    .ifPresent(ProductImage::unmarkAsDefault);
        }

        ProductImage productImage =
                ProductImage.create(product, request.imageUrl(), request.isDefault());
        productImageRepository.save(productImage);

        log.info(
                "상품 이미지 등록 완료: productId={}, imageUrl={}, isDefault={}",
                productId,
                request.imageUrl(),
                request.isDefault());
    }

    /** 대표 이미지 변경 */
    public void changeDefaultImage(UUID productId, UUID imageId) {
        Member member = memberUtil.getCurrentMember();
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

        // 현재 대표 이미지 해제
        images.stream()
                .filter(ProductImage::isDefault)
                .findFirst()
                .ifPresent(ProductImage::unmarkAsDefault);

        // 선택 이미지 대표 설정
        target.markAsDefault();

        log.info("대표 이미지 변경 완료: productId={}, newDefaultImageId={}", productId, imageId);
    }

    /** 상품 이미지 삭제 */
    public void deleteProductImage(UUID productId, UUID imageId) {
        Member member = memberUtil.getCurrentMember();
        ProductImage image = findValidImage(imageId);

        // 소유자 검증
        memberUtil.assertMemberResourceAccess(image.getProduct().getStore().getMember());

        if (!image.getProduct().getId().equals(productId)) {
            throw new CommonException(ProductImageErrorCode.INVALID_PRODUCT_IMAGE_RELATION);
        }

        // 대표 이미지 삭제 시: 다른 이미지 중 최신 생성된 걸 대표로 지정
        if (image.isDefault()) {
            productImageRepository
                    .findTopByProductIdOrderByCreatedAtDesc(productId)
                    .ifPresent(ProductImage::markAsDefault);
        }

        productImageRepository.delete(image);
        log.info("상품 이미지 삭제 완료: imageId={}, productId={}", imageId, productId);
    }

    /** 상품 이미지 목록 조회 */
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(UUID productId) {
        List<ProductImage> images = productImageRepository.findByProductId(productId);
        if (images.isEmpty()) {
            throw new CommonException(ProductImageErrorCode.PRODUCT_IMAGE_NOT_FOUND);
        }
        return images.stream().map(ProductImageResponse::from).toList();
    }

    /** 유효한 상품 조회 */
    private Product findValidProduct(UUID productId) {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));
    }

    /** 유효한 이미지 조회 */
    private ProductImage findValidImage(UUID imageId) {
        return productImageRepository
                .findById(imageId)
                .orElseThrow(
                        () -> new CommonException(ProductImageErrorCode.PRODUCT_IMAGE_NOT_FOUND));
    }
}
