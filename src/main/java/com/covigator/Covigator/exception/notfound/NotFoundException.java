package com.covigator.Covigator.exception.notfound;

import com.covigator.Covigator.exception.CovigatorException;
import org.springframework.http.HttpStatus;

public class NotFoundException extends CovigatorException {
    public NotFoundException(int code, String message) {
        super(HttpStatus.NOT_FOUND, code, message);
    }
}
