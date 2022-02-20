package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.vo.Review;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ReviewServiceIntegrationTest extends AbstractTest {

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 등록")
    @Test
    void createMenteeReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);

        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        assertEquals(1, enrollmentRepository.findByMenteeAndCanceledFalseAndClosedFalse(mentee).size());
        assertNull(cancellationRepository.findByEnrollment(enrollment));

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
    }

    // TODO - 멘티가 종료하는 것으로 변경
//    @WithAccount(NAME)
//    @DisplayName("멘티 리뷰 등록 - 종료된 강의")
//    @Test
//    void createMenteeReview_withClosedLecture() {
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

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 등록 - 취소한 강의")
    @Test
    void createMenteeReview_withCanceledLecture() {

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
    }

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 등록 - 수강 강의가 아닌 경우")
    @Test
    void createMenteeReview_unEnrolled() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);

        // When
        // Then
        assertThrows(EntityNotFoundException.class, () -> {
            reviewService.createMenteeReview(user, lecture2Id, menteeReviewCreateRequest);
        });
    }

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 수정")
    @Test
    void updateMenteeReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        Review review = reviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);

        // When
        reviewService.updateMenteeReview(user, lecture1Id, review.getId(), menteeReviewUpdateRequest);

        // Then
        Review updatedReview = reviewRepository.findByEnrollment(enrollment);
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
    void deleteMenteeReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        Review review = reviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);
        assertEquals(1, reviewRepository.findByLecture(lecture1).size());

        // When
        reviewService.deleteMenteeReview(user, lecture1Id, review.getId());

        // Then
        assertEquals(0, reviewRepository.findByLecture(lecture1).size());

    }

    @WithAccount(NAME)
    @DisplayName("멘토 리뷰 등록")
    @Test
    void createMentorReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        Review parent = reviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);

        // When
        Review child = reviewService.createMentorReview(mentorUser, lecture1Id, parent.getId(), mentorReviewCreateRequest);

        // Then
        List<Review> reviews = reviewRepository.findByLecture(lecture1);
        assertEquals(2, reviews.size());

        Review review = reviewRepository.findByParentAndId(parent, child.getId()).orElse(null);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(parent, review.getParent()),
                () -> assertEquals(child, review),
                () -> assertEquals(mentorReviewCreateRequest.getContent(), review.getContent()),
                () -> assertEquals(lecture1, review.getLecture()),
                () -> assertNull(review.getEnrollment())
        );
    }

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 삭제 - 멘토가 댓글을 단 경우")
    @Test
    void deleteMenteeReview_withChildren() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        Review parent = reviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);
        Review child = reviewService.createMentorReview(mentorUser, lecture1Id, parent.getId(), mentorReviewCreateRequest);

        // When
        reviewService.deleteMenteeReview(user, lecture1Id, parent.getId());

        // Then
        // children 삭제 체크
        List<Review> reviews = reviewRepository.findByLecture(lecture1);
        assertEquals(0, reviews.size());
        assertFalse(reviewRepository.findById(parent.getId()).isPresent());
        assertFalse(reviewRepository.findById(child.getId()).isPresent());
    }

    @WithAccount(NAME)
    @DisplayName("멘토 리뷰 수정")
    @Test
    void updateMentorReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        Review parent = reviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);
        Review child = reviewService.createMentorReview(mentorUser, lecture1Id, parent.getId(), mentorReviewCreateRequest);

        // When
        reviewService.updateMentorReview(mentorUser, lecture1Id, parent.getId(), child.getId(), mentorReviewUpdateRequest);

        // Then
        List<Review> reviews = reviewRepository.findByLecture(lecture1);
        assertEquals(2, reviews.size());

        Review review = reviewRepository.findByParentAndId(parent, child.getId()).orElse(null);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(parent, review.getParent()),
                () -> assertEquals(child, review),
                () -> assertEquals(mentorReviewUpdateRequest.getContent(), review.getContent()),
                () -> assertEquals(lecture1, review.getLecture()),
                () -> assertNull(review.getEnrollment())
        );

    }

    @WithAccount(NAME)
    @DisplayName("멘토 리뷰 삭제")
    @Test
    void deleteMentorReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePrice1.getId());
        Review parent = reviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);
        Review child = reviewService.createMentorReview(mentorUser, lecture1Id, parent.getId(), mentorReviewCreateRequest);

        // When
        reviewService.deleteMentorReview(mentorUser, lecture1Id, parent.getId(), child.getId());

        // Then
        // List<Review> reviews = reviewRepository.findByLecture(lecture1);
        // assertEquals(1, reviews.size());
        assertTrue(reviewRepository.findById(parent.getId()).isPresent());

        // parent = reviewRepository.findById(parent.getId()).orElse(null);
        assertEquals(reviewRepository.findByEnrollment(enrollment), reviewRepository.findByLecture(lecture1).get(0));
        parent = reviewRepository.findByLecture(lecture1).get(0);
        assertEquals(0, parent.getChildren().size());
        assertFalse(reviewRepository.findById(child.getId()).isPresent());
    }
}