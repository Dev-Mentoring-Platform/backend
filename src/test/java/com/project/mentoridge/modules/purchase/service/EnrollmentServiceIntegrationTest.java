package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.EnrollmentLogService;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.log.component.LecturePriceLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.configuration.AbstractTest.menteeReviewCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.mentorReviewCreateRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class EnrollmentServiceIntegrationTest {

    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    LecturePriceLogService lecturePriceLogService;
    @Autowired
    LecturePriceRepository lecturePriceRepository;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentLogService enrollmentLogService;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;
    @Autowired
    MentorReviewService mentorReviewService;
    @Autowired
    MentorReviewRepository mentorReviewRepository;

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;

    @Autowired
    SubjectRepository subjectRepository;

    private Subject subject1;
    private Subject subject2;

    private User mentorUser;
    private Mentor mentor;
    private User menteeUser;
    private Mentee mentee;

    private Lecture lecture;
    private LecturePrice lecturePrice;

    @BeforeEach
    void init() {

        // subject
        subject1 = subjectRepository.save(Subject.builder()
                .subjectId(1L)
                .learningKind(LearningKindType.IT)
                .krSubject("백엔드")
                .build());
        subject2 = subjectRepository.save(Subject.builder()
                .subjectId(2L)
                .learningKind(LearningKindType.IT)
                .krSubject("프론트엔드")
                .build());

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);

        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);
    }

    @DisplayName("신청 강의 리스트 / 승인 예정 - 페이징")
    @Test
    void get_paged_EnrollmentWithLecturePriceResponses() {

    }

    @DisplayName("수강 강의 조회")
    @Test
    void get_LecturePriceWithLectureResponse_by_enrollmentId() {

    }

    @DisplayName("리뷰 작성 수강내역 리스트")
    @Test
    void get_paged_reviewed_EnrollmentWithSimpleLectureResponses() {

    }

    @DisplayName("리뷰 미작성 수강내역 리스트")
    @Test
    void get_paged_unreviewed_EnrollmentWithSimpleLectureResponses() {

    }

    @DisplayName("수강 내역 조회")
    @Test
    void get_EnrollmentWithSimpleLectureResponse_by_enrollmentId() {

    }

    @Test
    void can_enroll() {

        // Given
        // When
        Enrollment saved = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());

        // Then
        Enrollment enrollment = enrollmentRepository.findById(saved.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertNotNull(enrollment),
                () -> assertEquals(mentee, enrollment.getMentee()),
                () -> assertEquals(mentee.getUser().getName(), enrollment.getMentee().getUser().getName()),

                // lecture
                () -> assertEquals(lecture, enrollment.getLecture()),
                () -> assertEquals(lecture.getMentor(), enrollment.getLecture().getMentor()),
                () -> assertEquals(lecture.getTitle(), enrollment.getLecture().getTitle()),
                () -> assertEquals(lecture.getSubTitle(), enrollment.getLecture().getSubTitle()),
                () -> assertEquals(lecture.getIntroduce(), enrollment.getLecture().getIntroduce()),
                () -> assertEquals(lecture.getContent(), enrollment.getLecture().getContent()),
                () -> assertEquals(lecture.getDifficulty(), enrollment.getLecture().getDifficulty()),
                () -> assertEquals(lecture.getThumbnail(), enrollment.getLecture().getThumbnail()),

                // lecturePrice
                () -> assertEquals(lecturePrice, enrollment.getLecturePrice()),
                () -> assertEquals(lecturePrice.getIsGroup(), enrollment.getLecturePrice().getIsGroup()),
                () -> assertEquals(lecturePrice.getNumberOfMembers(), enrollment.getLecturePrice().getNumberOfMembers()),
                () -> assertEquals(lecturePrice.getPricePerHour(), enrollment.getLecturePrice().getPricePerHour()),
                () -> assertEquals(lecturePrice.getTimePerLecture(), enrollment.getLecturePrice().getTimePerLecture()),
                () -> assertEquals(lecturePrice.getNumberOfLectures(), enrollment.getLecturePrice().getNumberOfLectures()),
                () -> assertEquals(lecturePrice.getTotalPrice(), enrollment.getLecturePrice().getTotalPrice()),

                () -> assertFalse(enrollment.isChecked()),
                () -> assertNull(enrollment.getCheckedAt()),

                () -> assertFalse(enrollment.isFinished()),
                () -> assertNull(enrollment.getFinishedAt())
        );
    }

    @Test
    void cannot_enroll_unapproved_lecture() {

        // Given
        // When
        lecture.cancelApproval();  // 승인 취소

        // Then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
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

    @Test
    void cannot_enroll_closed_lecturePrice() {

        // Given
        // When
        lecturePrice.close(mentorUser, lecturePriceLogService);  // 강의 모집 종료

        // Then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        });
    }

    @Test
    void 강의중복수강_실패() {

        // Given
        enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());

        // When
        // Then
        assertThrows(AlreadyExistException.class, () -> {
            enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
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

    @Test
    void delete() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        // 2022.03.05 - 강의 신청 시 멘토 확인 필요
        enrollment.check(menteeUser, enrollmentLogService);

        MenteeReview menteeReview = menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), menteeReviewCreateRequest);
        MentorReview mentorReview = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), menteeReview.getId(), mentorReviewCreateRequest);
        assertAll(
                () -> assertNotNull(menteeReview),
                () -> assertEquals(enrollment, menteeReview.getEnrollment()),
                () -> assertEquals(1, menteeReview.getChildren().size()),
                () -> assertEquals(lecture, menteeReview.getLecture()),
                () -> assertEquals(menteeReviewCreateRequest.getContent(), menteeReview.getContent()),
                () -> assertEquals(menteeReviewCreateRequest.getScore(), menteeReview.getScore())
        );

        // When
        enrollmentService.deleteEnrollment(enrollment);

        // Then
        assertAll(
                () -> assertFalse(enrollmentRepository.findById(enrollment.getId()).isPresent()),
                () -> assertFalse(menteeReviewRepository.findById(menteeReview.getId()).isPresent()),
                () -> assertFalse(mentorReviewRepository.findById(mentorReview.getId()).isPresent())
        );
    }

    @DisplayName("신청 승인")
    @Test
    void check_enrollment_by_mentor() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());

        // When
        enrollmentService.check(mentorUser, enrollment.getId());
        // Then
        assertThat(enrollment.isChecked()).isTrue();
        assertThat(enrollment.getCheckedAt()).isNotNull();
    }

    @DisplayName("이미 신청 승인한 강의 - RuntimeException")
    @Test
    void check_already_checked_enrollment() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        enrollmentService.check(mentorUser, enrollment.getId());

        // When
        // Then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.check(mentorUser, enrollment.getId());
        });
    }

    @DisplayName("수강 완료")
    @Test
    void finish_by_mentee() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        enrollmentService.check(mentorUser, enrollment.getId());

        // When
        enrollmentService.finish(menteeUser, enrollment.getId());
        // Then
        assertThat(enrollment.isFinished()).isTrue();
        assertThat(enrollment.getFinishedAt()).isNotNull();
    }

    @DisplayName("수강 완료 - 신청 승인되지 않은 강의")
    @Test
    void finish_uncheckedEnrollment() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());

        // When
        // Then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.finish(menteeUser, enrollment.getId());
        });
    }

    @DisplayName("수강 완료 - 이미 수강 완료된 강의")
    @Test
    void finish_alreadyFinishedEnrollment() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        enrollmentService.check(mentorUser, enrollment.getId());
        enrollmentService.finish(menteeUser, enrollment.getId());

        // When
        // Then
        assertThrows(RuntimeException.class, () -> {
            enrollmentService.finish(menteeUser, enrollment.getId());
        });
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