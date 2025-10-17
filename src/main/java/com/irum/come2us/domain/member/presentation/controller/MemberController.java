package com.irum.come2us.domain.member.presentation.controller;

import com.irum.come2us.domain.member.application.service.MemberService;
import com.irum.come2us.domain.member.presentation.dto.request.MemberCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
