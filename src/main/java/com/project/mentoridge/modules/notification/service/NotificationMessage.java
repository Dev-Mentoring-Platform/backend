package com.project.mentoridge.modules.notification.service;

import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.vo.Notification;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationMessage {

    private Long userId;
    private String username;
    private NotificationType type;
    private String content;

    public NotificationMessage(Notification notification) {
        this.userId = notification.getUser().getId();
        this.username = notification.getUser().getUsername();
        this.type = notification.getType();
        this.content = notification.getContent();
    }
}
