package com.project.mentoridge.modules.chat.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.service.ChatroomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.mentoridge.config.response.Response.ok;

@Api(tags = {"ChatroomController"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat/rooms")
public class ChatroomController {

    private final ChatroomService chatroomService;

    @ApiOperation("채팅방/상대 신고")
    @PutMapping("/{chatroom_id}/accuse")
    public ResponseEntity<?> accuse(@CurrentUser User user,
                                    @PathVariable(name = "chatroom_id") Long chatroomId) {
        chatroomService.accuse(user, chatroomId);
        return ok();
    }

}
