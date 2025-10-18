package com.irum.come2us.domain.member.application.service;

import com.irum.come2us.domain.member.application.util.MemberValidator;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.domain.member.presentation.dto.request.MemberCreateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberInfoUpdateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberPasswordUpdateRequest;
import com.irum.come2us.domain.member.presentation.dto.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;

    public void createCustomer(MemberCreateRequest request) {
        memberValidator.assertEmailIsNotTaken(request.email());
        memberRepository.save(
                Member.createCustomer(
                        request.email(), request.password(), request.name(), request.contact()));
    }

    public void createOwner(MemberCreateRequest request) {
        memberValidator.validateNewOwnerRegistration(request.email());
        memberRepository.save(
                Member.createOwner(
                        request.email(), request.password(), request.name(), request.contact()));
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse findMemberInfo() {
        Member member = memberValidator.getCurrentMember();
        return MemberInfoResponse.createMemberInfoResponse(member);
    }

    public void changeMemberNameAndContact(MemberInfoUpdateRequest request) {
        Member member = memberValidator.getCurrentMember();
        member.updateName(request.name());
        member.updateContact(request.contact());
    }

    public void changeMemberPassword(MemberPasswordUpdateRequest request) {
        Member member = memberValidator.getCurrentMember();
        memberValidator.validatePassword(request.originalPassword(), request.newPassword(), member);
        member.updatePassword(request.newPassword());
    } // 추후 BCryptEncoder 사용한 암/복호화 검증 로직 적용 예정

    public void changeCustomerRoleToOwner() {
        Member member = memberValidator.getCurrentMember();
        memberValidator.assertMemberIsNotOwner(member);
        member.grantOwner();
    }

    public void withdrawCustomer() {
        Member member = memberValidator.getCurrentMember();
        memberValidator.assertMemberIsCustomer(member);
        memberRepository.delete(member);
    }
}
