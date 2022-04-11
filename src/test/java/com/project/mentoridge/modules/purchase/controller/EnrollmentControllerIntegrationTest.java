package com.project.mentoridge.modules.purchase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class EnrollmentControllerIntegrationTest extends AbstractTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @WithAccount(NAME)
    @Test
    void cannot_enroll_unapproved_or_closed_lecture() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture1).get(0);
        Long lecturePriceId = lecturePrice.getId();

        // When
        lecture1.cancelApproval();  // 승인 취소

        // Then
        mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/enrollments", lecture1Id, lecturePriceId))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    @WithAccount(NAME)
    @Test
    void can_enroll() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture1).get(0);
        Long lecturePriceId = lecturePrice.getId();

        // When
        mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/enrollments", lecture1Id, lecturePriceId))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        assertEquals(1, enrollmentRepository.findByMentee(mentee).size());
        Enrollment enrollment = enrollmentRepository.findByMentee(mentee).get(0);
        assertAll(
                () -> assertNotNull(enrollment),
                () -> assertEquals(mentee, enrollment.getMentee()),
                () -> assertEquals(mentee.getUser().getName(), enrollment.getMentee().getUser().getName()),
                // lecture
                () -> assertEquals(lecture1, enrollment.getLecture()),
                () -> assertEquals(lecture1.getMentor(), enrollment.getLecture().getMentor()),
                () -> assertEquals(mentor, enrollment.getLecture().getMentor()),
                () -> assertEquals(lecture1.getTitle(), enrollment.getLecture().getTitle()),
                () -> assertEquals(lecture1.getSubTitle(), enrollment.getLecture().getSubTitle()),
                () -> assertEquals(lecture1.getIntroduce(), enrollment.getLecture().getIntroduce()),
                () -> assertEquals(lecture1.getContent(), enrollment.getLecture().getContent()),
                () -> assertEquals(lecture1.getDifficulty(), enrollment.getLecture().getDifficulty()),
                () -> assertEquals(lecture1.getThumbnail(), enrollment.getLecture().getThumbnail()),
                // lectureSubject

                // lecturePrice
                () -> assertEquals(lecturePrice.getIsGroup(), enrollment.getLecturePrice().getIsGroup()),
                () -> assertEquals(lecturePrice.getNumberOfMembers(), enrollment.getLecturePrice().getNumberOfMembers()),
                () -> assertEquals(lecturePrice.getPricePerHour(), enrollment.getLecturePrice().getPricePerHour()),
                () -> assertEquals(lecturePrice.getTimePerLecture(), enrollment.getLecturePrice().getTimePerLecture()),
                () -> assertEquals(lecturePrice.getNumberOfLectures(), enrollment.getLecturePrice().getNumberOfLectures()),
                () -> assertEquals(lecturePrice.getTotalPrice(), enrollment.getLecturePrice().getTotalPrice())
        );
    }
}