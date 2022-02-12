package com.project.mentoridge.config.exception;

import com.project.mentoridge.config.response.ErrorCode;
import lombok.Getter;

@Getter
public class GlobalException extends RuntimeException {

    private ErrorCode errorCode;
    // private String message;

    public GlobalException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public GlobalException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
