package com.irum.come2us.domain.product.application.service;

import com.irum.come2us.domain.category.domain.entity.Category;
import com.irum.come2us.domain.category.domain.repository.CategoryRepository;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.entity.ProductOptionGroup;
import com.irum.come2us.domain.product.domain.entity.ProductOptionValue;
import com.irum.come2us.domain.product.domain.repository.ProductOptionGroupRepository;
import com.irum.come2us.domain.product.domain.repository.ProductOptionValueRepository;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.product.presentation.dto.request.ProductCategoryUpdateRequest;
import com.irum.come2us.domain.product.presentation.dto.request.ProductCreateRequest;
import com.irum.come2us.domain.product.presentation.dto.request.ProductOptionGroupRequest;
import com.irum.come2us.domain.product.presentation.dto.request.ProductOptionValueRequest;
import com.irum.come2us.domain.product.presentation.dto.request.ProductOptionValueUpdateRequest;
import com.irum.come2us.domain.product.presentation.dto.request.ProductPublicUpdateRequest;
import com.irum.come2us.domain.product.presentation.dto.request.ProductUpdateRequest;
import com.irum.come2us.domain.product.presentation.dto.response.ProductCursorResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductDetailResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductOptionGroupResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductOptionValueResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.domain.store.domain.repository.StoreRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.CategoryErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.StoreErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private static final String PRODUCT_DETAIL_CACHE = "product:detail";

    private final ProductRepository productRepository;
    private final ProductOptionGroupRepository optionGroupRepository;
    private final ProductOptionValueRepository optionValueRepository;

    private final MemberRepository memberRepository;

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final MemberUtil memberUtil;

    // afterCommit 캐시 무효화용
    private final CacheManager cacheManager;


    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        Member member = memberUtil.getCurrentMember();

        Store store =
                storeRepository
                        .findByMember(member)
                        .orElseThrow(() -> new CommonException(StoreErrorCode.STORE_NOT_FOUND));

        if (!member.getRole().equals(Role.OWNER)) {
            log.warn("상품 등록 실패: 비인가 사용자 memberId={}", member.getMemberId());
            throw new CommonException(MemberErrorCode.UNAUTHORIZED_ACCESS);
        }

        Category category =
                categoryRepository
                        .findById(request.categoryId())
                        .orElseThrow(() -> new CommonException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        Product product =
                Product.createProduct(
                        store,
                        category,
                        request.name(),
                        request.description(),
                        request.detailDescription(),
                        request.price(),
                        request.isPublic());

        if (request.optionGroups() != null && !request.optionGroups().isEmpty()) {
            for (ProductOptionGroupRequest groupReq : request.optionGroups()) {
                ProductOptionGroup group = ProductOptionGroup.createOptionGroup(product, groupReq.name());
                product.addOptionGroup(group);

                if (groupReq.optionValues() != null) {
                    for (ProductOptionValueRequest valueReq : groupReq.optionValues()) {
                        ProductOptionValue.createOptionValue(
                                group,
                                valueReq.name(),
                                valueReq.stockQuantity(),
                                valueReq.extraPrice());
                    }
                }
            }
        }

        productRepository.save(product);
        log.info("상품 등록 완료: storeId={}, productId={}, productName={}",
                store.getId(), product.getId(), product.getName());

        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateProduct(UUID productId, ProductUpdateRequest request) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        if (request.name() == null
                && request.description() == null
                && request.detailDescription() == null
                && request.price() == null
                && request.isPublic() == null) {
            log.warn("상품 수정 실패: 변경된 필드가 없습니다. productId={}", productId);
            throw new CommonException(ProductErrorCode.PRODUCT_NOT_MODIFIED);
        }

        String updatedName = request.name() != null ? request.name() : product.getName();
        String updatedDescription = request.description() != null ? request.description() : product.getDescription();
        String updatedDetailDescription =
                request.detailDescription() != null ? request.detailDescription() : product.getDetailDescription();
        int updatedPrice = request.price() != null ? request.price() : product.getPrice();
        boolean updatedIsPublic = request.isPublic() != null ? request.isPublic() : product.isPublic();

        product.updateProduct(
                updatedName,
                updatedDescription,
                updatedDetailDescription,
                updatedPrice,
                updatedIsPublic);

        evictProductDetailAfterCommit(productId);

        log.info("상품 수정 완료: productId={}", productId);
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateProductPublicStatus(UUID productId, ProductPublicUpdateRequest request) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        boolean newStatus = request.isPublic();
        boolean currentStatus = product.isPublic();

        if (newStatus == currentStatus) {
            log.warn("상품 공개 상태 변경 실패: 동일한 상태 요청 productId={}, isPublic={}", productId, newStatus);
            throw new CommonException(ProductErrorCode.PRODUCT_NOT_MODIFIED);
        }

        product.updateProduct(
                product.getName(),
                product.getDescription(),
                product.getDetailDescription(),
                product.getPrice(),
                newStatus);

        evictProductDetailAfterCommit(productId);

        log.info("상품 공개 상태 변경 완료: productId={}, {} -> {}", productId, currentStatus, newStatus);
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse updateProductCategory(UUID productId, ProductCategoryUpdateRequest request) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        Category category =
                categoryRepository
                        .findById(request.categoryId())
                        .orElseThrow(() -> new CommonException(CategoryErrorCode.CATEGORY_NOT_FOUND));

        product.updateCategory(category);

        evictProductDetailAfterCommit(productId);

        log.info("상품 카테고리 변경 완료: productId={}, categoryId={}", productId, category.getCategoryId());
        return ProductResponse.from(product);
    }

    @Transactional(readOnly = true)
    public ProductCursorResponse getProductList(UUID categoryId, UUID cursor, Integer size, String keyword) {
        if (size == null || (size != 10 && size != 30 && size != 50)) {
            log.warn("허용되지 않은 size 요청: {} -> 기본값 10으로 대체", size);
            size = 10;
        }

        List<ProductResponse> products;

        if (categoryId != null && keyword != null && !keyword.trim().isEmpty()) {
            List<UUID> categoryIds = getAllDescendantCategoryIds(categoryId);
            products = productRepository.findProductsByCategoryIdsAndKeyword(cursor, size, categoryIds, keyword);
        } else if (categoryId != null) {
            List<UUID> categoryIds = getAllDescendantCategoryIds(categoryId);
            products = productRepository.findProductsByCategoryIds(cursor, size, categoryIds);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            products = productRepository.findProductsByKeyword(cursor, size, keyword);
        } else {
            products = productRepository.findProductsByCursor(cursor, size);
        }

        return ProductCursorResponse.of(products);
    }

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = PRODUCT_DETAIL_CACHE,
            key = "#productId.toString()",
            sync = true
    )
    public ProductDetailResponse getProductById(UUID productId) {
        log.info("DB HIT (product detail) productId={}", productId);

        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        return ProductDetailResponse.from(product);
    }

    @Transactional
    public void deleteProduct(UUID productId) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(product.getStore().getMember());
        product.softDelete(memberUtil.getCurrentMember().getMemberId());

        evictProductDetailAfterCommit(productId);

        log.info("상품 삭제 완료: productId={}", productId);
    }

    @Transactional
    public void createOptionGroup(UUID productId, ProductOptionGroupRequest request) {
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(product.getStore().getMember());

        ProductOptionGroup group = ProductOptionGroup.createOptionGroup(product, request.name());
        product.addOptionGroup(group);

        if (request.optionValues() != null && !request.optionValues().isEmpty()) {
            for (ProductOptionValueRequest valueReq : request.optionValues()) {
                ProductOptionValue.createOptionValue(group, valueReq.name(), valueReq.stockQuantity(), valueReq.extraPrice());
            }
        }

        productRepository.save(product);

        evictProductDetailAfterCommit(productId);

        log.info("상품 옵션 그룹 추가 완료: productId={}, groupName={}", productId, request.name());
    }

    @Transactional
    public void createOptionValue(UUID optionGroupId, ProductOptionValueRequest request) {
        ProductOptionGroup optionGroup =
                optionGroupRepository
                        .findById(optionGroupId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.OPTION_GROUP_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(optionGroup.getProduct().getStore().getMember());

        ProductOptionValue.createOptionValue(
                optionGroup,
                request.name(),
                request.stockQuantity(),
                request.extraPrice() != null ? request.extraPrice() : 0);

        optionGroupRepository.save(optionGroup);

        evictProductDetailAfterCommit(optionGroup.getProduct().getId());

        log.info("옵션 값 추가 완료: optionGroupId={}, valueName={}", optionGroupId, request.name());
    }

    @Transactional
    public ProductOptionGroupResponse updateProductOptionGroup(UUID optionGroupId, ProductOptionGroupRequest request) {
        ProductOptionGroup optionGroup =
                optionGroupRepository
                        .findById(optionGroupId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.OPTION_GROUP_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(optionGroup.getProduct().getStore().getMember());

        optionGroup.updateOptionGroupName(request.name());

        evictProductDetailAfterCommit(optionGroup.getProduct().getId());

        return ProductOptionGroupResponse.from(optionGroup);
    }

    @Transactional
    public ProductOptionValueResponse updateProductOptionValue(UUID optionValueId, ProductOptionValueUpdateRequest request) {
        ProductOptionValue optionValue =
                optionValueRepository
                        .findById(optionValueId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.OPTION_VALUE_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(optionValue.getOptionGroup().getProduct().getStore().getMember());

        if ((request.name() == null || request.name().isBlank())
                && request.stockQuantity() == null
                && request.extraPrice() == null) {
            log.warn("옵션 값 수정 실패: 변경된 필드가 없습니다. optionValueId={}", optionValueId);
            throw new CommonException(ProductErrorCode.PRODUCT_NOT_MODIFIED);
        }

        String updatedName =
                request.name() != null && !request.name().isBlank() ? request.name().trim() : optionValue.getName();

        int updatedStockQuantity =
                request.stockQuantity() != null ? request.stockQuantity() : optionValue.getStockQuantity();

        Integer updatedExtraPrice =
                request.extraPrice() != null ? request.extraPrice() : optionValue.getExtraPrice();

        optionValue.updateOptionValue(updatedName, updatedStockQuantity, updatedExtraPrice);
        optionValueRepository.save(optionValue);

        evictProductDetailAfterCommit(optionValue.getOptionGroup().getProduct().getId());

        log.info("상품 옵션 값 수정 완료: optionValueId={}", optionValueId);

        return ProductOptionValueResponse.from(optionValue);
    }

    @Transactional
    public void deleteProductOptionGroup(UUID optionGroupId) {
        ProductOptionGroup optionGroup =
                optionGroupRepository
                        .findById(optionGroupId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.OPTION_GROUP_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(optionGroup.getProduct().getStore().getMember());

        UUID productId = optionGroup.getProduct().getId();

        optionGroupRepository.delete(optionGroup);

        evictProductDetailAfterCommit(productId);

        log.info("상품 옵션 그룹 삭제 완료: groupId={}", optionGroupId);
    }

    @Transactional
    public void deleteProductOptionValue(UUID optionValueId) {
        ProductOptionValue optionValue =
                optionValueRepository
                        .findById(optionValueId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.OPTION_VALUE_NOT_FOUND));

        memberUtil.assertMemberResourceAccess(optionValue.getOptionGroup().getProduct().getStore().getMember());

        UUID productId = optionValue.getOptionGroup().getProduct().getId();

        optionValueRepository.delete(optionValue);

        evictProductDetailAfterCommit(productId);

        log.info("상품 옵션 값 삭제 완료: valueId={}", optionValueId);
    }

    private List<UUID> getAllDescendantCategoryIds(UUID categoryId) {
        List<UUID> ids = new ArrayList<>();
        collectDescendants(categoryId, ids);
        return ids;
    }

    private void collectDescendants(UUID categoryId, List<UUID> ids) {
        ids.add(categoryId);
        List<Category> children = categoryRepository.findChildrenByParentId(categoryId);
        for (Category child : children) {
            collectDescendants(child.getCategoryId(), ids);
        }
    }

    private void evictProductDetailAfterCommit(UUID productId) {
        if (productId == null) return;

        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 트랜잭션이 없으면 즉시 evict
            evictProductDetailNow(productId);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        evictProductDetailNow(productId);
                    }
                });
    }

    private void evictProductDetailNow(UUID productId) {
        Cache cache = cacheManager.getCache(PRODUCT_DETAIL_CACHE);
        if (cache == null) {
            log.warn("Cache not found: {}", PRODUCT_DETAIL_CACHE);
            return;
        }
        cache.evict(productId.toString());
        log.info("Evicted cache. cache={}, key={}", PRODUCT_DETAIL_CACHE, productId);
    }
}
