package com.ku.covigator.exception.notfound;

public class NotFoundDibsException extends NotFoundException{
    public NotFoundDibsException() {
        super(1003, "좋아요(찜)가 존재하지 않습니다.");
    }
}
