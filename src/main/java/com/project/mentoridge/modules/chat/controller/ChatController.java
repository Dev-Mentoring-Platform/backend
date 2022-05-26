package com.project.mentoridge.modules.chat.controller;

import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.chat.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"ChatController"})
@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Websocket으로 들어오는 메시지 발행 처리
    // 클라이언트에서 /pub/chat으로 발행 요청
    @MessageMapping("/chat")
    public ResponseEntity<?> chat(ChatMessage chatMessage) {

        // 메시지 저장
        chatService.sendMessage(chatMessage);
        return ok();
    }

    /*
    (A 기준)
    * 모든 api 호출 시 header에 토큰 필수

    1. A(나)가 채팅방에 들어옴
    - /api/chat/rooms/{chatroom_id}/enter 호출 -> A가 받은 메시지 전부 읽음 처리, 즉 checked를 true로 변경하고, DB에 A를 in으로 변경
    - /api/chat/rooms/{chatroom_id}/messages 호출해서 채팅방에 출력

    2. B(상대방)가 아직 들어오지 않은 상태에서 A(나)가 채팅을 보냄
    - /pub/chat으로 채팅 발행 -> DB에서 B가 in인지 out인지 판단, 현재 B가 out이므로 message의 checked를 false로 저장
    - 동시에 checked가 false인 메세지를 프론트로 전달하면 프론트에서 true/false만 확인하고 안읽음 처리

    3. B(상대방)가 채팅방을 들어옴
    - (B 입장에서) 1번 실행

    4. B(상대방)가 들어온 상태에서 A(나)가 채팅을 보냄
    - /pub/chat으로 채팅 발행 -> DB에서 B가 in인지 out인지 판단, 현재 B가 in이므로 message의 checked를 true로 저장
    - 동시에 checked가 true인 메세지를 프론트로 전달하면 프론트에서 true/false만 확인하고 읽음 처리

    5. A(나)가 채팅방을 나감
    - /api/chat/rooms/{chatroom_id}/out 호출 -> DB에 A를 out으로 변경
    */

    @ApiOperation("멘토가 멘티에게 채팅 신청")
    @PostMapping("/api/chat/mentor/me/mentee/{mentee_id}")
    public ResponseEntity<?> newChatroomByMentor(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable(name = "mentee_id") Long menteeId) {
        return ResponseEntity.ok(chatService.createChatroomByMentor(principalDetails, menteeId));
    }

    @ApiOperation("멘티가 멘토에게 채팅 신청")
    @PostMapping("/api/chat/mentee/me/mentor/{mentor_id}")
    public ResponseEntity<?> newChatroomMyMentee(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable(name = "mentor_id") Long mentorId) {
        return ResponseEntity.ok(chatService.createChatroomByMentee(principalDetails, mentorId));
    }

}
