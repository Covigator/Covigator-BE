package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PostSignInRequest;
import com.ku.covigator.dto.request.PostSignUpRequest;
import com.ku.covigator.dto.response.AccessTokenResponse;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증/인가")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로컬 로그인")
    @PostMapping("/sign-in")
    public ResponseEntity<AccessTokenResponse> signIn(@RequestBody PostSignInRequest request) {
        String accessToken = authService.signIn(request.email(), request.password());
        return ResponseEntity.ok(AccessTokenResponse.from(accessToken));
    }

    @Operation(summary = "회원가입")
    @PostMapping("/sign-up")
    public ResponseEntity<AccessTokenResponse> signUp(@RequestBody PostSignUpRequest request) {
        String accessToken = authService.signUp(request.toEntity());
        return ResponseEntity.ok(AccessTokenResponse.from(accessToken));
    }

    @Operation(summary = "카카오 로그인")
    @GetMapping("/oauth/kakao")
    public ResponseEntity<KakaoSignInResponse> signInKakao(@RequestParam String code) {
        return ResponseEntity.ok(authService.signInKakao(code));
    }

}
