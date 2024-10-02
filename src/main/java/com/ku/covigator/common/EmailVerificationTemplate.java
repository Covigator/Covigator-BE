package com.ku.covigator.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailVerificationTemplate {

    SUBJECT("[Covigator] 비밀번호 찾기 인증번호입니다."),
    CONTENT_PREFIX("인증번호는 "),
    CONTENT_SUFFIX(" 입니다.\n인증번호의 유효 기간은 5분 입니다.");

    private final String text;

}
