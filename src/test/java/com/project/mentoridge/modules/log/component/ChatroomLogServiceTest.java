package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class ChatroomLogServiceTest extends AbstractTest {

    @Autowired
    ChatroomLogService chatroomLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User userA = User.builder()
                .username("usernameA")
                .name("nameA")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nicknameA")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(userA)
                .bio("bio")
                .build();
        User userB = User.builder()
                .username("usernameB")
                .name("nameB")
                .gender(GenderType.FEMALE)
                .birthYear("20220319")
                .phoneNumber("01012345679")
                .nickname("nicknameB")
                .image(null)
                .zone("서울특별시 강남구 압구정동")
                .build();
        Mentee mentee = Mentee.builder()
                .user(userB)
                .subjects("subjects")
                .build();
        Chatroom chatroom = Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .build();

        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        chatroomLogService.insert(pw, chatroom);
        // then
        assertEquals(String.format("[Chatroom] 멘토 : %s, 멘티 : %s", chatroom.getMentor().getUser().getUsername(), chatroom.getMentee().getUser().getUsername()), sw.toString());
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User userA = User.builder()
                .username("usernameA")
                .name("nameA")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nicknameA")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(userA)
                .bio("bio")
                .build();
        User userB = User.builder()
                .username("usernameB")
                .name("nameB")
                .gender(GenderType.FEMALE)
                .birthYear("20220319")
                .phoneNumber("01012345679")
                .nickname("nicknameB")
                .image(null)
                .zone("서울특별시 강남구 압구정동")
                .build();
        Mentee mentee = Mentee.builder()
                .user(userB)
                .subjects("subjects")
                .build();
        Chatroom chatroom = Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .build();

        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        chatroomLogService.delete(pw, chatroom);
        // then
        assertEquals(String.format("[Chatroom] 멘토 : %s, 멘티 : %s", chatroom.getMentor().getUser().getUsername(), chatroom.getMentee().getUser().getUsername()), sw.toString());
    }

}