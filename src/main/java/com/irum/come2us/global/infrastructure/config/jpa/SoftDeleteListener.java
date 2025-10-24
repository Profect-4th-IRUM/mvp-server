package com.irum.come2us.global.infrastructure.config.jpa;

import com.irum.come2us.global.domain.BaseEntity;
import com.irum.come2us.global.security.MemberDetails;
import jakarta.persistence.PreRemove;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Deprecated
public class SoftDeleteListener {
    @PreRemove
    public void onPreRemove(BaseEntity baseEntity) {
        Long currentMember = getCurrentMemberId(); // SecurityContext에서 추출
        baseEntity.softDelete(currentMember);
        log.info("삭제 요청 멤버: :{}", currentMember);
    }

    private Long getCurrentMemberId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return null;
        return ((MemberDetails) auth.getPrincipal()).getUserId();
    }
}
