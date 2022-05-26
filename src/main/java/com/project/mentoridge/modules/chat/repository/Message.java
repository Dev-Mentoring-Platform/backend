package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.enums.MessageType;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.time.LocalDateTime;

@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter //@Setter
@Document(collection = "messages")
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Enumerated(EnumType.STRING)
    private MessageType type;
    private Long chatroomId;
    // private String sessionId;

    private Long senderId;
    // private String senderNickname;
    private Long receiverId;
    // private String receiverNickname;

    private String text;
    // TODO - CHECK : private LocalDateTime sentAt;
    private String sentAt = LocalDateTimeUtil.getDateTimeToString(LocalDateTime.now());


    // @Builder(access = AccessLevel.PUBLIC)
    private Message(MessageType type, Long chatroomId, User sender, User receiver, String text, LocalDateTime sentAt) {
        this.type = type;
        this.chatroomId = chatroomId;
        this.senderId = sender.getId();
        // this.senderNickname = sender.getNickname();
        this.receiverId = receiver.getId();
        // this.receiverNickname = receiver.getNickname();
        this.text = text;
        this.sentAt = LocalDateTimeUtil.getDateTimeToString(sentAt);
    }

    @Builder(access = AccessLevel.PUBLIC)
    private Message(MessageType type, Long chatroomId, Long senderId, Long receiverId, String text, String sentAt) {
        this.type = type;
        this.chatroomId = chatroomId;
        this.senderId = senderId;
        // this.senderNickname = senderNickname;
        this.receiverId = receiverId;
        // this.receiverNickname = receiverNickname;
        this.text = text;
        this.sentAt = sentAt;
    }
}
