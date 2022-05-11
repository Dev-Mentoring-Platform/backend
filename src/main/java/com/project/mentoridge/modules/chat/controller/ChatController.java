package com.project.mentoridge.modules.chat.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"ChatController"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations messageSendingTemplate;
    private final ChatService chatService;

    // Websocket으로 들어오는 메시지 발행 처리
    // 클라이언트에서 /pub/chat으로 발행 요청
    @MessageMapping("/chat")
    public ResponseEntity<?> chat(ChatMessage chatMessage) {
        // topic - /sub/chat/room/{chatroom_id}로 메시지 send
        // 클라이언트는 해당 주소를 구독하고 있다가 메시지가 전달되면 화면에 출력
        // WebSocketHandler 대체
        messageSendingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getChatroomId(), chatMessage);
        // 메시지 저장
        chatService.sendMessage(chatMessage);
        return ok();
    }

    @ApiOperation("멘토가 멘티에게 채팅 신청")
    @PostMapping("/api/chat/mentor/me/mentee/{mentee_id}")
    public ResponseEntity<?> newChatroomByMentor(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable(name = "mentee_id") Long menteeId) {
        chatService.createChatroomByMentor(principalDetails, menteeId);
        return ok();
    }

    @ApiOperation("멘티가 멘토에게 채팅 신청")
    @PostMapping("/api/chat/mentee/me/mentor/{mentor_id}")
    public ResponseEntity<?> newChatroomMyMentee(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable(name = "mentor_id") Long mentorId) {
        chatService.createChatroomByMentee(principalDetails, mentorId);
        return ok();
    }

}
