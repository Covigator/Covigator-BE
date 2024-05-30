package com.covigator.Covigator.controller;

import com.covigator.Covigator.dto.request.MemberSignInRequest;
import com.covigator.Covigator.dto.response.AccessTokenResponse;
import com.covigator.Covigator.service.AuthService;
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
    public ResponseEntity<AccessTokenResponse> signIn(@RequestBody MemberSignInRequest request) {
        String accessToken = authService.signIn(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(AccessTokenResponse.from(accessToken));
    }

}
