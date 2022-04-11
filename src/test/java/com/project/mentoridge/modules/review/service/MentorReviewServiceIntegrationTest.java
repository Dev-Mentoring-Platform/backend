package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MentorReviewServiceIntegrationTest extends AbstractTest {

    @WithAccount(NAME)
    @DisplayName("멘토 리뷰 등록")
    @Test
    void create_mentorReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        enrollment.check();
        MenteeReview parent = menteeReviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);

        // When
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lecture1Id, parent.getId(), mentorReviewCreateRequest);

        // Then
        MentorReview review = mentorReviewRepository.findByParentAndId(parent, child.getId()).orElse(null);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(parent, review.getParent()),
                () -> assertEquals(child, review),
                () -> assertEquals(mentorReviewCreateRequest.getContent(), review.getContent()),
                () -> assertEquals(lecture1, review.getLecture())
        );
    }

    @WithAccount(NAME)
    @DisplayName("멘토 리뷰 수정")
    @Test
    void update_mentorReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        enrollment.check();
        MenteeReview parent = menteeReviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lecture1Id, parent.getId(), mentorReviewCreateRequest);

        // When
        mentorReviewService.updateMentorReview(mentorUser, lecture1Id, parent.getId(), child.getId(), mentorReviewUpdateRequest);

        // Then
        MentorReview review = mentorReviewRepository.findByParentAndId(parent, child.getId()).orElse(null);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(parent, review.getParent()),
                () -> assertEquals(child, review),
                () -> assertEquals(mentorReviewUpdateRequest.getContent(), review.getContent()),
                () -> assertEquals(lecture1, review.getLecture())
        );

    }

    @WithAccount(NAME)
    @DisplayName("멘토 리뷰 삭제")
    @Test
    void delete_mentorReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        enrollment.check();
        MenteeReview parent = menteeReviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lecture1Id, parent.getId(), mentorReviewCreateRequest);

        // When
        mentorReviewService.deleteMentorReview(mentorUser, lecture1Id, parent.getId(), child.getId());

        // Then
        assertTrue(menteeReviewRepository.findById(parent.getId()).isPresent());
        assertEquals(menteeReviewRepository.findByEnrollment(enrollment), menteeReviewRepository.findByLecture(lecture1).get(0));

        parent = menteeReviewRepository.findByLecture(lecture1).get(0);
        assertEquals(0, parent.getChildren().size());
        assertFalse(mentorReviewRepository.findById(child.getId()).isPresent());
    }
}