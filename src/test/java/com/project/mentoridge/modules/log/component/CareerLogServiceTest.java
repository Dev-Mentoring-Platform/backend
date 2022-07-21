package com.project.mentoridge.modules.log.component;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.CareerService;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.project.mentoridge.configuration.AbstractTest.careerCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.careerUpdateRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMentorUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.mock;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class CareerLogServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    CareerService careerService;
    @Autowired
    CareerLogService careerLogService;

    @Autowired
    LoginService loginService;
    @Autowired
    MentorRepository mentorRepository;

    private User mentorUser;
    private Mentor mentor;

    @BeforeAll
    void init() {

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
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

    @Test
    void insert() {

        // given
        // when
        // then
        careerService.createCareer(mentorUser, careerCreateRequest);
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

    @Test
    void update() {

        // given
        Career career = careerService.createCareer(mentorUser, careerCreateRequest);

        // when
        // then
        careerService.updateCareer(mentorUser, career.getId(), careerUpdateRequest);
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

    @Test
    void delete() {

        // given
        Career career = careerService.createCareer(mentorUser, careerCreateRequest);

        // when
        // then
        careerService.deleteCareer(mentorUser, career.getId());
    }
}