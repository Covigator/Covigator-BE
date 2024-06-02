package com.ku.covigator.controller;

import com.ku.covigator.dto.request.PostSignInRequest;
import com.ku.covigator.dto.response.AccessTokenResponse;
import com.ku.covigator.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
