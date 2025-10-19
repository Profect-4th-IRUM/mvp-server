package com.irum.come2us.domain.category.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "p_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "category_id", updatable = false, nullable = false)
    private UUID categoryId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;  // 카테고리명

    // 카테고리의 계층 구조 표현 (자기참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;  // 부모 카테고리

    @Column(name = "depth", nullable = false, columnDefinition = "TINYINT")
    private int depth;  // 분류 단계
}
