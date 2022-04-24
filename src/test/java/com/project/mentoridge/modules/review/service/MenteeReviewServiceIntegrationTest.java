package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
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
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
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

import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MenteeReviewServiceIntegrationTest {

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

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
    MenteeReviewService menteeReviewService;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;
    @Autowired
    MentorReviewService mentorReviewService;
    @Autowired
    MentorReviewRepository mentorReviewRepository;

    @Autowired
    SubjectRepository subjectRepository;

    private Lecture lecture;
    private Mentor mentor;
    private User mentorUser;

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

        mentorUser = loginService.signUp(getSignUpRequestWithNameAndNickname("mentor", "mentor"));
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
    @DisplayName("멘티 리뷰 등록 - 확인된 등록이 아닌 경우")
    @Test
    void create_menteeReview_when_not_checked_enrollment() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);

        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture.getId(), lecturePrice1.getId());
        assertEquals(1, enrollmentRepository.findByMentee(mentee).size());

        // When
        // Then
        assertThrows(RuntimeException.class,
                () -> menteeReviewService.createMenteeReview(user, enrollment.getId(), menteeReviewCreateRequest)
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

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);

        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture.getId(), lecturePrice1.getId());
        assertEquals(1, enrollmentRepository.findByMentee(mentee).size());
        enrollment.check();

        // When
        menteeReviewService.createMenteeReview(user, enrollment.getId(), menteeReviewCreateRequest);

        // Then
        MenteeReview review = menteeReviewRepository.findByEnrollment(enrollment);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(enrollment, review.getEnrollment()),
                () -> assertEquals(0, review.getChildren().size()),
                () -> assertEquals(lecture, review.getLecture()),
                () -> assertEquals(menteeReviewCreateRequest.getContent(), review.getContent()),
                () -> assertEquals(menteeReviewCreateRequest.getScore(), review.getScore())
        );
    }

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 등록 - 수강 강의가 아닌 경우")
    @Test
    void create_menteeReview_unEnrolled() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);

        // When
        // Then
        assertThrows(EntityNotFoundException.class, () -> {
            menteeReviewService.createMenteeReview(user, 1000L, menteeReviewCreateRequest);
        });
    }

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 수정")
    @Test
    void update_menteeReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture.getId(), lecturePrice1.getId());
        enrollment.check();
        MenteeReview review = menteeReviewService.createMenteeReview(user, enrollment.getId(), menteeReviewCreateRequest);

        // When
        menteeReviewService.updateMenteeReview(user, review.getId(), menteeReviewUpdateRequest);

        // Then
        MenteeReview updatedReview = menteeReviewRepository.findByEnrollment(enrollment);
        assertNotNull(updatedReview);
        assertAll(
                () -> assertEquals(enrollment, updatedReview.getEnrollment()),
                () -> assertEquals(0, updatedReview.getChildren().size()),
                () -> assertEquals(lecture, updatedReview.getLecture()),
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

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture.getId(), lecturePrice1.getId());
        enrollment.check();
        MenteeReview review = menteeReviewService.createMenteeReview(user, enrollment.getId(), menteeReviewCreateRequest);
        assertEquals(1, menteeReviewRepository.findByLecture(lecture).size());

        // When
        menteeReviewService.deleteMenteeReview(user, review.getId());

        // Then
        assertEquals(0, menteeReviewRepository.findByLecture(lecture).size());

    }

    @WithAccount(NAME)
    @DisplayName("멘티 리뷰 삭제 - 멘토가 댓글을 단 경우")
    @Test
    void delete_menteeReview_withChildren() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lecture.getId(), lecturePrice1.getId());
        enrollment.check();
        MenteeReview parent = menteeReviewService.createMenteeReview(user, enrollment.getId(), menteeReviewCreateRequest);
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), parent.getId(), mentorReviewCreateRequest);

        // When
        menteeReviewService.deleteMenteeReview(user, parent.getId());

        // Then
        // children 삭제 체크
        List<MenteeReview> reviews = menteeReviewRepository.findByLecture(lecture);
        assertEquals(0, reviews.size());
        assertFalse(menteeReviewRepository.findById(parent.getId()).isPresent());
        assertFalse(mentorReviewRepository.findById(child.getId()).isPresent());
    }

}