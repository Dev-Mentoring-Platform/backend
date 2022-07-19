package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.repository.EducationRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.mentoridge.configuration.AbstractTest.*;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMentorUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ServiceTest
class EducationServiceIntegrationTest {

    @Autowired
    EducationService educationService;
    @Autowired
    EducationRepository educationRepository;

    @Autowired
    LoginService loginService;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;

    private User mentorUser;
    private Mentor mentor;

    @BeforeEach
    void init() {

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
    }

    @Test
    void getEducationResponse() {

        // Given
        List<Education> educations = educationRepository.findByMentor(mentor);
        Education education = educations.size() > 0 ? educations.get(0) : null;

        // When
        EducationResponse educationResponse = educationService.getEducationResponse(mentorUser, education.getId());
        // Then
        assertAll(
                () -> assertThat(educationResponse).extracting("educationLevel").isEqualTo(education.getEducationLevel()),
                () -> assertThat(educationResponse).extracting("schoolName").isEqualTo(education.getSchoolName()),
                () -> assertThat(educationResponse).extracting("major").isEqualTo(education.getMajor()),
                () -> assertThat(educationResponse).extracting("others").isEqualTo(education.getOthers())
        );
    }

    @Test
    void Education_등록() {

        // Given
        // When
        Education created = educationService.createEducation(mentorUser, educationCreateRequest);

        // Then
        Assertions.assertNotNull(created);
        assertAll(
                () -> assertEquals(educationCreateRequest.getEducationLevel(), created.getEducationLevel()),
                () -> assertEquals(educationCreateRequest.getSchoolName(), created.getSchoolName()),
                () -> assertEquals(educationCreateRequest.getMajor(), created.getMajor()),
                () -> assertEquals(educationCreateRequest.getOthers(), created.getOthers())
        );
    }

    @Test
    void Education_수정() {

        // Given
        Education education = educationService.createEducation(mentorUser, educationCreateRequest);

        // When
        educationService.updateEducation(mentorUser, education.getId(), educationUpdateRequest);

        // Then
        Education updatedEducation = educationRepository.findById(education.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(educationUpdateRequest.getEducationLevel(), updatedEducation.getEducationLevel()),
                () -> assertEquals(educationUpdateRequest.getSchoolName(), updatedEducation.getSchoolName()),
                () -> assertEquals(educationUpdateRequest.getMajor(), updatedEducation.getMajor()),
                () -> assertEquals(educationUpdateRequest.getOthers(), updatedEducation.getOthers())
        );
    }

    @Test
    void Education_삭제() {

        // Given
        Education education = educationService.createEducation(mentorUser, educationCreateRequest);

        // When
        educationService.deleteEducation(mentorUser, education.getId());

        // Then
        assertFalse(educationRepository.findById(education.getId()).isPresent());
    }
}