package com.ku.covigator.exception.notfound;

public class NotFoundCourseException extends NotFoundException{
    public NotFoundCourseException() {
        super(1002, "존재하지 않는 코스입니다");
    }
}
