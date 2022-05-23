package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class EnrollmentServiceIntegrationTest {

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    EntityManager em;

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    LectureService lectureService;
    @Autowired
    LecturePriceRepository lecturePriceRepository;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    ChatroomRepository chatroomRepository;
    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;

    @Autowired
    SubjectRepository subjectRepository;

    private Lecture lecture;
    private Mentor mentor;

    @BeforeEach
    void init() {

        // subject
        if (subjectRepository.count() == 0) {
            subjectRepository.save(Subject.builder()
                    .subjectId(1L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("백엔드")
                    .build());
            subjectRepository.save(Subject.builder()
                    .subjectId(2L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("프론트엔드")
                    .build());
        }

        User mentorUser = loginService.signUp(getSignUpRequestWithNameAndNickname("mentor", "mentor"));
        // loginService.verifyEmail(mentorUser.getUsername(), mentorUser.getEmailVerifyToken());
        mentorUser.verifyEmail();
        menteeRepository.save(Mentee.builder()
                .user(mentorUser)
                .build());
        mentor = mentorService.createMentor(mentorUser, mentorSignUpRequest);

        lecture = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecture.approve();
    }

    @WithAccount(NAME)
    @Test
    void can_enroll() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        Long lecturePriceId = lecturePrice.getId();

        // When
        enrollmentService.createEnrollment(user, lecture.getId(), lecturePriceId);

        // Then
        assertEquals(1, enrollmentRepository.findByMentee(mentee).size());
        Enrollment enrollment = enrollmentRepository.findByMentee(mentee).get(0);
        assertAll(
                () -> assertNotNull(enrollment),
                () -> assertEquals(mentee, enrollment.getMentee()),
                () -> assertEquals(mentee.getUser().getName(), enrollment.getMentee().getUser().getName()),
                // lecture
                () -> assertEquals(lecture, enrollment.getLecture()),
                () -> assertEquals(lecture.getMentor(), enrollment.getLecture().getMentor()),
                () -> assertEquals(mentor, enrollment.getLecture().getMentor()),
                () -> assertEquals(lecture.getTitle(), enrollment.getLecture().getTitle()),
                () -> assertEquals(lecture.getSubTitle(), enrollment.getLecture().getSubTitle()),
                () -> assertEquals(lecture.getIntroduce(), enrollment.getLecture().getIntroduce()),
                () -> assertEquals(lecture.getContent(), enrollment.getLecture().getContent()),
                () -> assertEquals(lecture.getDifficulty(), enrollment.getLecture().getDifficulty()),
                () -> assertEquals(lecture.getThumbnail(), enrollment.getLecture().getThumbnail()),
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

    @WithAccount(NAME)
    @Test
    void cannot_enroll_unapproved_lecture() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        Long lecturePriceId = lecturePrice.getId();

        // When
        lecture.cancelApproval();  // 승인 취소
        // Then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.createEnrollment(user, lecture.getId(), lecturePriceId);
        });
    }
/*
    @WithAccount(NAME)
    @Test
    void cannot_enroll_closed_lecture() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture1).get(0);
        Long lecturePriceId = lecturePrice.getId();

        // When
        lecture1.close();  // 강의 모집 종료

        // Then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.createEnrollment(user, lecture1Id, lecturePriceId);
        });
    }*/

    @WithAccount(NAME)
    @Test
    void cannot_enroll_closed_lecturePrice() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        Long lecturePriceId = lecturePrice.getId();

        // When
        lecturePrice.close();  // 강의 모집 종료

        // Then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.createEnrollment(user, lecture.getId(), lecturePriceId);
        });
    }

    @WithAccount(NAME)
    @Test
    void 강의중복수강_실패() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        Long lecturePriceId = lecturePrice.getId();

        enrollmentService.createEnrollment(user, lecture.getId(), lecturePriceId);

        // When
        assertThrows(AlreadyExistException.class, () -> {
            enrollmentService.createEnrollment(user, lecture.getId(), lecturePriceId);
        });

    }
/*
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
    }*/

    @WithAccount(NAME)
    @Test
    void delete() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        Long lecturePriceId = lecturePrice.getId();

        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture.getId(), lecturePriceId);
        // 2022.03.05 - 강의 신청 시 멘토 확인 필요
        enrollment.check();
        assertAll(
                () -> assertEquals(1, enrollmentRepository.findByMentee(mentee).size())
        );

        menteeReviewService.createMenteeReview(user, enrollment.getId(), menteeReviewCreateRequest);
        MenteeReview review = menteeReviewRepository.findByEnrollment(enrollment);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(enrollment, review.getEnrollment()),
                () -> assertEquals(0, review.getChildren().size()),
                () -> assertEquals(lecture, review.getLecture()),
                () -> assertEquals(menteeReviewCreateRequest.getContent(), review.getContent()),
                () -> assertEquals(menteeReviewCreateRequest.getScore(), review.getScore())
        );

        // When
        enrollmentService.deleteEnrollment(enrollment);

        // Then
        assertAll(
                // () -> assertEquals(0, chatroomRepository.findByMentorAndMentee(mentor, mentee).size()),
                () -> assertFalse(enrollmentRepository.findByMenteeAndLecture(mentee, lecture).isPresent()),
                () -> assertTrue(menteeReviewRepository.findByLecture(lecture).isEmpty())
        );
    }

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