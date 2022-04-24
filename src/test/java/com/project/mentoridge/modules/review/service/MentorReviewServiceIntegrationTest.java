package com.project.mentoridge.modules.review.service;

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

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MentorReviewServiceIntegrationTest {

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
    private Long lectureId;

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
        lectureId = lecture.getId();
    }

    @WithAccount(NAME)
    @DisplayName("멘토 리뷰 등록")
    @Test
    void create_mentorReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lectureId, lecturePrice1.getId());
        enrollment.check();
        MenteeReview parent = menteeReviewService.createMenteeReview(user, enrollment.getId(), menteeReviewCreateRequest);

        // When
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lectureId, parent.getId(), mentorReviewCreateRequest);

        // Then
        MentorReview review = mentorReviewRepository.findByParentAndId(parent, child.getId()).orElse(null);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(parent, review.getParent()),
                () -> assertEquals(child, review),
                () -> assertEquals(mentorReviewCreateRequest.getContent(), review.getContent()),
                () -> assertEquals(lecture, review.getLecture())
        );
    }

    @WithAccount(NAME)
    @DisplayName("멘토 리뷰 수정")
    @Test
    void update_mentorReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lectureId, lecturePrice1.getId());
        enrollment.check();
        MenteeReview parent = menteeReviewService.createMenteeReview(user, enrollment.getId(), menteeReviewCreateRequest);
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lectureId, parent.getId(), mentorReviewCreateRequest);

        // When
        mentorReviewService.updateMentorReview(mentorUser, lectureId, parent.getId(), child.getId(), mentorReviewUpdateRequest);

        // Then
        MentorReview review = mentorReviewRepository.findByParentAndId(parent, child.getId()).orElse(null);
        assertNotNull(review);
        assertAll(
                () -> assertEquals(parent, review.getParent()),
                () -> assertEquals(child, review),
                () -> assertEquals(mentorReviewUpdateRequest.getContent(), review.getContent()),
                () -> assertEquals(lecture, review.getLecture())
        );

    }

    @WithAccount(NAME)
    @DisplayName("멘토 리뷰 삭제")
    @Test
    void delete_mentorReview() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);
        Enrollment enrollment = enrollmentService.createEnrollment(user, lectureId, lecturePrice1.getId());
        enrollment.check();
        MenteeReview parent = menteeReviewService.createMenteeReview(user, enrollment.getId(), menteeReviewCreateRequest);
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lectureId, parent.getId(), mentorReviewCreateRequest);

        // When
        mentorReviewService.deleteMentorReview(mentorUser, lectureId, parent.getId(), child.getId());

        // Then
        assertTrue(menteeReviewRepository.findById(parent.getId()).isPresent());
        assertEquals(menteeReviewRepository.findByEnrollment(enrollment), menteeReviewRepository.findByLecture(lecture).get(0));

        parent = menteeReviewRepository.findByLecture(lecture).get(0);
        assertEquals(0, parent.getChildren().size());
        assertFalse(mentorReviewRepository.findById(child.getId()).isPresent());
    }
}