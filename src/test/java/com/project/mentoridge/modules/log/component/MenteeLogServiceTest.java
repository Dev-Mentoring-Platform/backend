package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class MenteeLogServiceTest extends AbstractTest {

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
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        menteeLogService.insert(pw, mentee);
        // then
        assertEquals(String.format("[Mentee] 사용자 : %s, 관심 주제 : %s", mentee.getUser().getUsername(), mentee.getSubjects()), sw.toString());
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
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        menteeLogService.update(pw, before, after);
        // then
        System.out.println(sw.toString());
        assertEquals(String.format("[Mentee] 관심 주제 : %s → %s", before.getSubjects(), after.getSubjects()), sw.toString());
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
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        menteeLogService.delete(pw, mentee);
        // then
        assertEquals(String.format("[Mentee] 사용자 : %s, 관심 주제 : %s", mentee.getUser().getUsername(), mentee.getSubjects()), sw.toString());
    }
}