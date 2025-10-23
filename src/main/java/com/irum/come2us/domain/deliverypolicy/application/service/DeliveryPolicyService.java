package com.irum.come2us.domain.deliverypolicy.application.service;

import com.irum.come2us.domain.deliverypolicy.domain.entity.DeliveryPolicy;
import com.irum.come2us.domain.deliverypolicy.domain.repository.DeliveryPolicyRepository;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.request.DeliveryPolicyCreateRequest;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.request.DeliveryPolicyInfoUpdateRequest;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.response.DeliveryPolicyInfoResponse;
import com.irum.come2us.domain.member.application.util.MemberValidator;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.domain.store.domain.repository.StoreRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.DeliveryPolicyErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.StoreErrorCode;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryPolicyService {
    private final DeliveryPolicyRepository deliveryPolicyRepository;
    private final StoreRepository storeRepository;
    private final MemberValidator memberValidator;

    public UUID createDeliveryPolicy(DeliveryPolicyCreateRequest request) {
        Member member = memberValidator.getCurrentMember();
        Store store = validateAndGetStoreByMember(member);
        ensureStoreHasNoExistingPolicy(store);

        DeliveryPolicy deliveryPolicy =
                DeliveryPolicy.createPolicy(
                        request.defaultDeliveryFee(),
                        request.minQuantity(),
                        request.minAmount(),
                        store);
        deliveryPolicyRepository.save(deliveryPolicy);
        return deliveryPolicy.getId();
    }

    public void changeDeliveryPolicy(
            UUID deliveryPolicyId, DeliveryPolicyInfoUpdateRequest request) {
        Member member = memberValidator.getCurrentMember();
        DeliveryPolicy deliveryPolicy = getDeliveryPolicyById(deliveryPolicyId);
        Store store = validateAndGetStoreByMember(member);

        validateStoreOwner(store, member);

        deliveryPolicy.updateFee(request.defaultDeliveryFee());
        deliveryPolicy.updateQuantity(request.minQuantity());
        deliveryPolicy.updateAmount(request.minAmount());
    }

    public void withdrawDeliveryPolicy(UUID deliveryPolicyId) {
        Member member = memberValidator.getCurrentMember();
        DeliveryPolicy deliveryPolicy = getDeliveryPolicyById(deliveryPolicyId);

        validateStoreOwner(deliveryPolicy.getStore(), member);
        deliveryPolicyRepository.delete(deliveryPolicy);
    }

    @Transactional
    public DeliveryPolicyInfoResponse findDeliveryPolicy(UUID deliveryPolicyId) {
        DeliveryPolicy deliveryPolicy = getDeliveryPolicyById(deliveryPolicyId);
        return DeliveryPolicyInfoResponse.from(deliveryPolicy);
    }

    // 상점 정책 존재하는지 확인
    private void ensureStoreHasNoExistingPolicy(Store store) {
        if (deliveryPolicyRepository.existsByStore(store)) {
            throw new CommonException(DeliveryPolicyErrorCode.ALREADY_EXISTS);
        }
    }

    // 이게 로그인된 멤버의 스토어가 맞나요?
    private void validateStoreOwner(Store store, Member currentMember) {
        if (!store.getMember().getMemberId().equals(currentMember.getMemberId())) {
            throw new CommonException(StoreErrorCode.UNAUTHORIZED_STORE_ACCESS);
        }
    }

    // 상점 주인 검증
    private Store validateAndGetStoreByMember(Member member) {
        return storeRepository
                .findByMember(member)
                .orElseThrow(() -> new CommonException(StoreErrorCode.STORE_NOT_FOUND));
    }

    // ID로 배송 정책 조회
    private DeliveryPolicy getDeliveryPolicyById(UUID deliveryPolicyId) {
        return deliveryPolicyRepository
                .findById(deliveryPolicyId)
                .orElseThrow(
                        () ->
                                new CommonException(
                                        DeliveryPolicyErrorCode.DELIVERY_POLICY_NOT_FOUND));
    }
}
