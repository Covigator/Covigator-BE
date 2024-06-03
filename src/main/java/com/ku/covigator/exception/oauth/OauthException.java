package com.ku.covigator.exception.oauth;

import com.ku.covigator.exception.CovigatorException;
import org.springframework.http.HttpStatus;

public class OauthException extends CovigatorException {
    public OauthException(HttpStatus httpStatus, int code, String message) {
        super(httpStatus, code, message);
    }
}
