package com.irum.come2us.domain.store.application.service;

import com.irum.come2us.domain.member.application.util.MemberValidator;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.product.domain.repository.ProductRepository;
import com.irum.come2us.domain.product.presentation.dto.request.ProductCursorResponse;
import com.irum.come2us.domain.product.presentation.dto.response.ProductResponse;
import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.domain.store.domain.repository.StoreRepository;
import com.irum.come2us.domain.store.presentation.dto.request.StoreCreateRequest;
import com.irum.come2us.domain.store.presentation.dto.request.StoreUpdateRequest;
import com.irum.come2us.domain.store.presentation.dto.response.StoreCreateResponse;
import com.irum.come2us.domain.store.presentation.dto.response.StoreInfoResponse;
import com.irum.come2us.domain.store.presentation.dto.response.StoreListResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.StoreErrorCode;
import com.irum.come2us.global.util.MemberUtil;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;
    private final MemberUtil memberUtil;

    public StoreCreateResponse createStore(StoreCreateRequest request) {
        Member member = memberUtil.getCurrentMember();

        validateMemberHasNoStore(member);
        validateBusinessNumber(request.businessRegistrationNumber());
        validateTelemarketingNumber(request.telemarketingRegistrationNumber());

        Store store =
                Store.createStore(
                        request.name(),
                        request.contact(),
                        request.address(),
                        request.businessRegistrationNumber(),
                        request.telemarketingRegistrationNumber(),
                        member);

        storeRepository.save(store);

        return new StoreCreateResponse(store.getId());
    }

    public void changeStore(UUID storeId, StoreUpdateRequest request) {
        Store store = getStoreById(storeId);

        memberUtil.assertMemberResourceAccess(store.getMember());

        store.updateBasicInfo(request.name(), request.contact(), request.address());
    }

    public void withdrawStore(UUID storeId) {
        Store store = getStoreById(storeId);
        memberUtil.assertMemberResourceAccess(store.getMember());
        store.softDelete(memberUtil.getCurrentMember().getMemberId());
    }

    @Transactional(readOnly = true)
    public List<StoreListResponse> findStoreList(UUID cursor, Integer size) {
        if (size == null || (size != 10 && size != 30 && size != 50)) {
            size = 10;
        }
        return storeRepository.findStoresByCursor(cursor, size);
    }

    @Transactional(readOnly = true)
    public StoreInfoResponse findStoreInfo(UUID storeId) {
        Store store = getStoreById(storeId);
        return StoreInfoResponse.from(store);
    }

    public ProductCursorResponse getMyStoreProducts(UUID cursor, Integer size) {
        Member member = memberUtil.getCurrentMember();
        Store store =
                storeRepository
                        .findByMember(member)
                        .orElseThrow(() -> new CommonException(StoreErrorCode.STORE_NOT_FOUND));

        return getProductsByStore(store.getId(), cursor, size);
    }

    public ProductCursorResponse getStoreProducts(UUID storeId, UUID cursor, Integer size) {
        return getProductsByStore(storeId, cursor, size);
    }

    private ProductCursorResponse getProductsByStore(UUID storeId, UUID cursor, Integer size) {
        if (size == null || (size != 10 && size != 30 && size != 50)) {
            size = 10;
        }

        List<ProductResponse> products =
                productRepository.findProductsByStoreWithCursor(storeId, cursor, size);

        return ProductCursorResponse.of(products);
    }

    // 본인 소유 상점 검증
    private void validateStoreOwner(Store store, Member currentMember) {
        if (!store.getMember().getMemberId().equals(currentMember.getMemberId())) {
            throw new CommonException(StoreErrorCode.UNAUTHORIZED_STORE_ACCESS);
        }
    }

    // 1인 1상점 제한
    private void validateMemberHasNoStore(Member member) {
        if (storeRepository.existsByMember(member)) {
            throw new CommonException(StoreErrorCode.STORE_ALREADY_EXISTS);
        }
    }

    // 사업자등록번호 중복 체크
    private void validateBusinessNumber(String businessRegistrationNumber) {
        if (storeRepository.existsByBusinessRegistrationNumber(businessRegistrationNumber)) {
            throw new CommonException(StoreErrorCode.BUSINESS_NUMBER_DUPLICATED);
        }
    }

    // 통신판매업번호 중복 체크
    private void validateTelemarketingNumber(String telemarketingRegistrationNumber) {
        if (storeRepository.existsByTelemarketingRegistrationNumber(
                telemarketingRegistrationNumber)) {
            throw new CommonException(StoreErrorCode.TELEMARKETING_NUMBER_DUPLICATED);
        }
    }

    private Store getStoreById(UUID storeId) {
        return storeRepository
                .findById(storeId)
                .orElseThrow(() -> new CommonException(StoreErrorCode.STORE_NOT_FOUND));
    }
}
