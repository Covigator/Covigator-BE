package com.ku.covigator.exception.notfound;

public class NotFoundPlaceException extends NotFoundException{
    public NotFoundPlaceException() {
        super(1001, "존재하지 않는 장소입니다.");
    }
}
