package com.irum.come2us.domain.deliveryaddress.application.service;

import com.irum.come2us.domain.deliveryaddress.domain.entity.DeliveryAddress;
import com.irum.come2us.domain.deliveryaddress.domain.repository.DeliveryAddressRepository;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.DeliveryAddressRegisterRequest;
import com.irum.come2us.domain.member.application.util.MemberValidator;
import com.irum.come2us.domain.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryAddressService {
    private final MemberValidator memberValidator;
    private final DeliveryAddressRepository deliveryAddressRepository;

    public void createDeliveryAddress(DeliveryAddressRegisterRequest request) {
        Member member = memberValidator.getCurrentMember();
        String recipientName =
                request.recipientName() == null ? member.getName() : request.recipientName();
        String recipientContact =
                request.recipientContact() == null
                        ? member.getContact()
                        : request.recipientContact();
        DeliveryAddress deliveryAddress =
                DeliveryAddress.create(member, request.address(), recipientName, recipientContact);
        if (!deliveryAddressRepository.findByMember(member).isPresent())
            deliveryAddress.markAsDefault();
        deliveryAddressRepository.save(deliveryAddress);
    }
}
