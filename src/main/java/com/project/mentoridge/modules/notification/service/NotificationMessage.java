package com.project.mentoridge.modules.notification.service;

import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.vo.Notification;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationMessage {

    private Long notificationId;
    private Long userId;
    private String username;
    private NotificationType type;
    private String content;
    private String createdAt;

    public NotificationMessage(Notification notification) {
        this.notificationId = notification.getId();
        this.userId = notification.getUser().getId();
        this.username = notification.getUser().getUsername();
        this.type = notification.getType();
        this.content = notification.getContent();
        this.createdAt = LocalDateTimeUtil.getDateTimeToString(notification.getCreatedAt());
    }
}
