package com.ku.covigator.support.slack;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RequestInfo {

    private String header;
    private String remoteAddr;
    private String method;
    private String requestURL;

    public static RequestInfo from(HttpServletRequest request) {
        return RequestInfo.builder()
                .header(request.getHeader("X-FORWARDED-FOR"))
                .remoteAddr(request.getRemoteAddr())
                .method(request.getMethod())
                .requestURL(request.getRequestURI())
                .build();
    }
}
