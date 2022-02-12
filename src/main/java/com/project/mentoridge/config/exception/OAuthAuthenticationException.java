package com.project.mentoridge.config.exception;

import com.project.mentoridge.config.response.ErrorCode;
import org.springframework.security.core.AuthenticationException;

public class OAuthAuthenticationException extends AuthenticationException {

    public static final String UNSUPPORTED = "지원하지 않는 형식입니다.";
    public static final String UNPARSABLE = "사용자 정보를 가져올 수 없습니다.";

    private ErrorCode errorCode;

    public OAuthAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
        this.errorCode = ErrorCode.UNAUTHENTICATED;
    }

    public OAuthAuthenticationException(String msg) {
        super(msg);
        this.errorCode = ErrorCode.UNAUTHENTICATED;
    }
}
