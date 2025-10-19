package com.irum.come2us.global.security;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member =
                memberRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return new MemberDetails(member);
    }
}
