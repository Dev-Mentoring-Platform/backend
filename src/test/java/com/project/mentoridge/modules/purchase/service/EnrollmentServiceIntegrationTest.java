package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Cancellation;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.vo.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class EnrollmentServiceIntegrationTest extends AbstractTest {

    @Autowired
    EntityManager em;

    @WithAccount(NAME)
    @Test
    void 강의수강() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture1).get(0);
        Long lecturePriceId = lecturePrice.getId();

        // When
        enrollmentService.createEnrollment(user, lecture1Id, lecturePriceId);

        // Then
        assertEquals(1, enrollmentRepository.findByMenteeAndCanceledFalseAndClosedFalse(mentee).size());
        Enrollment enrollment = enrollmentRepository.findByMenteeAndCanceledFalseAndClosedFalse(mentee).get(0);
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
                () -> assertEquals(lecturePrice.getTotalPrice(), enrollment.getLecturePrice().getTotalPrice()),
                () -> assertFalse(enrollment.isClosed()),
                () -> assertFalse(enrollment.isCanceled())
        );
    }

    @WithAccount(NAME)
    @Test
    void 강의중복수강_실패() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture1).get(0);
        Long lecturePriceId = lecturePrice.getId();

        enrollmentService.createEnrollment(user, lecture1Id, lecturePriceId);

        // When
        assertThrows(AlreadyExistException.class, () -> {
            enrollmentService.createEnrollment(user, lecture1Id, lecturePriceId);
        });

    }

    @DisplayName("강의 구매 취소")
    @WithAccount(NAME)
    @Test
    void cancel() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture1).get(0);
        Long lecturePriceId = lecturePrice.getId();

        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePriceId);
        assertAll(
                () -> assertFalse(enrollment.isCanceled()),
                () -> assertEquals(1, enrollmentRepository.findByMenteeAndCanceledFalseAndClosedFalse(mentee).size())
        );

        // When
        cancellationService.cancel(user, lecture1Id, cancellationCreateRequest);

        // Then
        assertEquals(0, enrollmentRepository.findByMenteeAndCanceledFalseAndClosedFalse(mentee).size());
        assertEquals(1, enrollmentRepository.findAllByMenteeId(mentee.getId()).size());
        assertTrue(enrollment.isCanceled());

        Cancellation cancellation = cancellationRepository.findByEnrollment(enrollment);
        assertAll(
                () -> assertNotNull(cancellation),
                () -> assertEquals(lecture1.getTitle(), enrollment.getLecture().getTitle()),
                () -> assertEquals(mentee.getUser().getName(), enrollment.getMentee().getUser().getName())
        );
    }

    @WithAccount(NAME)
    @Test
    void delete() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture1).get(0);
        Long lecturePriceId = lecturePrice.getId();

        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture1Id, lecturePriceId);
        Long enrollmentId = enrollment.getId();
        assertAll(
                () -> assertFalse(enrollment.isCanceled()),
                () -> assertEquals(1, enrollmentRepository.findByMenteeAndCanceledFalseAndClosedFalse(mentee).size())
        );

        reviewService.createMenteeReview(user, lecture1Id, menteeReviewCreateRequest);
        Review review = reviewRepository.findByEnrollment(enrollment);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(enrollment, review.getEnrollment()),
                () -> assertEquals(0, review.getChildren().size()),
                () -> assertEquals(lecture1, review.getLecture()),
                () -> assertEquals(menteeReviewCreateRequest.getContent(), review.getContent()),
                () -> assertEquals(menteeReviewCreateRequest.getScore(), review.getScore())
        );

//        enrollmentService.cancel(user, lectureId);
//
//        assertEquals(0, enrollmentRepository.findByMentee(mentee).size());
//        assertEquals(1, enrollmentRepository.findAllByMentee(mentee.getId()).size());
//        assertTrue(enrollment.isCanceled());
//
//        Cancellation cancellation = cancellationRepository.findByEnrollment(enrollment);
//        assertAll(
//                () -> assertNotNull(cancellation),
//                () -> assertEquals(lecture.getTitle(), enrollment.getLecture().getTitle()),
//                () -> assertEquals(mentee.getUser().getName(), enrollment.getMentee().getUser().getName())
//        );
//
//        assertFalse(chatroomRepository.findById(chatroomId).isPresent());

        // When
        enrollmentService.deleteEnrollment(enrollment);

        // Then
        assertAll(
                () -> assertEquals(0, chatroomRepository.findByMentorAndMentee(mentor, mentee).size()),
                () -> assertFalse(enrollmentRepository.findAllByMenteeIdAndLectureId(mentee.getId(), lecture1Id).isPresent()),
                () -> assertTrue(reviewRepository.findByLecture(lecture1).isEmpty()),
                () -> assertNull(cancellationRepository.findByEnrollmentId(enrollmentId))
        );
    }

    // TODO - 멘티가 종료하는 것으로 변경
//    @DisplayName("강의 종료")
//    @WithAccount(NAME)
//    @Test
//    void close() {
//
//        // Given
//        User user = userRepository.findByUsername(USERNAME).orElse(null);
//        Mentee mentee = menteeRepository.findByUser(user);
//        assertNotNull(user);
//
//        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
//        Long lecturePriceId = lecturePrice.getId();
//
//        Enrollment enrollment = enrollmentService.createEnrollment(user, lectureId, lecturePriceId);
//        Chatroom chatroom = chatroomRepository.findByEnrollment(enrollment).orElse(null);
//        assertNotNull(chatroom);
//
//        assertFalse(enrollment.isCanceled());
//        assertFalse(enrollment.isClosed());
//
//        Long enrollmentId = enrollment.getId();
//        Long chatroomId = chatroom.getId();
//        User mentorUser = mentor.getUser();
//
//        // When
//        enrollmentService.close(mentorUser, lectureId, enrollmentId);
//
//        // Then
////        enrollment = enrollmentRepository.findById(enrollmentId).orElse(null);
////        assertTrue(enrollment.isClosed());
//        enrollment = enrollmentRepository.findByMenteeAndLecture(mentee, lecture).orElse(null);
//        assertNull(enrollment);
//        enrollment = enrollmentRepository.findAllById(enrollmentId);
//        assertNotNull(enrollment);
//        assertFalse(enrollment.isCanceled());
//        assertTrue(enrollment.isClosed());
//
//        assertFalse(chatroomRepository.findById(chatroomId).isPresent());
//        List<Chatroom> chatrooms = chatroomRepository.findByMentorAndMentee(mentor, mentee);
//        assertEquals(0, chatrooms.size());
//        assertFalse(chatroomRepository.findByEnrollment(enrollment).isPresent());
//    }
}