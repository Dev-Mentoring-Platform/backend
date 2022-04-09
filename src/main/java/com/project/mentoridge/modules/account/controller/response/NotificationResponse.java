package com.project.mentoridge.modules.account.controller.response;

import com.project.mentoridge.modules.notification.vo.Notification;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import lombok.Data;

@Data
public class NotificationResponse {

    public NotificationResponse(Notification notification) {
        this.notificationId = notification.getId();
        // this.username = notification.getUser().getUsername();
        this.content = notification.getContent();
        this.checked = notification.isChecked();
        this.createdAt = LocalDateTimeUtil.getDateTimeToString(notification.getCreatedAt());
        this.checkedAt = LocalDateTimeUtil.getDateTimeToString(notification.getCheckedAt());
    }

    private Long notificationId;
    // private String username;    // 수신인
    private String content;
    private boolean checked;
    private String createdAt;
    private String checkedAt;
}
