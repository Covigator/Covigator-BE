package com.ku.covigator.exception.jwt;

import com.ku.covigator.exception.badrequest.BadRequestException;

public class JwtInvalidException extends BadRequestException {

    public JwtInvalidException() {
        super(10001, "유효하지 않은 JWT 토큰입니다.");
    }

}
