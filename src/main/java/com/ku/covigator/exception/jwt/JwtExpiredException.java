package com.ku.covigator.exception.jwt;

import com.ku.covigator.exception.badrequest.BadRequestException;

public class JwtExpiredException extends BadRequestException {

    public JwtExpiredException() {
        super(10000, "만료된 JWT 토큰입니다.");
    }

}
