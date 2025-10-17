package com.irum.come2us.domain.member.presentation.controller;

import com.irum.come2us.domain.member.application.service.MemberService;
import com.irum.come2us.domain.member.presentation.dto.request.MemberCreateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberInfoUpdateRequest;
import com.irum.come2us.domain.member.presentation.dto.request.MemberPasswordUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<Void> joinCustomer(@RequestBody MemberCreateRequest request) {
        log.info("고객 회원가입 요청: {}", request);
        memberService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/owner-signup")
    public ResponseEntity<Void> joinOwner(@RequestBody MemberCreateRequest request) {
        log.info("판매자 회원가입 요청: {}", request);
        memberService.createOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/info")
    public ResponseEntity<Void> updateMemberInfo(@RequestBody MemberInfoUpdateRequest request) {
        log.info("멤버 개인정보 수정 요청: {}", request);
        memberService.changeMemberNameAndContact(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/password")
    public ResponseEntity<Void> updateMemberPassword(
            @RequestBody MemberPasswordUpdateRequest request) {
        log.info("멤버 비밀번호 변경 요청: {}", request);
        memberService.changeMemberPassword(request);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/role")
    public ResponseEntity<Void> updateCustomerToOwner() {
        memberService.changeCustomerRoleToOwner();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCustomer() {
        memberService.withdrawCustomer();
        return ResponseEntity.noContent().build();
    }
}
