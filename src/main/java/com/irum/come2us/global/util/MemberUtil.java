package com.irum.come2us.global.util;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.AuthErrorCode;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import com.irum.come2us.global.security.MemberDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MemberUtil {

    private final MemberRepository memberRepository;

    public Member getCurrentMember() {
        return memberRepository
                .findByMemberId(getCurrentMemberId())
                .orElseThrow(() -> new CommonException(AuthErrorCode.AUTHENTICATION_NOT_FOUND));
    } // 로그인 된 유저 정보 조회

    public void assertMemberResourceAccess(Member member) {
        if (!member.getMemberId().equals(getCurrentMember().getMemberId()))
            throw new CommonException(MemberErrorCode.UNAUTHORIZED_ACCESS);
    }

    private Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new CommonException(AuthErrorCode.AUTHENTICATION_NOT_FOUND);
        }
        try {
            MemberDetails memberDetails = (MemberDetails) authentication.getPrincipal();
            return memberDetails.getUserId();
        } catch (ClassCastException e) {
            log.warn(e.getMessage());
            throw new CommonException(AuthErrorCode.AUTHENTICATION_NOT_FOUND);
        } catch (Exception e) {
            log.warn(e.getMessage());
            throw new CommonException(AuthErrorCode.AUTHENTICATION_NOT_FOUND);
        }
    } // 로그인 된 아이디 반환
}
