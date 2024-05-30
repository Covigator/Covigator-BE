package com.covigator.Covigator.exception.badrequest;

import com.covigator.Covigator.exception.CovigatorException;
import org.springframework.http.HttpStatus;

public class BadRequestException extends CovigatorException {
    public BadRequestException(int code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }
}
