package com.ku.covigator.exception.notfound;

public class NotFoundEmailException extends NotFoundException{

    public NotFoundEmailException() {
        super(1004, "등록되지 않은 이메일입니다.");
    }
}
