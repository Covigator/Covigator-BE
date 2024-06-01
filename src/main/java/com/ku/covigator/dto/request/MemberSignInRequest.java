package com.ku.covigator.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberSignInRequest {

    private String email;
    private String password;
}
