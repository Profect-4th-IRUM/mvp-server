package com.irum.come2us.global.domain;

import com.irum.come2us.global.infrastructure.config.jpa.SoftDeleteListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
@Getter
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity {
    @CreatedBy
    @Column(updatable = false, nullable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    private Long updatedBy;

    @Column(updatable = false)
    private Long deletedBy;

    public void markDeleted(Long memberId) {
        this.deletedBy = memberId;
    }
}
