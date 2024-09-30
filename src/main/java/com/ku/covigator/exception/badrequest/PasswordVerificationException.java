package com.ku.covigator.exception.badrequest;

public class PasswordVerificationException extends BadRequestException {

    public PasswordVerificationException() {
        super(3004, "비밀번호와 비밀번호 확인이 일치하지 않습니다.");
    }
}
