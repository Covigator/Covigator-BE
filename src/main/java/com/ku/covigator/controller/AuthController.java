package com.ku.covigator.controller;

import com.ku.covigator.dto.request.FindPasswordRequest;
import com.ku.covigator.dto.request.PostSignInRequest;
import com.ku.covigator.dto.request.PostSignUpRequest;
import com.ku.covigator.dto.response.AccessTokenResponse;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Auth", description = "인증/인가")
@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "로컬 로그인")
    @PostMapping("/sign-in")
    public ResponseEntity<AccessTokenResponse> signIn(@RequestBody @Valid PostSignInRequest request) {
        String accessToken = authService.signIn(request.email(), request.password());
        return ResponseEntity.ok(AccessTokenResponse.from(accessToken));
    }

    @Operation(summary = "회원가입")
    @PostMapping("/sign-up")
    public ResponseEntity<AccessTokenResponse> signUp(@RequestPart(value = "postSignUpRequest") @Valid PostSignUpRequest request,
                                                      @RequestPart(value = "image", required = false) MultipartFile image) {
        String accessToken = authService.signUp(request.toEntity(), image);
        return ResponseEntity.ok(AccessTokenResponse.from(accessToken));
    }

    @Operation(summary = "비밀번호 찾기 (임시 비밀번호 설정)")
    @PostMapping("/find-password")
    public ResponseEntity<Void> findPassword(@Valid @RequestBody FindPasswordRequest request) {
        authService.createVerificationNumber(request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "카카오 로그인 (카카오 서버 Redirect 용, 프론트에서 호출하지 않음)")
    @GetMapping("/oauth/kakao")
    public ResponseEntity<KakaoSignInResponse> signInKakao(@RequestParam String code) {
        return ResponseEntity.ok(authService.signInKakao(code));
    }

}
