package com.project.mentoridge.modules.chat.controller.response;

import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import lombok.Data;

@Data
public class ChatroomResponse {

    // TODO - 쿼리
    // TODO - FETCH JOIN
    public ChatroomResponse(Chatroom chatroom) {
        this.chatroomId = chatroom.getId();
        this.mentorId = chatroom.getMentor().getUser().getId();
        // this.mentorUsername = chatroom.getMentor().getUser().getUsername();
        this.mentorNickname = chatroom.getMentor().getUser().getNickname();
        this.mentorImage = chatroom.getMentor().getUser().getImage();
        this.menteeId = chatroom.getMentee().getUser().getId();
        // this.menteeUsername = chatroom.getMentee().getUser().getUsername();
        this.menteeNickname = chatroom.getMentee().getUser().getNickname();
        this.menteeImage = chatroom.getMentee().getUser().getImage();
    }

    private Long chatroomId;
    private Long mentorId;
    // private String mentorUsername;
    private String mentorNickname;
    private String mentorImage;
    private Long menteeId;
    // private String menteeUsername;
    private String menteeNickname;
    private String menteeImage;
    private Message lastMessage;
    private Integer uncheckedMessageCount = 0;
}
