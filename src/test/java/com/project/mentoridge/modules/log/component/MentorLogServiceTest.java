package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

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
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        mentorLogService.insert(pw, mentor);
        // then
        assertEquals(String.format("[Mentor] 사용자 : %s, 소개 : %s", mentor.getUser().getUsername(), mentor.getBio()), sw.toString());
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
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        mentorLogService.update(pw, before, after);
        // then
        System.out.println(sw.toString());
        assertEquals(String.format("[Mentor] 소개 : %s → %s", before.getBio(), after.getBio()), sw.toString());
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
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        mentorLogService.delete(pw, mentor);
        // then
        assertEquals(String.format("[Mentor] 사용자 : %s, 소개 : %s", mentor.getUser().getUsername(), mentor.getBio()), sw.toString());
    }
}