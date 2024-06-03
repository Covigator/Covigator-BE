package com.ku.covigator.exception.oauth;

import org.springframework.http.HttpStatus;

public class KakaoMemberInfoRequestException extends OauthException {
    public KakaoMemberInfoRequestException() {
        super(HttpStatus.BAD_REQUEST, 2001, "사용자 정보 요청에 실패했습니다");
    }
}
