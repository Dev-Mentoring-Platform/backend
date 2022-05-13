package com.project.mentoridge.modules.chat.service;

import com.project.mentoridge.modules.chat.repository._MessageRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Disabled
@Transactional
@SpringBootTest
class ChatServiceIntegrationTest {

    @Autowired
    ChatService chatService;
    @Autowired
    _MessageRepository messageRepository;

    @Test
    void saveMessage() {

        // given
        // when
        // then
//        Message message = Message.builder()
//                .type(MessageType.MESSAGE)
//                .chatroomId(1L)
//                .sessionId("4253d14c-c3d5-ef9d-22cb-8823c7632c24")
//                .senderNickname("user1")
//                .receiverId(1L)
//                .message("hi")
//                .sentAt(LocalDateTime.now())
//                .checked(true)
//                .build();
//        chatService.saveMessage(message);
//        System.out.println(messageRepository.findAll());
    }
}