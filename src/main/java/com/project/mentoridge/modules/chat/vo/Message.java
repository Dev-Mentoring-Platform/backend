package com.project.mentoridge.modules.chat.vo;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.chat.enums.MessageType;
import lombok.*;

import javax.persistence.*;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "message_id"))
@Entity
public class Message extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id",
            referencedColumnName = "chatroom_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MESSAGE_CHATROOM_ID"))
    private Chatroom chatroom;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id",
            referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_MESSAGE_USER_ID"))
    private User sender;

    // private String message;
    private String text;
    private boolean checked = false;

    @Builder(access = AccessLevel.PUBLIC)
    private Message(MessageType type, Chatroom chatroom, User sender, String text, boolean checked) {
        this.type = type;
        this.chatroom = chatroom;
        this.sender = sender;
        this.text = text;
        this.checked = checked;
    }
/*
    public void check() {
        this.checked = true;
    }*/

    public Long getSenderId() {
        return this.sender.getId();
    }

    public Long getChatroomId() {
        return this.chatroom.getId();
    }
}
