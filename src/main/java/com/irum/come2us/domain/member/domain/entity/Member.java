package com.irum.come2us.domain.member.domain.entity;

import com.irum.come2us.domain.member.domain.entity.enums.Role;
import com.irum.come2us.global.constants.RegexConstants;
import com.irum.come2us.global.domain.BaseTimeEntity;
import com.irum.come2us.global.presentation.advice.exception.CommonException;
import com.irum.come2us.global.presentation.advice.exception.errorcode.MemberErrorCode;
import jakarta.persistence.*;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

@Getter
@Entity
@Table(name = "p_member")
@Where(clause = "deleted_at IS NULL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

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
        this.email = validEmail(email);
        this.password = password;
        this.name = name;
        this.contact = validContact(contact);
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

    public void updatePassword(String password) {
        this.password = password;
    }

    public void grantOwner() {
        this.role = Role.OWNER;
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile(RegexConstants.EMAIL);
    private static final Pattern PHONE_NUMBER_PATTERN =
            Pattern.compile(RegexConstants.PHONE_NUMBER);

    private String validEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new CommonException(MemberErrorCode.INVALID_EMAIL);
        }
        return email;
    }

    private String validContact(String contact) {
        if (!PHONE_NUMBER_PATTERN.matcher(contact).matches()) {
            throw new CommonException(MemberErrorCode.INVALID_CONTACT);
        }
        return contact;
    }
}
