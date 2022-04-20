package com.project.mentoridge.modules.notice.controller.response;

import com.project.mentoridge.modules.notice.vo.Notice;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import lombok.Data;

@Data
public class NoticeResponse {

    private String title;
    private String content;
    private String createdAt;

    public NoticeResponse(Notice notice) {
        this.title = notice.getTitle();
        this.content = notice.getContent();
        this.createdAt = LocalDateTimeUtil.getDateTimeToString(notice.getCreatedAt());
    }
}
