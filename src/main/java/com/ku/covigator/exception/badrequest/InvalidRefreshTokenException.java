package com.ku.covigator.exception.badrequest;

public class InvalidRefreshTokenException extends BadRequestException{

    public InvalidRefreshTokenException() {
        super(3006, "Refresh Token이 유효하지 않습니다.");
    }
}
