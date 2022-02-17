package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Pick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.init.TestDataBuilder.getLectureCreateRequestWithTitleAndPricePerHourAndTimePerLectureAndNumberOfLecturesAndLearningKindAndKrSubject;
import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Transactional
@MockMvcTest
class MenteePickControllerIntegrationTest extends AbstractTest {

    @Autowired
    MockMvc mockMvc;

    private User mentorUser;
    private Mentor mentor;
    private Lecture lecture1;
    private Long lecture1Id;
    private Lecture lecture2;
    private Long lecture2Id;

    @BeforeEach
    void init() {

        SignUpRequest signUpRequest = getSignUpRequestWithNameAndNickname("mentor", "mentor");
        mentorUser = loginService.signUp(signUpRequest);
        loginService.verifyEmail(mentorUser.getUsername(), mentorUser.getEmailVerifyToken());
        mentor = mentorService.createMentor(mentorUser, mentorSignUpRequest);

        lecture1 = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecture1Id = lecture1.getId();

        LectureCreateRequest lectureCreateRequest2 =
                getLectureCreateRequestWithTitleAndPricePerHourAndTimePerLectureAndNumberOfLecturesAndLearningKindAndKrSubject("제목2", 2000L, 3, 5, LearningKindType.IT, "자바스크립트");
        lecture2 = lectureService.createLecture(mentorUser, lectureCreateRequest2);
        lecture2Id = lecture2.getId();
    }

//    @Test
//    void getPicks() {
//    }

    @WithAccount(NAME)
    @Test
    void subtractPick() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        Long pickId = pickService.createPick(user, lecture1Id).getId();

        // When
        mockMvc.perform(delete("/mentees/my-picks/{pick_id}", pickId))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        Pick pick = pickRepository.findById(pickId).orElse(null);
        assertNull(pick);
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }

    @WithAccount(NAME)
    @Test
    void clear() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        Long pick1Id = pickService.createPick(user, lecture1Id).getId();
        Long pick2Id = pickService.createPick(user, lecture2Id).getId();
        assertEquals(2, pickRepository.findByMentee(mentee).size());

        // When
        mockMvc.perform(delete("/mentees/my-picks"))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        assertFalse(pickRepository.findById(pick1Id).isPresent());
        assertFalse(pickRepository.findById(pick2Id).isPresent());
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }
}