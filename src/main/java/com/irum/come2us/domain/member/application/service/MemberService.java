package com.irum.come2us.domain.member.application.service;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.domain.member.domain.repository.ClientRepository;
import com.irum.come2us.domain.member.presentation.dto.request.MemberCreateRequest;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final ClientRepository clientRepository;

    public void createCustomer(MemberCreateRequest request) {
        verifyCustomerAlreadyExists(request.email());
        clientRepository.save(
                Member.createCustomer(
                        request.email(), request.password(), request.name(), request.contact()));
    }

    public void createOwner(MemberCreateRequest request) {
        verifyMemberAlreadyExists(request.email());
        clientRepository.save(
                Member.createOwner(
                        request.email(), request.password(), request.name(), request.contact()));
    }

    private void verifyCustomerAlreadyExists(String email) {
        if (clientRepository.findByEmail(email).isPresent())
            throw new CommonException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
    }

    private void verifyMemberAlreadyExists(String email) {
        Optional<Member> member = clientRepository.findByEmail(email);
        if (member.isPresent()) {
            Role role = member.get().getRole();
            switch (role) {
                case OWNER -> throw new CommonException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
                case CUSTOMER -> throw new CommonException(MemberErrorCode.OWNER_UPGRADE_REQUIRED);
            }
        }
    }
}
