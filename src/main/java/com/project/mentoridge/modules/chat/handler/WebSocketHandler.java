package com.project.mentoridge.modules.chat.handler;

import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.firebase.service.AndroidPushNotificationsService;
import com.project.mentoridge.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
// @Component
public class WebSocketHandler extends TextWebSocketHandler {

    // TODO - CHECK
    private static final String TYPE = "type";
    private static final String SESSION_ID = "sessionId";
    private static final String CHATROOM_ID = "chatroomId";
    private static final String SENDER_NICKNAME = "senderNickname";
    private static final String RECEIVER_ID = "receiverId";
    private static final String MESSAGE = "message";

    private final ChatroomRepository chatroomRepository;
    private final ChatService chatService;

    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final AndroidPushNotificationsService androidPushNotificationsService;

    // 소켓 연결
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
/*
        String uri = session.getUri().toString();

        // TODO - or getQuery : ?chatroomId=1
        Long chatroomId = Long.valueOf(uri.split("/chat/")[1]);
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));
        chatroom.enter(session);
        super.afterConnectionEstablished(session);

        JSONObject object = new JSONObject();
        object.put(TYPE, SESSION_ID);
        object.put(SESSION_ID, session.getId());
        log.info(object.toJSONString());
        session.sendMessage(new TextMessage(object.toJSONString()));*/
    }

    // 메세지 발송
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
/*
        String text = message.getPayload();

        JSONObject object = JsonUtil.parse(text);
        log.info(object.toJSONString());

        if (object.get(CHATROOM_ID) != null) {
            Long chatroomId = (Long) object.get(CHATROOM_ID);
            Chatroom chatroom = chatroomRepository.findById(chatroomId)
                    .orElseThrow(() -> new EntityNotFoundException(CHATROOM));

            // 해당 방의 세션에만 메세지 발송
            TextMessage textMessage = new TextMessage(object.toJSONString());
            chatroom.sendMessage(textMessage, chatService);

            Long receiverId = (Long) object.get(RECEIVER_ID);
            User receiver = userRepository.findById(receiverId)
                    .orElseThrow(() -> new EntityNotFoundException(USER));
            // TODO - CHECK : 웹소켓 세션 - 영속성 컨텍스트
            // TODO - CHECK : 효율성 체크
            if (sessionMap.size() != 2) {
                notificationService.createNotification(receiverId, NotificationType.CHAT);
            }
            String sender = (String) object.get(SENDER_NICKNAME);
            String messageText = (String) object.get(MESSAGE);
            Message msg = Message.builder()
                    .type(MessageType.MESSAGE)
                    .chatroomId(chatroomId)
                    .sessionId(session.getId())
                    .senderNickname(sender)
                    .receiverId(receiverId)
                    .message(messageText)
                    .sentAt(LocalDateTime.now())
                    .checked(sessionMap.size() == 2)
                    .build();
            chatService.saveMessage(msg);
            // androidPushNotificationsService.send(receiver.getFcmToken(), sender + "님으로부터 채팅이 도착했습니다", messageText);
        }*/
    }

    // 소켓 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
    }
}
