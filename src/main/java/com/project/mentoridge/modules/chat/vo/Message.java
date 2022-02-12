package com.project.mentoridge.modules.chat.vo;

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
@Getter
//@Setter
@Document(collection = "messages")
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Enumerated(EnumType.STRING)
    private MessageType type;
    private Long chatroomId;
    private String sessionId;
    private String senderNickname;
    private Long receiverId;
    private String message;
    // TODO - CHECK : private LocalDateTime sentAt;
    private String sentAt;

    private boolean checked;

    @Builder(access = AccessLevel.PRIVATE)
    private Message(MessageType type, Long chatroomId, String sessionId, String senderNickname, Long receiverId, String message, LocalDateTime sentAt, boolean checked) {
        this.type = type;
        this.chatroomId = chatroomId;
        this.sessionId = sessionId;
        this.senderNickname = senderNickname;
        this.receiverId = receiverId;
        this.message = message;
        this.sentAt = LocalDateTimeUtil.getDateTimeToString(sentAt);
        this.checked = checked;
    }

    public static Message of(MessageType type, Long chatroomId, String sessionId, String senderNickname, Long receiverId, String message, LocalDateTime sentAt, boolean checked) {
        return Message.builder()
                .type(type)
                .chatroomId(chatroomId)
                .sessionId(sessionId)
                .senderNickname(senderNickname)
                .receiverId(receiverId)
                .message(message)
                .sentAt(sentAt)
                .checked(checked)
                .build();
    }

    public void check() {
        this.checked = true;
    }
}
