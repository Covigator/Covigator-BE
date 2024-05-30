package com.covigator.Covigator.exception.badrequest;

public class PasswordMismatchException extends BadRequestException{
    public PasswordMismatchException() {
        super(1001, "비밀번호가 일치하지 않습니다.");
    }

}
