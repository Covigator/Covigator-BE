package com.ku.covigator.controller;

import com.ku.covigator.dto.request.*;
import com.ku.covigator.dto.response.TokenResponse;
import com.ku.covigator.dto.response.KakaoSignInResponse;
import com.ku.covigator.exception.badrequest.PasswordVerificationException;
import com.ku.covigator.exception.badrequest.WrongVerificationCodeException;
import com.ku.covigator.security.jwt.LoggedInMemberId;
import com.ku.covigator.service.AuthService;
import com.ku.covigator.service.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    private final RedisUtil redisUtil;

    @Operation(summary = "로컬 로그인")
    @PostMapping("/sign-in")
    public ResponseEntity<TokenResponse> signIn(@RequestBody @Valid PostSignInRequest request) {
        return ResponseEntity.ok(authService.signIn(request.email(), request.password()));
    }

    @Operation(summary = "회원가입")
    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponse> signUp(@RequestPart(value = "postSignUpRequest") @Valid PostSignUpRequest request,
                                                @RequestPart(value = "image", required = false) MultipartFile image) {
        return ResponseEntity.ok(authService.signUp(request.toEntity(), image));
    }

    @Operation(summary = "카카오 로그인 (인가 코드 확인)")
    @GetMapping("/oauth/kakao")
    public ResponseEntity<KakaoSignInResponse> signInKakao(@RequestParam String code) {
        return ResponseEntity.ok(authService.signInKakao(code));
    }

    @Operation(summary = "비밀번호 찾기 - 이메일 전송")
    @PostMapping("/find-password/send-email")
    public ResponseEntity<Void> findPassword(@Valid @RequestBody FindPasswordRequest request) {
        authService.createVerificationNumber(request.email());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 찾기 - 이메일 인증번호 인증")
    @PostMapping("/find-password/verify-code")
    public ResponseEntity<Void> verifyNumber(@Valid @RequestBody VerifyEmailCodeRequest request) {
        String code = redisUtil.getData(request.email());
        if(!code.equals(request.code())) {
            throw new WrongVerificationCodeException();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 찾기 - 문자 전송")
    @PostMapping("/find-email/send-message")
    public ResponseEntity<Void> sendMessage(@Parameter(hidden = true) @LoggedInMemberId Long memberId,
                                            @Valid @RequestBody SmsRequest request) {
        authService.sendMessage(memberId, request.phoneNumber());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "이메일 찾기 - 본인확인 인증")
    @PostMapping("/find-email/verify-code")
    public ResponseEntity<Void> verifyNumber(@Parameter(hidden = true) @LoggedInMemberId Long memberId,
                                             @Valid @RequestBody VerifySmsCodeRequest request) {
        String code = redisUtil.getData(memberId.toString());
        if(!code.equals(request.code())) {
            throw new WrongVerificationCodeException();
        }
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "비밀번호 변경")
    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Parameter(hidden = true) @LoggedInMemberId Long memberId,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        if (!request.password().equals(request.passwordVerification())) {
            throw new PasswordVerificationException();
        }
        authService.changePassword(memberId, request.password());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "액세스/리프레시 토큰 재발급")
    @PostMapping("/reissue-token")
    public ResponseEntity<TokenResponse> reissueToken(@Valid @RequestBody PostReissueTokenRequest request) {
        return ResponseEntity.ok(authService.reissueToken(request.refreshToken()));
    }
}
