package com.project.mentoridge.config.response;

import lombok.Getter;

@Getter
public enum ErrorCode {

    ALREADY_EXIST(400, "이미 등록되었습니다."),
    ENTITY_NOT_FOUND(400, "Entity Not Found"),
    INVALID_INPUT(400, "Invalid Input"),
    UNAUTHORIZED(401, "Unauthorized"),
    UNAUTHENTICATED(401, "Unauthenticated"),

    TOKEN_EXPIRED(401, "Token Expired");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
