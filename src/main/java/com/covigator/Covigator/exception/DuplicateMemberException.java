package com.covigator.Covigator.exception;

import lombok.Getter;

@Getter
public class DuplicateMemberException extends RuntimeException{

    public DuplicateMemberException(String message) {
        super(message);
    }

}
