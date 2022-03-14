package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.log.repository.LogRepository;
import org.junit.jupiter.api.BeforeEach;
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
class CareerLogServiceTest extends AbstractTest {

    @Autowired
    LogRepository logRepository;
    @Autowired
    CareerLogService careerLogService;

    @BeforeEach
    void before() {
        logRepository.deleteAll();
    }

    @Test
    void insert_content() throws NoSuchFieldException, IllegalAccessException {
        // [Career] 직업 : -, 직장명 : -, 그 외 경력 : -, 자격증 : -
        // given
        Career career = Career.builder()
                .mentor(mock(Mentor.class))
                .job("jobA")
                .companyName("companyNameA")
                .license("licenseA")
                .others("othersA")
                .build();
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        careerLogService.insert(pw, career);
        // then
        assertEquals(String.format("[Career] 직업 : %s, 직장명 : %s, 그 외 경력 : %s, 자격증 : %s",
                career.getJob(), career.getCompanyName(), career.getOthers(), career.getLicense()), sw.toString());
    }

    @WithAccount(NAME)
    @Test
    void insert() {
        // given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // when
        careerService.createCareer(user, careerCreateRequest);
        // then
        assertEquals(logRepository.count(), 1L);
        logRepository.findAll().stream().forEach(System.out::println);
    }

    @Test
    void update_content() throws NoSuchFieldException, IllegalAccessException {
        // [Career] 직업 : {} → {}, 직장명 : {} → {}, 그 외 경력 : {} → {}, 자격증 : {} → {}
        // given
        Career before = Career.builder()
                .mentor(mock(Mentor.class))
                .job("jobA")
                .companyName("companyNameA")
                .license("licenseA")
                .others("othersA")
                .build();
        Career after = Career.builder()
                .mentor(mock(Mentor.class))
                .job("jobB")
                .companyName("companyNameB")
                .license("licenseB")
                .others("othersB")
                .build();
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        careerLogService.update(pw, before, after);
        // then
        assertEquals(String.format("[Career] 직업 : %s → %s, 직장명 : %s → %s, 그 외 경력 : %s → %s, 자격증 : %s → %s",
                before.getJob(), after.getJob(),
                before.getCompanyName(), after.getCompanyName(),
                before.getOthers(), after.getOthers(),
                before.getLicense(), after.getLicense()),
                sw.toString());
    }

    @WithAccount(NAME)
    @Test
    void update() {

        // given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        Career career = careerService.createCareer(user, careerCreateRequest);
        Long careerId = career.getId();

        // when
        careerService.updateCareer(user, careerId, careerUpdateRequest);
        // then
        logRepository.findAll().stream().forEach(System.out::println);
        assertEquals(logRepository.count(), 2L);
    }

    @Test
    void delete_content() throws NoSuchFieldException, IllegalAccessException {
        // [Career] 직업 : {}, 직장명 : {}, 그 외 경력 : {}, 자격증 : {}

        // given
        Career career = Career.builder()
                .mentor(mock(Mentor.class))
                .job("jobA")
                .companyName("companyNameA")
                .license("licenseA")
                .others("othersA")
                .build();
        // when
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);

        careerLogService.delete(pw, career);
        // then
        assertEquals(String.format("[Career] 직업 : %s, 직장명 : %s, 그 외 경력 : %s, 자격증 : %s",
                career.getJob(), career.getCompanyName(), career.getOthers(), career.getLicense()), sw.toString());
    }

    @WithAccount(NAME)
    @Test
    void delete() {

        // given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        Career career = careerService.createCareer(user, careerCreateRequest);
        Long careerId = career.getId();

        // when
        careerService.deleteCareer(user, careerId);
        // then
        logRepository.findAll().stream().forEach(System.out::println);
        assertEquals(logRepository.count(), 2L);
    }
}