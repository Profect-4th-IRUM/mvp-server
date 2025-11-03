package com.irum.come2us.domain.member.application.service;

import com.irum.come2us.domain.member.application.util.MemberValidator;
import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.domain.member.presentation.dto.request.MemberCreateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberInfoUpdateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberPasswordUpdateRequest;
import com.irum.come2us.domain.member.presentation.dto.response.MemberInfoListResponse;
import com.irum.come2us.domain.member.presentation.dto.response.MemberInfoResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ManagerService {
    private final MemberRepository memberRepository;
    private final MemberValidator memberValidator;
    private final BCryptPasswordEncoder passwordEncoder;

    public void createManager(MemberCreateRequest request) {
        memberValidator.assertEmailIsNotTaken(request.email());
        memberRepository.save(
                Member.createManager(
                        request.email(),
                        passwordEncoder.encode(request.password()),
                        request.name(),
                        request.contact()));
    }

    @Transactional(readOnly = true)
    public MemberInfoResponse findManagerInfo(Long memberId) {
        Member member = memberValidator.getMemberById(memberId);
        return MemberInfoResponse.createMemberInfoResponse(member);
    }

    @Transactional(readOnly = true)
    public MemberInfoListResponse findManagerInfoList(Long lastMemberId, Integer pageSize) {
        if (pageSize == null || (pageSize != 10 && pageSize != 30 && pageSize != 50)) {
            log.warn("허용되지 않은 size 요청: {} -> 기본값 10으로 대체", pageSize);
            pageSize = 10;
        }
        int limit = pageSize + 1;
        List<MemberInfoResponse> memberInfoList =
                memberRepository.findMembersByCursor(lastMemberId, limit);
        boolean hasNext = memberInfoList.size() > pageSize;
        Long nextCursor = null;
        List<MemberInfoResponse> responseList = memberInfoList;
        if (hasNext) {
            responseList = memberInfoList.subList(0, pageSize);
            nextCursor = responseList.get(pageSize - 1).memberId();
        }

        return new MemberInfoListResponse(responseList, nextCursor, hasNext);
    }

    public void changeManagerNameAndContact(Long memberId, MemberInfoUpdateRequest request) {
        memberValidator.applyValidUpdate(
                memberValidator.getMemberById(memberId), request.name(), request.contact());
    }

    public void changeManagerPassword(Long memberId, MemberPasswordUpdateRequest request) {
        Member member = memberValidator.getMemberById(memberId);
        memberValidator.validatePassword(request.originalPassword(), request.newPassword(), member);
        member.updatePassword(passwordEncoder.encode(request.newPassword()));
    } // 추후 BCryptEncoder 사용한 암/복호화 검증 로직 적용 예정

    public void removeManager(Long memberId) {
        Member member = memberValidator.getMemberById(memberId);
        memberValidator.assertMemberIsManager(member);
        member.updateDeletedAt();
    }
}
