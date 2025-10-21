package com.irum.come2us.global.infrastructure.config.jpa;

import com.irum.come2us.global.domain.BaseEntity;
import com.irum.come2us.global.security.MemberDetails;
import jakarta.persistence.PreRemove;
import org.springframework.security.core.context.SecurityContextHolder;

public class SoftDeleteListener {
    @PreRemove
    public void onPreRemove(BaseEntity baseEntity) {

        Long currentMember = getCurrentMemberId(); // SecurityContext에서 추출
        baseEntity.markDeleted(currentMember);
    }

    private Long getCurrentMemberId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return null;
        return ((MemberDetails) auth.getPrincipal()).getUserId();
    }
}
