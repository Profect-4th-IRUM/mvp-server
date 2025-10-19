package com.irum.come2us.domain.member.application.util;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberValidator {
    private final MemberRepository memberRepository;

    public Member getCurrentMember() {
        return memberRepository
                .findByMemberId(1L)
                .orElseThrow(() -> new CommonException(MemberErrorCode.MEMBER_NOT_FOUND));
    } // Spring Security 도입 후 SecurityContextHolder를 통해 검증하도록 변경 예정

    public Member getMemberById(Long memberId) {
        return memberRepository
                .findByMemberId(memberId)
                .orElseThrow(() -> new CommonException(MemberErrorCode.MEMBER_NOT_FOUND));
    } // Spring Security 도입 후 SecurityContextHolder를 통해 검증하도록 변경 예정

    public void validatePassword(String originalPassword, String newPassword, Member member) {
        if (!member.getPassword()
                .equals(originalPassword)) // 추후 단순 equal 값 비교가 아닌 인코딩 복호화값 비교 로직으로 변환 예정
        throw new CommonException(MemberErrorCode.INVALID_PASSWORD);
        if (member.getPassword().equals(newPassword))
            throw new CommonException(MemberErrorCode.DUPLICATED_PASSWORD);
    }

    public void validateNewOwnerRegistration(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            Role role = member.get().getRole();
            switch (role) {
                case OWNER -> throw new CommonException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
                case CUSTOMER -> throw new CommonException(MemberErrorCode.OWNER_UPGRADE_REQUIRED);
            }
        }
    }

    public void assertMemberIsNotOwner(Member member) {
        if (member.getRole().equals(Role.OWNER))
            throw new CommonException(MemberErrorCode.ROLE_ALREADY_GRANTED);
    }

    public void assertEmailIsNotTaken(String email) {
        if (memberRepository.findByEmail(email).isPresent())
            throw new CommonException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
    }

    public void assertMemberIsCustomer(Member member) {
        Role role = member.getRole();
        if (!role.equals(Role.CUSTOMER)) {
            switch (role) {
                case OWNER -> throw new CommonException(MemberErrorCode.OWNER_CANNOT_WITHDRAW);
                case MANAGER -> throw new CommonException(MemberErrorCode.MANAGER_CANNOT_WITHDRAW);
            }
        }
    }

    public void assertMemberIsManager(Member member) {
        if (!member.getRole().equals(Role.MANAGER))
            throw new CommonException(MemberErrorCode.MEMBER_IS_NOT_MANAGER);
    }
}
