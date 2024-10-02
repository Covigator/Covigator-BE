package com.ku.covigator.exception.badrequest;

public class WrongVerificationCodeException extends BadRequestException{

    public WrongVerificationCodeException() {
        super(3005, "인증번호가 올바르지 않습니다. 다시 시도해주세요.");
    }
}
