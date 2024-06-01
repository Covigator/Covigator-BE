package com.ku.covigator.exception.jwt;

import com.ku.covigator.exception.badrequest.BadRequestException;

public class JwtUnsupportedTokenException extends BadRequestException {

    public JwtUnsupportedTokenException() {
        super(10004, "지원되지 않는 JWT 토큰입니다.");
    }

}
