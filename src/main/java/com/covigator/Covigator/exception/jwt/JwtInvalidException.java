package com.covigator.Covigator.exception.jwt;

import com.covigator.Covigator.exception.badrequest.BadRequestException;

public class JwtInvalidException extends BadRequestException {

    public JwtInvalidException() {
        super(10001, "유효하지 않은 JWT 토큰입니다.");
    }

}
