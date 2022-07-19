package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.annotation.ServiceTest;
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
import static org.mockito.Mockito.when;

@ServiceTest
class InquiryLogServiceTest {

    @Autowired
    InquiryLogService inquiryLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {
        // [Inquiry] 유형 : -, 제목 : -, 내용 : -
        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .type(InquiryType.LECTURE)
                .title("titleA")
                .content("contentA")
                .build();

        // when
        String log = inquiryLogService.insert(user, inquiry);
        // then
        assertEquals(String.format("[Inquiry] 유형 : %s, 제목 : %s, 내용 : %s",
                inquiry.getType(), inquiry.getTitle(), inquiry.getContent()), log);
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {
        // [Inquiry] 유형 : {} → {}, 제목 : {} → {}, 내용 : {} → {}
        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        Inquiry before = Inquiry.builder()
                .user(user)
                .type(InquiryType.LECTURE)
                .title("titleA")
                .content("contentA")
                .build();
        Inquiry after = Inquiry.builder()
                .user(user)
                .type(InquiryType.MENTOR)
                .title("titleB")
                .content("contentB")
                .build();
        // when
        String log = inquiryLogService.update(user, before, after);
        // then
        assertEquals(String.format("[Inquiry] 유형 : %s → %s, 제목 : %s → %s, 내용 : %s → %s",
                before.getType(), after.getType(),
                before.getTitle(), after.getTitle(),
                before.getContent(), after.getContent()),
                log);
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {
        // [Inquiry] 유형 : %s, 제목 : %s, 내용 : %s
        // given
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user");
        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .type(InquiryType.LECTURE)
                .title("titleA")
                .content("contentA")
                .build();
        // when
        String log = inquiryLogService.delete(user, inquiry);
        // then
        assertEquals(String.format("[Inquiry] 유형 : %s, 제목 : %s, 내용 : %s",
                inquiry.getType(), inquiry.getTitle(), inquiry.getContent()), log);
    }

}