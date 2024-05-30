package com.covigator.Covigator.exception.jwt;

import com.covigator.Covigator.exception.badrequest.BadRequestException;

public class JwtMalformedTokenException extends BadRequestException {

    public JwtMalformedTokenException() {
        super(10002, "잘못된 JWT 서명입니다.");
    }

}
