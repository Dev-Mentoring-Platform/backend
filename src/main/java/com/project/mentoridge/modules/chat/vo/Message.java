package com.project.mentoridge.modules.chat.vo;

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
@Getter
//@Setter
@Document(collection = "messages")
public class Message {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @Enumerated(EnumType.STRING)
    private MessageType type;
    private Long chatroomId;
    // private String sessionId;

    private Long senderId;
    private String senderNickname;
    private Long receiverId;
    private String receiverNickname;

    private String message;
    // TODO - CHECK : private LocalDateTime sentAt;
    private String sentAt;

    private boolean checked;

    @Builder(access = AccessLevel.PUBLIC)
    private Message(MessageType type, Long chatroomId, User sender, User receiver, String message, LocalDateTime sentAt, boolean checked) {
        this.type = type;
        this.chatroomId = chatroomId;

        this.senderId = sender.getId();
        this.senderNickname = sender.getNickname();
        this.receiverId = receiver.getId();
        this.receiverNickname = receiver.getNickname();
        this.message = message;
        this.sentAt = LocalDateTimeUtil.getDateTimeToString(sentAt);
        this.checked = checked;
    }

    public void check() {
        this.checked = true;
    }
}
