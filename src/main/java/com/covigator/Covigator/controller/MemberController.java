package com.covigator.Covigator.controller;

import com.covigator.Covigator.dto.request.MemberSignUpRequest;
import com.covigator.Covigator.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> signUp(@RequestBody MemberSignUpRequest request) {
        memberService.signUp(request.toEntity());
        return ResponseEntity.ok().build();
    }
}
