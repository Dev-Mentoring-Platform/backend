package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.vo.Education;
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

@Transactional
@SpringBootTest
class InquiryLogServiceTest extends AbstractTest {

    @Autowired
    InquiryLogService inquiryLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {
        // [Inquiry] 유형 : -, 제목 : -, 내용 : -
        // given
        Inquiry inquiry = Inquiry.builder()
                .user(mock(User.class))
                .type(InquiryType.LECTURE)
                .title("titleA")
                .content("contentA")
                .build();

        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        inquiryLogService.insert(pw, inquiry);
        // then
        assertEquals(String.format("[Inquiry] 유형 : %s, 제목 : %s, 내용 : %s",
                inquiry.getType(), inquiry.getTitle(), inquiry.getContent()), sw.toString());
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {
        // [Inquiry] 유형 : {} → {}, 제목 : {} → {}, 내용 : {} → {}
        // given
        Inquiry before = Inquiry.builder()
                .user(mock(User.class))
                .type(InquiryType.LECTURE)
                .title("titleA")
                .content("contentA")
                .build();
        Inquiry after = Inquiry.builder()
                .user(mock(User.class))
                .type(InquiryType.MENTOR)
                .title("titleB")
                .content("contentB")
                .build();
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        inquiryLogService.update(pw, before, after);
        // then
        assertEquals(String.format("[Inquiry] 유형 : %s → %s, 제목 : %s → %s, 내용 : %s → %s",
                before.getType(), after.getType(),
                before.getTitle(), after.getTitle(),
                before.getContent(), after.getContent()),
                sw.toString());
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {
        // [Inquiry] 유형 : %s, 제목 : %s, 내용 : %s
        // given
        Inquiry inquiry = Inquiry.builder()
                .user(mock(User.class))
                .type(InquiryType.LECTURE)
                .title("titleA")
                .content("contentA")
                .build();
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        inquiryLogService.delete(pw, inquiry);
        // then
        assertEquals(String.format("[Inquiry] 유형 : %s, 제목 : %s, 내용 : %s",
                inquiry.getType(), inquiry.getTitle(), inquiry.getContent()), sw.toString());
    }

}