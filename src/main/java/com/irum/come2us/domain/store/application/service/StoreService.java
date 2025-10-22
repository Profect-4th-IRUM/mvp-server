package com.irum.come2us.domain.store.application.service;

import com.irum.come2us.domain.member.application.util.MemberValidator;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.domain.store.domain.repository.StoreRepository;
import com.irum.come2us.domain.store.presentation.dto.request.StoreCreateRequest;
import com.irum.come2us.domain.store.presentation.dto.request.StoreDeliveryFeeUpdateRequest;
import com.irum.come2us.domain.store.presentation.dto.request.StoreUpdateRequest;
import com.irum.come2us.domain.store.presentation.dto.response.StoreInfoResponse;
import com.irum.come2us.domain.store.presentation.dto.response.StoreListResponse;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.StoreErrorCode;
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
    private final MemberValidator memberValidator;

    public UUID registerStore(StoreCreateRequest request) {
        Member member = memberValidator.getCurrentMember();

        validateMemberHasNoStore(member); // 1인 1상점 제한
        validateBusinessNumber(request.businessRegistrationNumber()); // 사업자번호 중복 체크
        validateTelemarketingNumber(request.telemarketingRegistrationNumber()); // 통신판매번호 중복 체크

        Store store =
                Store.createStore(
                        request.name(),
                        request.contact(),
                        request.address(),
                        request.businessRegistrationNumber(),
                        request.telemarketingRegistrationNumber(),
                        request.deliveryFee(),
                        member);

        storeRepository.save(store);
        return store.getId();
    } // owner 권한.

    public void changeStore(UUID storeId, StoreUpdateRequest request) {
        Store store = getStoreById(storeId);
        Member currentMember = memberValidator.getCurrentMember();

        validateStoreOwner(store, currentMember);

        store.updateBasicInfo(request.name(), request.contact(), request.address());
    }

    public void changeDeliveryFee(UUID storeId, StoreDeliveryFeeUpdateRequest request) {
        Store store = getStoreById(storeId);
        Member currentMember = memberValidator.getCurrentMember();

        validateStoreOwner(store, currentMember);

        store.changeDeliveryFee(request.deliveryFee());
    }

    public void withdrawStore(UUID storeId) {
        Store store = getStoreById(storeId);
        // TODO: 권한 체크>?
        storeRepository.delete(store);
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
