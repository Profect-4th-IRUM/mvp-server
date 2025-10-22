package com.irum.come2us.domain.deliverypolicy.application.service;

import com.irum.come2us.domain.deliverypolicy.domain.entity.DeliveryPolicy;
import com.irum.come2us.domain.deliverypolicy.domain.repository.DeliveryPolicyRepository;
import com.irum.come2us.domain.deliverypolicy.presentation.dto.request.DeliveryPolicyCreateRequest;
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
        validateStoreHasNotPolicy(store);

        DeliveryPolicy deliveryPolicy =
                DeliveryPolicy.createPolicy(
                        request.defaultDeliveryFee(),
                        request.minQuantity(),
                        request.minAmount(),
                        store);
        deliveryPolicyRepository.save(deliveryPolicy);
        return deliveryPolicy.getId();
    }

    private void validateStoreHasNotPolicy(Store store) {
        if (deliveryPolicyRepository.existsByStore(store)) {
            throw new CommonException(DeliveryPolicyErrorCode.ALREADY_EXISTS);
        }
    }

    private void validateStoreOwner(Store store, Member currentMember) {
        if (!store.getMember().getMemberId().equals(currentMember.getMemberId())) {
            throw new CommonException(StoreErrorCode.UNAUTHORIZED_STORE_ACCESS);
        }
    }

    private Store validateAndGetStoreByMember(Member member) {
        return storeRepository
                .findByMember(member)
                .orElseThrow(() -> new CommonException(StoreErrorCode.STORE_NOT_FOUND));
    }
}
