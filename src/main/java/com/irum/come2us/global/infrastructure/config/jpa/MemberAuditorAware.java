package com.irum.come2us.global.infrastructure.config.jpa;

import com.irum.come2us.global.security.MemberDetails;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class MemberAuditorAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || authentication.getPrincipal() == null
                || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        if (authentication.getPrincipal() instanceof MemberDetails) {
            MemberDetails nowMember = (MemberDetails) authentication.getPrincipal();
            Long memberId = nowMember.getUserId();
            log.info("현재 유저: {}", nowMember.getUserId());
            return Optional.of(memberId);
        }
        return Optional.empty();
    }
}
