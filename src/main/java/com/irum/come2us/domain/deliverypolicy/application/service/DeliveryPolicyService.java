package com.irum.come2us.domain.deliverypolicy.application.service;

import com.irum.come2us.domain.deliverypolicy.domain.entity.DeliveryPolicy;
import com.irum.come2us.domain.deliverypolicy.domain.repository.DeliveryPolicyRepository;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.request.DeliveryPolicyCreateRequest;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.request.DeliveryPolicyInfoUpdateRequest;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.response.DeliveryPolicyInfoResponse;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.store.domain.entity.Store;
import com.irum.come2us.domain.store.domain.repository.StoreRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.DeliveryPolicyErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.StoreErrorCode;
import com.irum.come2us.global.util.MemberUtil;
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
    private final MemberUtil memberUtil;

    public UUID createDeliveryPolicy(DeliveryPolicyCreateRequest request) {
        Member member = memberUtil.getCurrentMember();
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
        DeliveryPolicy deliveryPolicy = validDeliveryPolicy(deliveryPolicyId);

        deliveryPolicy.updateFee(request.defaultDeliveryFee());
        deliveryPolicy.updateQuantity(request.minQuantity());
        deliveryPolicy.updateAmount(request.minAmount());
    }

    public void withdrawDeliveryPolicy(UUID deliveryPolicyId) {
        DeliveryPolicy deliveryPolicy = validDeliveryPolicy(deliveryPolicyId);

        deliveryPolicyRepository.delete(deliveryPolicy);
    }

    @Transactional
    public DeliveryPolicyInfoResponse findDeliveryPolicy(UUID deliveryPolicyId) {
        DeliveryPolicy deliveryPolicy = validDeliveryPolicy(deliveryPolicyId);
        return DeliveryPolicyInfoResponse.from(deliveryPolicy);
    }

    // 상점에 배달비 정책이 존재하는지 확인
    private void ensureStoreHasNoExistingPolicy(Store store) {
        if (deliveryPolicyRepository.existsByStore(store)) {
            throw new CommonException(DeliveryPolicyErrorCode.ALREADY_EXISTS);
        }
    }

    // 로그인된 멤버의 상점 조회
    private Store validateAndGetStoreByMember(Member member) {
        return storeRepository
                .findByMember(member)
                .orElseThrow(() -> new CommonException(StoreErrorCode.STORE_NOT_FOUND));
    }

    // 본인 소유 검증.
    private DeliveryPolicy validDeliveryPolicy(UUID deliveryPolicyId) {
        DeliveryPolicy deliverypolicy =
                deliveryPolicyRepository
                        .findById(deliveryPolicyId)
                        .orElseThrow(
                                () ->
                                        new CommonException(
                                                DeliveryPolicyErrorCode.DELIVERY_POLICY_NOT_FOUND));
        memberUtil.assertMemberResourceAccess(deliverypolicy.getStore().getMember());
        return deliverypolicy;
    }
}

// 상점에 이미 정책이 존재하는지 (정책 생성을 위해)
// 상점의 존재 하는지
// 본인 소유 검증.
