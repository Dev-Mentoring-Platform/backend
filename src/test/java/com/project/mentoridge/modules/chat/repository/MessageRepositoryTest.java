package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.chat.enums.MessageType;
import com.project.mentoridge.modules.chat.vo.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

//@ExtendWith(SpringExtension.class)
@DataMongoTest
class MessageRepositoryTest {

    @Autowired
    MessageRepository messageRepository;

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

        Message message1 = Message.of(MessageType.MESSAGE, 1L, "session1", user1Nickname, user2Id,
                "hello! My name is user1", LocalDateTime.now(), true);
        messageRepository.save(message1);

        Message message2 = Message.of(MessageType.MESSAGE, 1L, "session1", user1Nickname, user2Id,
                "hi~~~", LocalDateTime.now(), false);
        messageRepository.save(message1);

        Message message3 = Message.of(MessageType.MESSAGE, 2L, "session3", user3Nickname, user4Id,
                "hello! My name is user3", LocalDateTime.now(), true);
        messageRepository.save(message2);
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

    @Test
    void 읽지_않은_메세지_개수() {

        // given
        // when
        // then
        assertAll(
                () -> assertThat(messageRepository.countAllByChatroomIdAndCheckedIsFalseAndReceiverId(1L, 2L)).isEqualTo(1),
                () -> assertThat(messageRepository.countAllByChatroomIdAndCheckedIsFalseAndReceiverId(2L, 4L)).isEqualTo(0)
        );
    }
}