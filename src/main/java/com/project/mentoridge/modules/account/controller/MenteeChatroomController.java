package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.CurrentUser;
import com.project.mentoridge.modules.account.service.MenteeChatroomService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.controller.response.ChatroomResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"MenteeChatroomController"})
@RequestMapping("/api/mentees/my-chatrooms")
@RestController
@RequiredArgsConstructor
public class MenteeChatroomController {

    private final MenteeChatroomService menteeChatroomService;

    @ApiOperation("채팅방 전체 조회 - 페이징")
    @GetMapping
    public ResponseEntity<?> getChatrooms(@CurrentUser User user,
                                          @RequestParam(defaultValue = "1") Integer page) {
        Page<ChatroomResponse> chatrooms = menteeChatroomService.getChatroomResponsesOfMentee(user, page);
        return ResponseEntity.ok(chatrooms);
    }
}
