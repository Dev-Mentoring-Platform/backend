package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.repository.CareerRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class CareerServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MentorRepository mentorRepository;

    @Autowired
    LoginService loginService;
    @Autowired
    MentorService mentorService;
    @Autowired
    CareerService careerService;
    @Autowired
    CareerRepository careerRepository;

    private User mentorUser;
    private Mentor mentor;

    @BeforeEach
    @Override
    protected void init() {

        initDatabase();

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
    }

    @Test
    void getCareerResponse() {

        // Given
        List<Career> careers = careerRepository.findByMentor(mentor);
        Career career = careers.size() > 0 ? careers.get(0) : null;

        // When
        CareerResponse careerResponse = careerService.getCareerResponse(mentorUser, career.getId());
        // Then
        assertAll(
                () -> assertThat(careerResponse).extracting("job").isEqualTo(career.getJob()),
                () -> assertThat(careerResponse).extracting("companyName").isEqualTo(career.getCompanyName()),
                () -> assertThat(careerResponse).extracting("others").isEqualTo(career.getOthers()),
                () -> assertThat(careerResponse).extracting("license").isEqualTo(career.getLicense())
        );
    }

    @Test
    void Career_등록() {

        // Given
        // When
        Career created = careerService.createCareer(mentorUser, careerCreateRequest);

        // Then
        assertAll(
                () -> assertNotNull(created),
                () -> assertEquals(careerCreateRequest.getJob(), created.getJob()),
                () -> assertEquals(careerCreateRequest.getCompanyName(), created.getCompanyName()),
                () -> assertEquals(careerCreateRequest.getOthers(), created.getOthers()),
                () -> assertEquals(careerCreateRequest.getLicense(), created.getLicense())
        );

    }

    @Test
    void Career_수정() {

        // Given
        Career career = careerService.createCareer(mentorUser, careerCreateRequest);

        // When
        careerService.updateCareer(mentorUser, career.getId(), careerUpdateRequest);

        // Then
        Career updatedCareer = careerRepository.findById(career.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertNotNull(updatedCareer),
                () -> assertEquals(careerUpdateRequest.getJob(), updatedCareer.getJob()),
                () -> assertEquals(careerUpdateRequest.getCompanyName(), updatedCareer.getCompanyName()),
                () -> assertEquals(careerUpdateRequest.getOthers(), updatedCareer.getOthers()),
                () -> assertEquals(careerUpdateRequest.getLicense(), updatedCareer.getLicense())
        );
    }

    @Test
    void Career_삭제() {

        // Given
        Career career = careerService.createCareer(mentorUser, careerCreateRequest);

        // When
        careerService.deleteCareer(mentorUser, career.getId());

        // Then
        assertFalse(careerRepository.findById(career.getId()).isPresent());
    }
}