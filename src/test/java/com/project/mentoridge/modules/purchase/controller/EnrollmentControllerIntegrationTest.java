package com.project.mentoridge.modules.purchase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Transactional
@MockMvcTest
class EnrollmentControllerIntegrationTest extends AbstractTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    private Mentor mentor;
    private Lecture lecture;
    private Long lectureId;

    @BeforeEach
    void init() {

        SignUpRequest signUpRequest = getSignUpRequestWithNameAndNickname("mentor", "mentor");
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        mentor = mentorService.createMentor(user, mentorSignUpRequest);
        lecture = lectureService.createLecture(user, lectureCreateRequest);
        lectureId = lecture.getId();
    }

    @WithAccount(NAME)
    @Test
    void 강의수강() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        Long lecturePriceId = lecturePrice.getId();

        // When
        mockMvc.perform(post("/lectures/{lecture_id}/{lecture_price_id}/enrollments", lectureId, lecturePriceId))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        assertEquals(1, enrollmentRepository.findByMenteeAndCanceledFalseAndClosedFalse(mentee).size());
        Enrollment enrollment = enrollmentRepository.findByMenteeAndCanceledFalseAndClosedFalse(mentee).get(0);
        assertAll(
                () -> assertNotNull(enrollment),
                () -> assertEquals(lecture.getTitle(), enrollment.getLecture().getTitle()),
                () -> assertEquals(mentee.getUser().getName(), enrollment.getMentee().getUser().getName()),
                () -> assertEquals(lecturePrice.getIsGroup(), enrollment.getLecturePrice().getIsGroup()),
                () -> assertEquals(lecturePrice.getNumberOfMembers(), enrollment.getLecturePrice().getNumberOfMembers()),
                () -> assertEquals(lecturePrice.getPricePerHour(), enrollment.getLecturePrice().getPricePerHour()),
                () -> assertEquals(lecturePrice.getNumberOfLectures(), enrollment.getLecturePrice().getNumberOfLectures())
        );

        // 강의 수강 시 채팅방 자동 생성
        Chatroom chatroom = chatroomRepository.findByEnrollment(enrollment).orElse(null);
        List<Chatroom> chatrooms = chatroomRepository.findByMentorAndMentee(mentor, mentee);
        assertAll(
                () -> assertNotNull(chatroom),
                () -> assertEquals(1, chatrooms.size()),
                () -> assertEquals(chatroom, chatrooms.get(0)),
                () -> assertEquals(enrollment, chatroom.getEnrollment()),
                () -> assertEquals(enrollment.getLecture().getMentor(), chatroom.getMentor()),
                () -> assertEquals(mentee, chatroom.getMentee())
        );
    }
}