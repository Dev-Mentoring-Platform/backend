package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class CareerServiceIntegrationTest extends AbstractTest {

    @BeforeEach
    void before() {
        careerRepository.deleteAll();
        mentorRepository.deleteAll();
    }

    @WithAccount(NAME)
    @Test
    void Career_등록() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        careerService.createCareer(user, careerCreateRequest);

        // Then
        Mentor mentor = mentorRepository.findByUser(user);
        assertEquals(1, careerRepository.findByMentor(mentor).size());

    }

    @WithAccount(NAME)
    @Test
    void Career_수정() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        Career career = careerService.createCareer(user, careerCreateRequest);
        Long careerId = career.getId();

        // When
        careerService.updateCareer(user, careerId, careerUpdateRequest);

        // Then
        Career updatedCareer = careerRepository.findById(careerId).orElse(null);
        assertAll(
                () -> assertNotNull(updatedCareer),
                () -> assertEquals(careerUpdateRequest.getCompanyName(), updatedCareer.getCompanyName())
        );
    }

    @WithAccount(NAME)
    @Test
    void Career_삭제() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        Career career = careerService.createCareer(user, careerCreateRequest);
        Long careerId = career.getId();

        // When
        careerService.deleteCareer(user, careerId);

        // Then
        Career deletedCareer = careerRepository.findById(careerId).orElse(null);
        Assertions.assertNull(deletedCareer);

        Mentor mentor = mentorRepository.findByUser(user);
        assertEquals(0, mentor.getCareers().size());
    }
}