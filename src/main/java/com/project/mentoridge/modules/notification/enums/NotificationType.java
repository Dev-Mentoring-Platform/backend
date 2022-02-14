package com.project.mentoridge.modules.notification.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationType {

    CHAT("CHAT", "채팅 메시지가 도착했습니다."),
    ENROLLMENT("ENROLLMENT", "멘토님의 강의가 수강되었습니다.");

    private String type;
    private String message;
}
