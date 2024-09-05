package com.ku.covigator.exception.notfound;

public class NotFoundLikeException extends NotFoundException{
    public NotFoundLikeException() {
        super(1003, "좋아요(찜)가 존재하지 않습니다.");
    }
}
