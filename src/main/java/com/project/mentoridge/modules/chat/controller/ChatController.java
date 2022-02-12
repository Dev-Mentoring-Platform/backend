package com.project.mentoridge.modules.chat.controller;

import com.project.mentoridge.modules.chat.service.MessageService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"ChatController"})
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final MessageService messageService;

//    @ApiOperation("채팅 메시지 리스트")
//    @GetMapping("/messages")
//    public ResponseEntity<?> getMessages() {
//
//        List<Message> messages = messageService.getMessages();
//        return ResponseEntity.ok(messages);
//    }

}
