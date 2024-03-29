package com.project.mentoridge.modules.chat.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Disabled
//@ExtendWith(SpringExtension.class)
@DataMongoTest
class MessageRepositoryTest {

    @Autowired
    MessageMongoRepository messageRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    private String user1Nickname = "user1";
    private Long user1Id = 1L;
    private String user2Nickname = "user2";
    private Long user2Id = 2L;

    private String user3Nickname = "user3";
    private Long user3Id = 3L;
    private String user4Nickname = "user4";
    private Long user4Id = 4L;

//    @Test
//    void before() {
//        assertNotNull(messageRepository);
//        assertNotNull(mongoTemplate);
//    }

    @BeforeEach
    void init() {

        messageRepository.deleteAll();

//        Message message1 = Message.builder()
//                .type(MessageType.MESSAGE)
//                .chatroomId(1L)
//                .sessionId("session1")
//                .senderNickname(user1Nickname)
//                .receiverId(user2Id)
//                .message("hello! My name is user1")
//                .sentAt(LocalDateTime.now())
//                .checked(true)
//                .build();
//        messageRepository.save(message1);
//
//        Message message2 = Message.builder()
//                .type(MessageType.MESSAGE)
//                .chatroomId(1L)
//                .sessionId("session1")
//                .senderNickname(user1Nickname)
//                .receiverId(user2Id)
//                .message("hi~~~")
//                .sentAt(LocalDateTime.now())
//                .checked(false)
//                .build();
//        messageRepository.save(message2);
//
//        Message message3 = Message.builder()
//                .type(MessageType.MESSAGE)
//                .chatroomId(2L)
//                .sessionId("session3")
//                .senderNickname(user3Nickname)
//                .receiverId(user4Id)
//                .message("hello! My name is user3")
//                .sentAt(LocalDateTime.now())
//                .checked(true)
//                .build();
//        messageRepository.save(message3);
    }

    @Test
    void findAllByChatroomId() {

        // given
        // when
        List<Message> messages = messageRepository.findAllByChatroomId(1L);
        // then
        assertThat(messages.size()).isEqualTo(2);

        Message message = messages.get(0);
        assertAll(
                () -> assertThat(message).extracting("senderNickname").isEqualTo(user1Nickname)
        );
    }

    @Test
    void 마지막_메세지() {

        // given
        // when
        Message lastMessage = messageRepository.findFirstByChatroomIdOrderByIdDesc(1L);
        // then
        assertAll(
                () -> assertThat(lastMessage).extracting("senderNickname").isEqualTo(user1Nickname),
                () -> assertThat(lastMessage).extracting("message").isEqualTo("hi~~~"),
                () -> assertThat(lastMessage).extracting("checked").isEqualTo(false)
        );
    }
}