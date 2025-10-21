package com.irum.come2us.domain.category.domain.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Entity
@Table(name = "p_category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Category {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "category_id", updatable = false, nullable = false)
    private UUID categoryId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> children = new ArrayList<>();

    @Column(name = "depth", nullable = false)
    private int depth;

    // 정적 팩토리 메서드만 public (외부에서 객체 생성 시 사용)
    public static Category createRootCategory(String name) {
        return Category.builder().name(name).depth(1).build();
    }

    public static Category createSubCategory(String name, Category parent) {
        return Category.builder().name(name).parent(parent).depth(parent.getDepth() + 1).build();
    }
}
