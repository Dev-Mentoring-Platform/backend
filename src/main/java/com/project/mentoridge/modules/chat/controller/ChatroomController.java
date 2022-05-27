package com.project.mentoridge.modules.chat.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"ChatroomController"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatroomController {
    // https://zaccoding.tistory.com/16
    // https://pearlluck.tistory.com/333
    private final ChatService chatService;

    @ApiOperation("내 채팅방 리스트")
    @GetMapping("/all")
    public ResponseEntity<?> getMyAllChatrooms(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return ResponseEntity.ok(chatService.getChatroomResponses(principalDetails));
    }

    @ApiOperation("내 채팅방 리스트 - 페이징")
    @GetMapping
    public ResponseEntity<?> getMyChatrooms(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam(defaultValue = "1") Integer page) {
        return ResponseEntity.ok(chatService.getChatroomResponses(principalDetails, page));
    }

    @ApiOperation("채팅방 입장 - 메시지 읽음 처리")
    @PutMapping("/{chatroom_id}/enter")
    public ResponseEntity<?> enter(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable(name = "chatroom_id") Long chatroomId) {
        chatService.enterChatroom(principalDetails, chatroomId);
        return ok();
    }

    @ApiOperation("채팅방 나가기")
    @PutMapping("/{chatroom_id}/out")
    public ResponseEntity<?> out(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable(name = "chatroom_id") Long chatroomId) {
        chatService.outChatroom(principalDetails, chatroomId);
        return ok();
    }

    // 지난 메시지 리스트
    @ApiOperation("메시지 조회")
    @GetMapping("/{chatroom_id}/messages")
    public ResponseEntity<?> getMessages(@CurrentUser User user, @PathVariable(name = "chatroom_id") Long chatroomId, @RequestParam(defaultValue = "1") Integer page) {
        Page<ChatMessage> messages = chatService.getChatMessagesOfChatroom(chatroomId, page);
        return ResponseEntity.ok(messages);
    }

    @ApiOperation("채팅방/상대 신고")
    @PutMapping("/{chatroom_id}/accuse")
    public ResponseEntity<?> accuse(@CurrentUser User user,
                                    @PathVariable(name = "chatroom_id") Long chatroomId) {
        chatService.accuseChatroom(user, chatroomId);
        return ok();
    }
}
