package com.ku.covigator.exception.oauth;

import org.springframework.http.HttpStatus;

public class KakaoTokenRequestException extends OauthException{
    public KakaoTokenRequestException() {
        super(HttpStatus.BAD_REQUEST, 2003, "토큰 요청에 실패했습니다");
    }
}
