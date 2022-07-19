package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@ServiceTest
class EducationLogServiceTest {

    @Autowired
    EducationLogService educationLogService;

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {
        // [Education] 최종학력 : -, 학교명 : -, 전공 : -, 그 외 학력 : -
        // given
        Education education = Education.builder()
                .mentor(mock(Mentor.class))
                .educationLevel(EducationLevelType.UNIVERSITY)
                .schoolName("schoolNameA")
                .major("majorA")
                .others("othersA")
                .build();
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        educationLogService.insert(pw, education);
        // then
        assertEquals(String.format("[Education] 최종학력 : %s, 학교명 : %s, 전공 : %s, 그 외 학력 : %s",
                education.getEducationLevel(), education.getSchoolName(), education.getMajor(), education.getOthers()), sw.toString());
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {
        // [Education] 최종학력 : {} → {}, 학교명 : {} → {}, 전공 : {} → {}, 그 외 학력 : {} → {}
        // given
        Education before = Education.builder()
                .mentor(mock(Mentor.class))
                .educationLevel(EducationLevelType.UNIVERSITY)
                .schoolName("schoolNameA")
                .major("majorA")
                .others("othersA")
                .build();
        Education after = Education.builder()
                .mentor(mock(Mentor.class))
                .educationLevel(EducationLevelType.HIGH)
                .schoolName("schoolNameB")
                .major("majorB")
                .others("othersB")
                .build();
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        educationLogService.update(pw, before, after);
        // then
        assertEquals(String.format("[Education] 최종학력 : %s → %s, 학교명 : %s → %s, 전공 : %s → %s, 그 외 학력 : %s → %s",
                before.getEducationLevel(), after.getEducationLevel(),
                before.getSchoolName(), after.getSchoolName(),
                before.getMajor(), after.getMajor(),
                before.getOthers(), after.getOthers()),
                sw.toString());
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {
        // [Education] 최종학력 : -, 학교명 : -, 전공 : -, 그 외 학력 : -
        // given
        Education education = Education.builder()
                .mentor(mock(Mentor.class))
                .educationLevel(EducationLevelType.UNIVERSITY)
                .schoolName("schoolNameA")
                .major("majorA")
                .others("othersA")
                .build();
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        educationLogService.delete(pw, education);
        // then
        assertEquals(String.format("[Education] 최종학력 : %s, 학교명 : %s, 전공 : %s, 그 외 학력 : %s",
                education.getEducationLevel(), education.getSchoolName(), education.getMajor(), education.getOthers()), sw.toString());
    }

}