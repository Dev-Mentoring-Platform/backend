package com.project.mentoridge.modules.purchase.controller;

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

import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getLectureCreateRequestWithTitleAndPricePerHourAndTimePerLectureAndNumberOfLecturesAndLearningKindAndKrSubject;
import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Transactional
@MockMvcTest
class PickControllerIntegrationTest extends AbstractTest {

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
                getLectureCreateRequestWithTitleAndPricePerHourAndTimePerLectureAndNumberOfLecturesAndLearningKindAndKrSubject("제목2", 1000L, 3, 10, LearningKindType.IT, "자바스크립트");
        lecture2 = lectureService.createLecture(mentorUser, lectureCreateRequest2);
        lecture2Id = lecture2.getId();
    }

    @WithAccount(NAME)
    @Test
    void addPick() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        // When
        mockMvc.perform(post("/lectures/{lecture_id}/picks", lecture1Id))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        List<Pick> picks = pickRepository.findByMentee(mentee);
        assertEquals(1, picks.size());
        Pick pick = picks.get(0);
        assertAll(
                () -> assertNotNull(pick),
                () -> assertEquals(mentee, pick.getMentee()),
                () -> assertEquals(lecture1, pick.getLecture())
        );
    }
}