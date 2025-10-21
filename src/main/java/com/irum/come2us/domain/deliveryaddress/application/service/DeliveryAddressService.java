package com.irum.come2us.domain.deliveryaddress.application.service;

import com.irum.come2us.domain.deliveryaddress.domain.entity.DeliveryAddress;
import com.irum.come2us.domain.deliveryaddress.domain.repository.DeliveryAddressRepository;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.request.DeliveryAddressRegisterRequest;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.response.DeliveryAddressInfoListResponse;
import com.irum.come2us.domain.deliveryaddress.presentation.dto.response.DeliveryAddressInfoResponse;
import com.irum.come2us.domain.member.application.util.MemberValidator;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.DeliveryAddressErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import com.irum.come2us.global.util.Cursor;
import java.util.List;
import java.util.UUID;
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

    @Transactional(readOnly = true)
    public DeliveryAddressInfoResponse findDeliveryAddress(UUID deliveryAddressId) {
        DeliveryAddress deliveryAddress = validDeliveryAddress(deliveryAddressId);
        return DeliveryAddressInfoResponse.of(
                deliveryAddress.getDeliveryAddressId(),
                deliveryAddress.getAddress(),
                deliveryAddress.getRecipientName(),
                deliveryAddress.getRecipientContact(),
                deliveryAddress.isDefault(),
                deliveryAddress.getCreatedAt());
    }

    @Transactional(readOnly = true)
    public DeliveryAddressInfoListResponse findDeliveryAddressList(String cursor, int pageSize) {
        Cursor decoded = Cursor.fromBase64(cursor);
        int limit = pageSize + 1;
        List<DeliveryAddressInfoResponse> addressList =
                deliveryAddressRepository.findDeliveryAddressByCursor(
                        memberValidator.getCurrentMember().getMemberId(),
                        decoded.getCreatedAt(),
                        decoded.getId(),
                        limit);
        boolean hasNext = addressList.size() > pageSize;
        List<DeliveryAddressInfoResponse> resultList =
                hasNext ? addressList.subList(0, pageSize) : addressList;
        String nextCursor = null;
        if (hasNext) {
            DeliveryAddressInfoResponse lastItem = resultList.get(resultList.size() - 1);
            Cursor next = new Cursor(lastItem.createdAt(), lastItem.id());
            nextCursor = next.toString(); // Base64 직렬화
        }
        return new DeliveryAddressInfoListResponse(resultList, nextCursor, hasNext);
    }

    private DeliveryAddress validDeliveryAddress(UUID deliveryAddressId) {
        DeliveryAddress address =
                deliveryAddressRepository
                        .findById(deliveryAddressId)
                        .orElseThrow(
                                () ->
                                        new CommonException(
                                                DeliveryAddressErrorCode
                                                        .DELIVERY_ADDRESS_NOT_FOUND));
        if (!address.getMember().equals(memberValidator.getCurrentMember()))
            throw new CommonException(MemberErrorCode.UNAUTHORIZED_ACCESS);
        return address;
    }
}
