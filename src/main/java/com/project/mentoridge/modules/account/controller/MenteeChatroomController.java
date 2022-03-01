package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.controller.response.ChatroomResponse;
import com.project.mentoridge.modules.chat.service.ChatroomService;
import com.project.mentoridge.modules.chat.vo.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = {"MenteeChatroomController"})
@RequestMapping("/api/mentees/my-chatrooms")
@RestController
@RequiredArgsConstructor
public class MenteeChatroomController {

    private final ChatroomService chatroomService;

    @ApiOperation("채팅방 전체 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getChatrooms(@CurrentUser User user,
                                          @RequestParam(defaultValue = "1") Integer page) {

        Page<ChatroomResponse> chatrooms = chatroomService.getChatroomResponsesOfMentee(user, page);
        return ResponseEntity.ok(chatrooms);
    }

    @ApiOperation("채팅방 메시지 조회")
    @GetMapping("/{chatroom_id}/messages")
    public ResponseEntity<?> getMessagesOfChatroom(@CurrentUser User user,
                                                   @PathVariable(name = "chatroom_id") Long chatroomId) {
        // TODO - MessageResponse
        List<Message> messages = chatroomService.getMessagesOfMenteeChatroom(user, chatroomId);
        return ResponseEntity.ok(messages);
    }
}
