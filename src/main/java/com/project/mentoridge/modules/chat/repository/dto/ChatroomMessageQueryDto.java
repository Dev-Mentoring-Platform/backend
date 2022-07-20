package com.project.mentoridge.modules.chat.repository.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatroomMessageQueryDto {

    private Long chatroomId;
    private Long uncheckedMessageCount;

    public ChatroomMessageQueryDto(Long chatroomId, Long uncheckedMessageCount) {
        this.chatroomId = chatroomId;
        this.uncheckedMessageCount = uncheckedMessageCount;
    }
}
