package com.project.mentoridge.modules.chat.controller;

import com.project.mentoridge.modules.chat.vo.Message;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RestController;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"ChatController"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messageSendingTemplate;

    // Websocket으로 들어오는 메시지 발행 처리
    // 클라이언트에서 /pub/chat으로 발행 요청
    @MessageMapping("/chat")
    public ResponseEntity<?> chat(Message message) {
        // topic - /sub/chat/room/{chatroom_id}로 메시지 send
        // 클라이언트는 해당 주소를 구독하고 있다가 메시지가 전달되면 화면에 출력
        // WebSocketHandler 대체
        messageSendingTemplate.convertAndSend("/sub/chat/room/" + message.getChatroomId(), message);
        return ok();
    }
}
