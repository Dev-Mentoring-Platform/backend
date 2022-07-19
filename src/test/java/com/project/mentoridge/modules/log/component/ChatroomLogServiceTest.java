package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ServiceTest
class ChatroomLogServiceTest {

    @Autowired
    ChatroomLogService chatroomLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User mentorUser = User.builder()
                .username("mentorUser@email.com")
                .name("mentorUser")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("mentorUser")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .bio("bio")
                .build();
        User menteeUser = User.builder()
                .username("menteeUser@email.com")
                .name("menteeUser")
                .gender(GenderType.FEMALE)
                .birthYear("20220319")
                .phoneNumber("01012345679")
                .nickname("menteeUser")
                .image(null)
                .zone("서울특별시 강남구 압구정동")
                .build();
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .subjects("subjects")
                .build();
        Chatroom chatroom = Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .build();

        // when
        String log = chatroomLogService.insert(menteeUser, chatroom);
        // then
        assertEquals(String.format("[Chatroom] 멘토 : %s, 멘티 : %s",
                chatroom.getMentor().getUser().getUsername(), chatroom.getMentee().getUser().getUsername()), log);
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User mentorUser = User.builder()
                .username("mentorUser@email.com")
                .name("mentorUser")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("mentorUser")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .bio("bio")
                .build();
        User menteeUser = User.builder()
                .username("menteeUser@email.com")
                .name("menteeUser")
                .gender(GenderType.FEMALE)
                .birthYear("20220319")
                .phoneNumber("01012345679")
                .nickname("menteeUser")
                .image(null)
                .zone("서울특별시 강남구 압구정동")
                .build();
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .subjects("subjects")
                .build();
        Chatroom chatroom = Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .build();

        // when
        String log = chatroomLogService.delete(menteeUser, chatroom);
        // then
        assertEquals(String.format("[Chatroom] 멘토 : %s, 멘티 : %s",
                chatroom.getMentor().getUser().getUsername(), chatroom.getMentee().getUser().getUsername()), log);
    }

}