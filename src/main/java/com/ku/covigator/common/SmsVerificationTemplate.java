package com.ku.covigator.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SmsVerificationTemplate {

    CONTENT_PREFIX("[Covigator] 본인확인 인증번호 ["),
    CONTENT_SUFFIX("] 입니다. \"타인 노출 금지\"");

    private final String text;

}
