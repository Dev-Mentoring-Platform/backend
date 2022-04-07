package com.project.mentoridge.config.exception;

import com.project.mentoridge.config.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class EntityNotFoundException extends GlobalException {

    public EntityNotFoundException(String message) {
        super(ErrorCode.ENTITY_NOT_FOUND, message);
    }

    public EntityNotFoundException() {
        super(ErrorCode.ENTITY_NOT_FOUND);
    }

    public EntityNotFoundException(EntityType entityType) {
        super(ErrorCode.ENTITY_NOT_FOUND, entityType.getMessage());
    }

    @Getter
    @AllArgsConstructor
    public enum EntityType {

        USER("존재하지 않는 사용자입니다."),
        NOTIFICATION("존재하지 않는 알림입니다."),
        MENTEE("존재하지 않는 멘티입니다."),
        MENTOR("존재하지 않는 멘토입니다."),
        CAREER("존재하지 않는 데이터입니다."),
        EDUCATION("존재하지 않는 데이터입니다."),
        SUBJECT("존재하지 않는 과목입니다."),
        LECTURE("존재하지 않는 강의입니다."),
        LECTURE_PRICE("존재하지 않는 데이터입니다."),
        PICK("존재하지 않는 내역입니다."),
        ENROLLMENT("수강 내역이 존재하지 않습니다."),
        CANCELLATION("취소 내역이 존재하지 않습니다."),
        REVIEW("존재하지 않는 리뷰입니다."),
        CHATROOM("존재하지 않는 채팅방입니다."),

        POST("존재하지 않는 게시글입니다."),
        COMMENT("존재하지 않는 댓글입니다.");

        private String message;
    }
}
