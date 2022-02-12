package com.project.mentoridge.config.exception;

import com.project.mentoridge.config.response.ErrorCode;

public class InvalidInputException extends GlobalException {

    public InvalidInputException(String message) {
        super(ErrorCode.INVALID_INPUT, message);
    }

    public InvalidInputException() {
        super(ErrorCode.INVALID_INPUT);
    }
}
