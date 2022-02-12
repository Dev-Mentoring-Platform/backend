package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Pick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@Transactional
@SpringBootTest
class PickServiceIntegrationTest extends AbstractTest {

    private User mentorUser;
    private Mentor mentor;
    private Lecture lecture1;
    private Long lecture1Id;
    private Lecture lecture2;
    private Long lecture2Id;

//    @Test
//    void getPicks() {
//    }

    @BeforeEach
    void init() {

        SignUpRequest signUpRequest = getSignUpRequest("mentor", "mentor");
        mentorUser = loginService.signUp(signUpRequest);
        loginService.verifyEmail(mentorUser.getUsername(), mentorUser.getEmailVerifyToken());
        mentor = mentorService.createMentor(mentorUser, mentorSignUpRequest);

        lecture1 = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecture1Id = lecture1.getId();

        LectureCreateRequest.LecturePriceCreateRequest lecturePriceCreateRequest2
                = LectureCreateRequest.LecturePriceCreateRequest.of(false, 3, 1000L, 3, 10, 30000L);
        LectureCreateRequest.LectureSubjectCreateRequest lectureSubjectCreateRequest2
                = LectureCreateRequest.LectureSubjectCreateRequest.of(LearningKindType.IT, "자바스크립트");
        LectureCreateRequest lectureCreateRequest2 = LectureCreateRequest.of(
                "https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c",
                "제목2",
                "소제목2",
                "소개2",
                DifficultyType.INTERMEDIATE,
                "<p>본문2</p>",
                Arrays.asList(SystemType.OFFLINE),
                Arrays.asList(lecturePriceCreateRequest2),
                Arrays.asList(lectureSubjectCreateRequest2)
        );
        lecture2 = lectureService.createLecture(mentorUser, lectureCreateRequest2);
        lecture2Id = lecture2.getId();
    }

    @WithAccount(NAME)
    @Test
    void createPick() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        // When
        Long pickId = pickService.createPick(user, lecture1Id).getId();

        // Then
        Pick pick = pickRepository.findById(pickId).orElse(null);
        assertAll(
                () -> assertNotNull(pick),
                () -> assertEquals(mentee, pick.getMentee()),
                () -> assertEquals(lecture1, pick.getLecture())
        );
    }

    @WithAccount(NAME)
    @Test
    void deletePick() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        Long pickId = pickService.createPick(user, lecture1Id).getId();

        // When
        pickService.deletePick(user, pickId);

        // Then
        Pick pick = pickRepository.findById(pickId).orElse(null);
        assertNull(pick);
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }

    @WithAccount(NAME)
    @Test
    void deleteAllPicks() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        Long pick1Id = pickService.createPick(user, lecture1Id).getId();
        Long pick2Id = pickService.createPick(user, lecture2Id).getId();
        assertEquals(2, pickRepository.findByMentee(mentee).size());

        // When
        pickService.deleteAllPicks(user);

        // Then
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }
}