package com.irum.come2us.domain.member.application.service;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.domain.member.domain.repository.ClientRepository;
import com.irum.come2us.domain.member.presentation.dto.request.MemberCreateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberInfoUpdateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberPasswordUpdateRequest;
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
        assertEmailIsNotTaken(request.email());
        clientRepository.save(
                Member.createCustomer(
                        request.email(), request.password(), request.name(), request.contact()));
    }

    public void createOwner(MemberCreateRequest request) {
        validateNewOwnerRegistration(request.email());
        clientRepository.save(
                Member.createOwner(
                        request.email(), request.password(), request.name(), request.contact()));
    }

    public void changeMemberNameAndContact(MemberInfoUpdateRequest request) {
        Member member = getMember();
        member.updateName(request.name());
        member.updateContact(request.contact());
    }

    public void changeMemberPassword(MemberPasswordUpdateRequest request) {
        Member member = getMember();
        validatePassword(request.originalPassword(), request.newPassword(), member);
        member.updatePassword(request.newPassword());
    } // 추후 BCryptEncoder 사용한 암/복호화 검증 로직 적용 예정

    public void changeCustomerRoleToOwner() {
        Member member = getMember();
        assertMemberIsNotOwner(member);
        member.grantOwner();
    }

    // 검증 로직
    private void assertEmailIsNotTaken(String email) {
        if (clientRepository.findByEmail(email).isPresent())
            throw new CommonException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
    }

    private void validateNewOwnerRegistration(String email) {
        Optional<Member> member = clientRepository.findByEmail(email);
        if (member.isPresent()) {
            Role role = member.get().getRole();
            switch (role) {
                case OWNER -> throw new CommonException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
                case CUSTOMER -> throw new CommonException(MemberErrorCode.OWNER_UPGRADE_REQUIRED);
            }
        }
    }

    private void validatePassword(String originalPassword, String newPassword, Member member) {
        if (member.getPassword().equals(originalPassword))
            throw new CommonException(MemberErrorCode.INVALID_PASSWORD);
        if (member.getPassword().equals(newPassword))
            throw new CommonException(MemberErrorCode.DUPLICATED_PASSWORD);
    }

    private Member getMember() {
        return clientRepository
                .findById(0L)
                .orElseThrow(() -> new CommonException(MemberErrorCode.MEMBER_NOT_FOUND));
    } // Spring Security 도입 후 SecurityContextHolder를 통해 검증하도록 변경 예정

    private void assertMemberIsNotOwner(Member member) {
        if (member.getRole().equals(Role.OWNER))
            throw new CommonException(MemberErrorCode.ROLE_ALREADY_GRANTED);
    }
}
