package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ServiceTest
class MentorLogServiceTest {

    @Autowired
    MentorLogService mentorLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = User.builder()
                .username("username")
                .name("name")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nickname")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(user)
                .bio("bio")
                .build();

        // when
        String log = mentorLogService.insert(user, mentor);
        // then
        assertEquals(String.format("[Mentor] 사용자 : %s, 소개 : %s", mentor.getUser().getUsername(), mentor.getBio()), log);
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = User.builder()
                .username("username")
                .name("name")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nickname")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor before = Mentor.builder()
                .user(user)
                .bio("bioA")
                .build();
        Mentor after = Mentor.builder()
                .user(user)
                .bio("bioB")
                .build();

        // when
        String log = mentorLogService.update(user, before, after);
        // then
        assertEquals(String.format("[Mentor] 소개 : %s → %s", before.getBio(), after.getBio()), log);
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = User.builder()
                .username("username")
                .name("name")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nickname")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentor mentor = Mentor.builder()
                .user(user)
                .bio("bio")
                .build();

        // when
        String log = mentorLogService.delete(user, mentor);
        // then
        assertEquals(String.format("[Mentor] 사용자 : %s, 소개 : %s", mentor.getUser().getUsername(), mentor.getBio()), log);
    }
}