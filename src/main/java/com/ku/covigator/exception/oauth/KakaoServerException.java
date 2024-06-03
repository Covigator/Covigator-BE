package com.ku.covigator.exception.oauth;

import org.springframework.http.HttpStatus;

public class KakaoServerException extends OauthException{
    public KakaoServerException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, 2002, "카카오 서버에서 오류가 발생했습니다.");
    }
}
