package com.irum.come2us.domain.category.domain.entity;

import com.irum.come2us.global.domain.BaseEntity;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.CategoryErrorCode;
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
public class Category extends BaseEntity {

    private static final int MAX_DEPTH = 3;

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

    public static Category createRootCategory(String name) {
        return Category.builder().name(name).depth(1).build();
    }

    public static Category createSubCategory(String name, Category parent) {
        if (parent.getDepth() >= MAX_DEPTH) {
            throw new CommonException(CategoryErrorCode.CATEGORY_DEPTH_EXCEEDED);
        }

        Category child =
                Category.builder().name(name).parent(parent).depth(parent.getDepth() + 1).build();

        parent.addChild(child);
        return child;
    }

    private void addChild(Category child) {
        this.children.add(child);
    }

    public void updateName(String name) {
        this.name = name;
    }
}
