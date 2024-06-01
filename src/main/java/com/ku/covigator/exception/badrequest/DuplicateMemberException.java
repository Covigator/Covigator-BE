package com.ku.covigator.exception.badrequest;

import lombok.Getter;

@Getter
public class DuplicateMemberException extends BadRequestException{

    public DuplicateMemberException() {
        super(1002, "이미 가입된 사용자입니다.");
    }
}
