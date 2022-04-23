package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@Transactional
@SpringBootTest
class _MenteeReviewServiceIntegrationTest extends AbstractTest {

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 등록 - 확인된 등록이 아닌 경우")
    @Test
    void create_menteeReview_when_not_checked_enrollment() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);

        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        assertEquals(1, enrollmentRepository.findByMentee(mentee).size());

        // When
        // Then
        assertThrows(RuntimeException.class,
                () -> menteeReviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest)
        );
    }
    
    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 등록")
    @Test
    void create_menteeReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);

        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        assertEquals(1, enrollmentRepository.findByMentee(mentee).size());
        enrollment.check();

        // When
        menteeReviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);

        // Then
        MenteeReview review = menteeReviewRepository.findByEnrollment(enrollment);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(enrollment, review.getEnrollment()),
                () -> assertEquals(0, review.getChildren().size()),
                () -> assertEquals(lecture1, review.getLecture()),
                () -> assertEquals(menteeReviewCreateRequest.getContent(), review.getContent()),
                () -> assertEquals(menteeReviewCreateRequest.getScore(), review.getScore())
        );
    }

//    @WithAccount(NAME)
//    @DisplayName("멘티 리뷰 등록 - 종료된 강의")
//    @Test
//    void create_menteeReview_with_closedLecture() {
//
//        // Given
//        User user = userRepository.findByUsername(USERNAME).orElse(null);
//        Mentee mentee = menteeRepository.findByUser(user);
//        assertNotNull(user);
//
//        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
//
//        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
//        assertEquals(1, enrollmentRepository.findByMentee(mentee).size());
//        assertNull(cancellationRepository.findByEnrollment(enrollment));
//        assertNotNull(chatroomRepository.findByEnrollment(enrollment));
//
//        Chatroom chatroom = chatroomRepository.findByEnrollment(enrollment).orElse(null);
//        Long chatroomId = chatroom.getId();
//
//        // 수강 종료
//        enrollmentService.close(mentorUser, lecture1Id, enrollment.getId());
//
//        // When
//        reviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);
//
//        // Then
//        Review review = reviewRepository.findByEnrollment(enrollment);
//        assertNotNull(review);
//        assertAll(
//                () -> assertEquals(enrollment, review.getEnrollment()),
//                () -> assertEquals(0, review.getChildren().size()),
//                () -> assertEquals(lecture1, review.getLecture()),
//                () -> assertEquals(menteeReviewCreateRequest.getContent(), review.getContent()),
//                () -> assertEquals(menteeReviewCreateRequest.getScore(), review.getScore())
//        );
//    }

/*
    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 등록 - 취소한 강의")
    @Test
    void create_menteeReview_with_canceledLecture() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);

        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        assertEquals(1, enrollmentRepository.findByMenteeAndCanceledFalseAndClosedFalse(mentee).size());
        assertNull(cancellationRepository.findByEnrollment(enrollment));

        cancellationService.cancel(user, lecture1Id, cancellationCreateRequest);
        assertNotNull(cancellationRepository.findByEnrollment(enrollment));

        // When
        reviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);

        // Then
        Review review = reviewRepository.findByEnrollment(enrollment);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(enrollment, review.getEnrollment()),
                () -> assertEquals(0, review.getChildren().size()),
                () -> assertEquals(lecture1, review.getLecture()),
                () -> assertEquals(menteeReviewCreateRequest.getContent(), review.getContent()),
                () -> assertEquals(menteeReviewCreateRequest.getScore(), review.getScore())
        );
    }*/

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 등록 - 수강 강의가 아닌 경우")
    @Test
    void create_menteeReview_unEnrolled() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);

        // When
        // Then
        assertThrows(EntityNotFoundException.class, () -> {
            menteeReviewService.createMenteeReview(user, lecture2Id, menteeReviewCreateRequest);
        });
    }

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 수정")
    @Test
    void update_menteeReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        enrollment.check();
        MenteeReview review = menteeReviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);

        // When
        menteeReviewService.updateMenteeReview(user, lecture1Id, review.getId(), menteeReviewUpdateRequest);

        // Then
        MenteeReview updatedReview = menteeReviewRepository.findByEnrollment(enrollment);
        assertNotNull(updatedReview);
        assertAll(
                () -> assertEquals(enrollment, updatedReview.getEnrollment()),
                () -> assertEquals(0, updatedReview.getChildren().size()),
                () -> assertEquals(lecture1, updatedReview.getLecture()),
                () -> assertEquals(menteeReviewUpdateRequest.getContent(), updatedReview.getContent()),
                () -> assertEquals(menteeReviewUpdateRequest.getScore(), updatedReview.getScore())
        );
    }

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 삭제")
    @Test
    void delete_menteeReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        enrollment.check();
        MenteeReview review = menteeReviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);
        assertEquals(1, menteeReviewRepository.findByLecture(lecture1).size());

        // When
        menteeReviewService.deleteMenteeReview(user, lecture1Id, review.getId());

        // Then
        assertEquals(0, menteeReviewRepository.findByLecture(lecture1).size());

    }

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 삭제 - 멘토가 댓글을 단 경우")
    @Test
    void delete_menteeReview_withChildren() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        enrollment.check();
        MenteeReview parent = menteeReviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lecture1Id, parent.getId(), mentorReviewCreateRequest);

        // When
        menteeReviewService.deleteMenteeReview(user, lecture1Id, parent.getId());

        // Then
        // children 삭제 체크
        List<MenteeReview> reviews = menteeReviewRepository.findByLecture(lecture1);
        assertEquals(0, reviews.size());
        assertFalse(menteeReviewRepository.findById(parent.getId()).isPresent());
        assertFalse(mentorReviewRepository.findById(child.getId()).isPresent());
    }

}