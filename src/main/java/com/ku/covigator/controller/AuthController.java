package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PostSignInRequest;
import com.ku.covigator.dto.request.PostSignUpRequest;
import com.ku.covigator.dto.response.AccessTokenResponse;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-in")
    public ResponseEntity<AccessTokenResponse> signIn(@RequestBody PostSignInRequest request) {
        String accessToken = authService.signIn(request.email(), request.password());
        return ResponseEntity.ok(AccessTokenResponse.from(accessToken));
    }

    @PostMapping("sign-up")
    public ResponseEntity<AccessTokenResponse> signUp(@RequestBody PostSignUpRequest request) {
        String accessToken = authService.signUp(request.toEntity());
        return ResponseEntity.ok(AccessTokenResponse.from(accessToken));
    }

    @GetMapping("/oauth/kakao")
    public ResponseEntity<KakaoSignInResponse> signInKakao(@RequestParam String code) {
        return ResponseEntity.ok(authService.signInKakao(code));
    }

}
