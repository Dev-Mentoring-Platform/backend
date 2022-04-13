package com.project.mentoridge.config.exception;

import com.project.mentoridge.config.response.ErrorCode;

public class AlreadyExistException extends GlobalException {

    public static final String ID = "동일한 ID가 존재합니다.";
    public static final String NICKNAME = "동일한 닉네임이 존재합니다.";
    public static final String ENROLLMENT = "동일한 수강내역이 존재합니다.";
    public static final String PICK = "동일한 내역이 존재합니다.";
    public static final String MENTOR = "이미 등록된 멘토입니다.";

    public AlreadyExistException(String message) {
        super(ErrorCode.ALREADY_EXIST, message);
    }

    public AlreadyExistException() {
        super(ErrorCode.ALREADY_EXIST);
    }
}
