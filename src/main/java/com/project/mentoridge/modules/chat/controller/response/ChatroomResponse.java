package com.project.mentoridge.modules.chat.controller.response;

import com.project.mentoridge.modules.chat.controller.ChatMessage;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatroomResponse {

    public ChatroomResponse(Chatroom chatroom) {
        this.chatroomId = chatroom.getId();
        this.mentorId = chatroom.getMentor().getId();
        this.mentorUserId = chatroom.getMentor().getUser().getId();
        // this.mentorUsername = chatroom.getMentor().getUser().getUsername();
        this.mentorNickname = chatroom.getMentor().getUser().getNickname();
        this.mentorImage = chatroom.getMentor().getUser().getImage();
        this.menteeId = chatroom.getMentee().getId();
        this.menteeUserId = chatroom.getMentee().getUser().getId();
        // this.menteeUsername = chatroom.getMentee().getUser().getUsername();
        this.menteeNickname = chatroom.getMentee().getUser().getNickname();
        this.menteeImage = chatroom.getMentee().getUser().getImage();
    }

    private Long chatroomId;
    private Long mentorId;
    private Long mentorUserId;
    // private String mentorUsername;
    private String mentorNickname;
    private String mentorImage;
    private Long menteeId;
    private Long menteeUserId;
    // private String menteeUsername;
    private String menteeNickname;
    private String menteeImage;

    private ChatMessage lastMessage;
    private Long uncheckedMessageCount = null;
}
