package com.irum.come2us.domain.discount.application.service;

import com.irum.come2us.domain.discount.domain.entity.Discount;
import com.irum.come2us.domain.discount.domain.repository.DiscountRepository;
import com.irum.come2us.domain.discount.presentation.dto.request.DiscountRegisterRequest;
import com.irum.come2us.domain.member.application.util.MemberValidator;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.product.domain.entity.Product;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.store.domain.repository.StoreRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.DiscountErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.ProductErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
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

    private void checkDuplicateDiscount(UUID productId) {
        if (discountRepository.existsByProductId(productId)) {
            throw new CommonException(DiscountErrorCode.DUPLICATE_DISCOUNT);
        }
    }

    private Product assertOwnerProduct(UUID productId) { // 본인의 상품에 대해서만 상품 할인 등록 가능
        Member member = memberValidator.getCurrentMember();
        Product product =
                productRepository
                        .findById(productId)
                        .orElseThrow(() -> new CommonException(ProductErrorCode.PRODUCT_NOT_FOUND));
        Member productOwner = product.getStore().getMember();
        if (!member.equals(productOwner))
            throw new CommonException(MemberErrorCode.UNAUTHORIZED_ACCESS);
        return product;
    }
}
