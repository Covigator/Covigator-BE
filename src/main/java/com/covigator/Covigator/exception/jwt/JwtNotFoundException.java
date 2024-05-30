package com.covigator.Covigator.exception.jwt;

import com.covigator.Covigator.exception.badrequest.BadRequestException;

public class JwtNotFoundException extends BadRequestException {

    public JwtNotFoundException() {
        super(10003, "JWT 토큰이 존재하지 않습니다.");
    }

}
