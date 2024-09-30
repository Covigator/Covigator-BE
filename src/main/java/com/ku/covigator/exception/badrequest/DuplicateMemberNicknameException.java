package com.ku.covigator.exception.badrequest;

public class DuplicateMemberNicknameException extends BadRequestException{

    public DuplicateMemberNicknameException() {
        super(3003, "이미 존재하는 닉네임입니다.");
    }
}
