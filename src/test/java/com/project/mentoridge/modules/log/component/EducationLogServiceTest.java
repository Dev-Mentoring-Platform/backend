package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        User user = mock(User.class);
        Education education = Education.builder()
                .mentor(mock(Mentor.class))
                .educationLevel(EducationLevelType.UNIVERSITY)
                .schoolName("schoolNameA")
                .major("majorA")
                .others("othersA")
                .build();
        // when
        String log = educationLogService.insert(user, education);
        // then
        assertEquals(String.format("[Education] 최종학력 : %s, 학교명 : %s, 전공 : %s, 그 외 학력 : %s",
                education.getEducationLevel(), education.getSchoolName(), education.getMajor(), education.getOthers()), log);
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {
        // [Education] 최종학력 : {} → {}, 학교명 : {} → {}, 전공 : {} → {}, 그 외 학력 : {} → {}
        // given
        User user = mock(User.class);
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
        String log = educationLogService.update(user, before, after);
        // then
        assertEquals(String.format("[Education] 최종학력 : %s → %s, 학교명 : %s → %s, 전공 : %s → %s, 그 외 학력 : %s → %s",
                before.getEducationLevel(), after.getEducationLevel(),
                before.getSchoolName(), after.getSchoolName(),
                before.getMajor(), after.getMajor(),
                before.getOthers(), after.getOthers()),
                log);
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {
        // [Education] 최종학력 : -, 학교명 : -, 전공 : -, 그 외 학력 : -
        // given
        User user = mock(User.class);
        Education education = Education.builder()
                .mentor(mock(Mentor.class))
                .educationLevel(EducationLevelType.UNIVERSITY)
                .schoolName("schoolNameA")
                .major("majorA")
                .others("othersA")
                .build();
        // when
        String log = educationLogService.delete(user, education);
        // then
        assertEquals(String.format("[Education] 최종학력 : %s, 학교명 : %s, 전공 : %s, 그 외 학력 : %s",
                education.getEducationLevel(), education.getSchoolName(), education.getMajor(), education.getOthers()), log);
    }

}