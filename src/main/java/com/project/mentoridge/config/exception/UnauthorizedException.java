package com.project.mentoridge.config.exception;

import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.modules.account.enums.RoleType;

public class UnauthorizedException extends GlobalException {

    public UnauthorizedException(String message) {
        super(ErrorCode.UNAUTHORIZED, message);
    }

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED, "권한이 없습니다.");
    }

    public UnauthorizedException(RoleType roleType) {
        super(ErrorCode.UNAUTHORIZED, "해당 사용자는 " + roleType.getName() + "가 아닙니다.");
    }

}
