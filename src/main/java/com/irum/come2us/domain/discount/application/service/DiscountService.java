package com.irum.come2us.domain.discount.application.service;

import com.irum.come2us.domain.discount.domain.entity.Discount;
import com.irum.come2us.domain.discount.domain.repository.DiscountRepository;
import com.irum.come2us.domain.discount.presentation.dto.request.DiscountInfoUpdateRequest;
import com.irum.come2us.domain.discount.presentation.dto.request.DiscountRegisterRequest;
import com.irum.come2us.domain.discount.presentation.dto.response.DiscountInfoListResponse;
import com.irum.come2us.domain.discount.presentation.dto.response.DiscountInfoResponse;
import com.irum.come2us.domain.member.application.util.MemberValidator;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.domain.store.domain.repository.StoreRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.DiscountErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.StoreErrorCode;
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
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final MemberValidator memberValidator;

    public void createDiscount(DiscountRegisterRequest request) {
        Product product = assertOwnerProduct(request.productId());
        checkDuplicateDiscount(product.getId());
        discountRepository.save(Discount.create(request.name(), request.amount(), product));
    }

    @Transactional(readOnly = true)
    public DiscountInfoResponse findDiscountInfoByProduct(UUID productId) {
        Discount discount = getValidDiscountOfProduct(productId);
        return DiscountInfoResponse.of(discount);
    }

    @Transactional(readOnly = true)
    public DiscountInfoListResponse findDiscountInfoListByStore(
            UUID storeId, UUID cursor, Integer size) {
        Store store = assertOwnerStore(storeId);
        if (size == null || (size != 10 && size != 30 && size != 50)) {
            log.warn("허용되지 않은 size 요청: {} -> 기본값 10으로 대체", size);
            size = 10;
        }
        int limit = size + 1;
        List<DiscountInfoResponse> discountList =
                discountRepository.findDiscountListByCursor(store.getId(), cursor, limit);

        boolean hasNext = discountList.size() > size;
        UUID nextCursor = null;
        List<DiscountInfoResponse> responseList =
                hasNext ? discountList.subList(0, size) : discountList;
        if (!responseList.isEmpty()) {
            nextCursor = responseList.get(responseList.size() - 1).discountId();
        }
        return new DiscountInfoListResponse(responseList, nextCursor, hasNext);
    }

    public void changeDiscountInfo(UUID discountId, DiscountInfoUpdateRequest request) {
        Discount discount = getValidDiscount(discountId);
        if (request.name() != null) discount.updateName(request.name());
        discount.updateAmount(request.amount());
    }

    public void removeDiscount(UUID discountId) {
        Discount discount = getValidDiscount(discountId);
        discountRepository.delete(discount);
    }

    private void checkDuplicateDiscount(UUID productId) {
        if (discountRepository.existsByProductId(productId)) {
            throw new CommonException(DiscountErrorCode.DUPLICATE_DISCOUNT);
        }
    }

    private Discount getValidDiscount(UUID discountId) {
        Discount discount =
                discountRepository
                        .findById(discountId)
                        .orElseThrow(
                                () -> new CommonException(DiscountErrorCode.DISCOUNT_NOT_FOUND));
        assertMember(discount.getProduct().getStore().getMember());
        return discount;
    }

    private Discount getValidDiscountOfProduct(UUID productId) {
        Discount discount =
                discountRepository
                        .findByProductId(productId)
                        .orElseThrow(
                                () -> new CommonException(DiscountErrorCode.DISCOUNT_NOT_FOUND));
        assertMember(discount.getProduct().getStore().getMember());
        return discount;
    }

    private Product assertOwnerProduct(UUID productId) { // 본인의 상품에 대해서만 상품 할인 등록 가능
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));
        assertMember(product.getStore().getMember());
        return product;
    }

    private Store assertOwnerStore(UUID storeId) {
        Store store =
                storeRepository
                        .findById(storeId)
                        .orElseThrow(() -> new CommonException(StoreErrorCode.STORE_NOT_FOUND));
        assertMember(store.getMember());
        return store;
    }

    private void assertMember(Member member) {
        Member currentMember = memberValidator.getCurrentMember();
        if (!member.getMemberId().equals(currentMember.getMemberId()))
            throw new CommonException(MemberErrorCode.UNAUTHORIZED_ACCESS);
    }
}
