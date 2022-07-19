package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ServiceTest
class MenteeLogServiceTest {

    @Autowired
    MenteeLogService menteeLogService;

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
        Mentee mentee = Mentee.builder()
                .user(user)
                .subjects("subjects")
                .build();

        // when
        String log = menteeLogService.insert(user, mentee);
        // then
        assertEquals(String.format("[Mentee] 사용자 : %s, 관심 주제 : %s", mentee.getUser().getUsername(), mentee.getSubjects()), log);
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {

        // given
        User user = User.builder()
                .username("usernameA")
                .name("nameA")
                .gender(GenderType.MALE)
                .birthYear("20220318")
                .phoneNumber("01012345678")
                .nickname("nicknameA")
                .image(null)
                .zone("서울특별시 강남구 청담동")
                .build();
        Mentee before = Mentee.builder()
                .user(user)
                .subjects("subjectsA")
                .build();
        Mentee after = Mentee.builder()
                .user(user)
                .subjects("subjectsB")
                .build();

        // when
        String log = menteeLogService.update(user, before, after);
        // then
        assertEquals(String.format("[Mentee] 관심 주제 : %s → %s", before.getSubjects(), after.getSubjects()), log);
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
        Mentee mentee = Mentee.builder()
                .user(user)
                .subjects("subjects")
                .build();

        // when
        String log = menteeLogService.delete(user, mentee);
        // then
        assertEquals(String.format("[Mentee] 사용자 : %s, 관심 주제 : %s", mentee.getUser().getUsername(), mentee.getSubjects()), log);
    }
}