package com.ku.covigator.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CovigatorException extends RuntimeException{

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    public CovigatorException(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
