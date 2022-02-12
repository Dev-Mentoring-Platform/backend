package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Disabled
@Transactional
@SpringBootTest
class EducationServiceIntegrationTest extends AbstractTest {

    @WithAccount(NAME)
    @Test
    void Education_등록() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        educationService.createEducation(user, educationCreateRequest);

        // Then
        Mentor mentor = mentorRepository.findByUser(user);
        Assertions.assertEquals(1, educationRepository.findByMentor(mentor).size());
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
        Assertions.assertEquals(educationUpdateRequest.getMajor(), updatedEducation.getMajor());
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
        Assertions.assertEquals(0, mentor.getEducations().size());
    }
}