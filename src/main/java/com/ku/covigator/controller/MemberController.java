package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PatchMemberRequest;
import com.ku.covigator.dto.request.PostVerifyNicknameRequest;
import com.ku.covigator.exception.badrequest.PasswordVerificationException;
import com.ku.covigator.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "회원")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원 정보 수정")
    @PatchMapping("/{member_id}")
    public ResponseEntity<Void> updateMember(
            @PathVariable(value = "member_id") Long memberId,
            @Valid @RequestBody PatchMemberRequest request
            ) {

        if (!request.password().equals(request.passwordVerification())) {
            throw new PasswordVerificationException();
        }

        memberService.updateMember(memberId, request.nickname(), request.password());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "닉네임 중복 확인")
    @PostMapping("/nickname")
    public ResponseEntity<Void> verifyNickname(
            @Valid @RequestBody PostVerifyNicknameRequest request
    ) {
        memberService.verifyNicknameDuplication(request.nickname());
        return ResponseEntity.ok().build();
    }
}
