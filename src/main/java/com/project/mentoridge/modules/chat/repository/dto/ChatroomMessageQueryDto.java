package com.project.mentoridge.modules.chat.repository.dto;

import lombok.Data;

@Data
public class ChatroomMessageQueryDto {

    private Long chatroomId;
    private Long uncheckedMessageCount;

    public ChatroomMessageQueryDto(Long chatroomId, Long uncheckedMessageCount) {
        this.chatroomId = chatroomId;
        this.uncheckedMessageCount = uncheckedMessageCount;
    }
}
