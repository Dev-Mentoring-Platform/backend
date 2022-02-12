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

@Api(tags = {"MentorChatroomController"})
@RequestMapping("/api/mentors/my-chatrooms")
@RestController
@RequiredArgsConstructor
public class MentorChatroomController {

    private final ChatroomService chatroomService;

    @ApiOperation("채팅방 전체 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getChatrooms(@CurrentUser User user,
                                          @RequestParam(defaultValue = "1") Integer page) {

        Page<ChatroomResponse> chatrooms = chatroomService.getChatroomResponsesOfMentor(user, page);
        return ResponseEntity.ok(chatrooms);
    }

    // TODO - /{chatroom_id}/messages
    @ApiOperation("채팅방 개별 조회")
    @GetMapping("/{chatroom_id}")
    public ResponseEntity<?> getMessagesOfChatroom(@CurrentUser User user,
                                                   @PathVariable(name = "chatroom_id") Long chatroomId) {

        List<Message> messages = chatroomService.getMessagesOfMentorChatroom(user, chatroomId);
        return ResponseEntity.ok(messages);
    }
}
