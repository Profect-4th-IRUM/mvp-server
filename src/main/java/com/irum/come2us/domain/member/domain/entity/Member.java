package com.irum.come2us.domain.member.domain.entity;

import com.irum.come2us.domain.member.domain.entity.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "p_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(String email, String password, String name, String contact, Role role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.role = role;
    }

    public static Member createCustomer(
            String email, String password, String name, String contact) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .contact(contact)
                .role(Role.CUSTOMER)
                .build();
    }

    public static Member createOwner(String email, String password, String name, String contact) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .contact(contact)
                .role(Role.OWNER)
                .build();
    }

    public static Member createManager(String email, String password, String name, String contact) {
        return Member.builder()
                .email(email)
                .password(password)
                .name(name)
                .contact(contact)
                .role(Role.MANAGER)
                .build();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateContact(String contact) {
        this.contact = contact;
    }

    // 공통예외핸들러 개발 후 이메일, 연락처 정규화 로직 추가 예정
}
