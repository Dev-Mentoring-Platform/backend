package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.repository.EducationRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.mentoridge.configuration.AbstractTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
class EducationServiceIntegrationTest {

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    UserRepository userRepository;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    EducationService educationService;
    @Autowired
    EducationRepository educationRepository;

    @WithAccount(NAME)
    @Test
    void getEducationResponse() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentor mentor = mentorService.createMentor(user, mentorSignUpRequest);
        List<Education> educations = educationRepository.findByMentor(mentor);
        Education education = educations.size() > 0 ? educations.get(0) : null;

        // When
        EducationResponse educationResponse = educationService.getEducationResponse(user, education.getId());
        // Then
        assertAll(
                () -> assertThat(educationResponse).extracting("educationLevel").isEqualTo(education.getEducationLevel()),
                () -> assertThat(educationResponse).extracting("schoolName").isEqualTo(education.getSchoolName()),
                () -> assertThat(educationResponse).extracting("major").isEqualTo(education.getMajor()),
                () -> assertThat(educationResponse).extracting("others").isEqualTo(education.getOthers())
        );
    }

    @WithAccount(NAME)
    @Test
    void Education_등록() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        Education created = educationService.createEducation(user, educationCreateRequest);

        // Then
        Mentor mentor = mentorRepository.findByUser(user);
        // assertEquals(2, educationRepository.findByMentor(mentor).size());
        Assertions.assertNotNull(created);
        assertAll(
                () -> assertEquals(educationCreateRequest.getEducationLevel(), created.getEducationLevel()),
                () -> assertEquals(educationCreateRequest.getSchoolName(), created.getSchoolName()),
                () -> assertEquals(educationCreateRequest.getMajor(), created.getMajor()),
                () -> assertEquals(educationCreateRequest.getOthers(), created.getOthers())
        );
    }

    @WithAccount(NAME)
    @Test
    void Education_수정() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        Education education = educationService.createEducation(user, educationCreateRequest);
        Long educationId = education.getId();

        // When
        educationService.updateEducation(user, educationId, educationUpdateRequest);

        // Then
        Education updatedEducation = educationRepository.findById(educationId).orElse(null);
        Assertions.assertNotNull(updatedEducation);
        assertAll(
                () -> assertEquals(educationUpdateRequest.getEducationLevel(), updatedEducation.getEducationLevel()),
                () -> assertEquals(educationUpdateRequest.getSchoolName(), updatedEducation.getSchoolName()),
                () -> assertEquals(educationUpdateRequest.getMajor(), updatedEducation.getMajor()),
                () -> assertEquals(educationUpdateRequest.getOthers(), updatedEducation.getOthers())
        );
    }

    @WithAccount(NAME)
    @Test
    void Education_삭제() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        Education education = educationService.createEducation(user, educationCreateRequest);
        Long educationId = education.getId();

        // When
        educationService.deleteEducation(user, educationId);

        // Then
        Education deletedEducation = educationRepository.findById(educationId).orElse(null);
        Assertions.assertNull(deletedEducation);

        Mentor mentor = mentorRepository.findByUser(user);
        assertEquals(1, mentor.getEducations().size());
    }
}