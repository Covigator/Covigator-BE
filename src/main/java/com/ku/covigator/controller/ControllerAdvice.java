package com.ku.covigator.controller;

import com.ku.covigator.dto.response.ErrorResponse;
import com.ku.covigator.exception.CovigatorException;
import com.ku.covigator.support.slack.SlackAlarmGenerator;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Objects;

@RestControllerAdvice
@RequiredArgsConstructor
public class ControllerAdvice {

    private final SlackAlarmGenerator slackAlarmGenerator;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleInputFieldException(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(9000, Objects.requireNonNull(e.getFieldError()).getDefaultMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonException() {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(9001, "Json 형식이 올바르지 않습니다."));
    }

    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ErrorResponse> handleContentTypeException() {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(9002, "ContentType 값이 올바르지 않습니다."));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleRequestMethodException() {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(9003, "해당 Http Method에 맞는 API가 존재하지 않습니다."));
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleFileSizeLimitExceeded() {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(9004, "이미지 용량이 너무 큽니다."));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParamException() {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(9005, "요청 파라미터 이름이 올바르지 않습니다."));
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingMultiPartParamException( ) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(9006, "요청 MultipartFile 파라미터 이름이 올바르지 않습니다."));
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException() {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(9007, "요청 파일이 올바르지 않습니다. 파일 손상 여부나 요청 형식을 확인해주세요."));
    }

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
