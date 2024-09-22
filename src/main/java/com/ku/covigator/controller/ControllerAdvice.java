package com.ku.covigator.controller;

import com.ku.covigator.dto.response.ErrorResponse;
import com.ku.covigator.exception.CovigatorException;
import com.ku.covigator.support.slack.SlackAlarmGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private final SlackAlarmGenerator slackAlarmGenerator;

    @ExceptionHandler(CovigatorException.class)
    public ResponseEntity<ErrorResponse> handleCovigatorException(CovigatorException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleInternalException(Exception e, HttpServletRequest request) {
        slackAlarmGenerator.sendSlackAlertErrorLog(e, request);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(9999, "서버 복구중입니다. 잠시만 기다려주세요."));
    }

}
