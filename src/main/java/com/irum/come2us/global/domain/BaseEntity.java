package com.irum.come2us.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners({AuditingEntityListener.class})
@Getter
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity {
    @CreatedBy
    @Column(updatable = false, nullable = false)
    private Long createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    private Long updatedBy;

    private Long deletedBy;

    public void softDelete(Long deletedBy) {
        super.updateDeletedAt();
        this.deletedBy = deletedBy;
    }
}
