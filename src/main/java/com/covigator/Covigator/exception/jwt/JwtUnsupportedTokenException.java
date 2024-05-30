package com.covigator.Covigator.exception.jwt;

import com.covigator.Covigator.exception.badrequest.BadRequestException;

public class JwtUnsupportedTokenException extends BadRequestException {

    public JwtUnsupportedTokenException() {
        super(10004, "지원되지 않는 JWT 토큰입니다.");
    }

}
