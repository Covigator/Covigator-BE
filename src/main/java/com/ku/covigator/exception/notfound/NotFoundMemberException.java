package com.ku.covigator.exception.notfound;

public class NotFoundMemberException extends NotFoundException{

    public NotFoundMemberException() {
        super(1000, "존재하지 않는 회원입니다");
    }
}
