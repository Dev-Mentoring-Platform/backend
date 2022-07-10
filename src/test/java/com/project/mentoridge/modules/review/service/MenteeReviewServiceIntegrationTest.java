package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.EnrollmentLogService;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.log.component.UserLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleEachLectureResponse;
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
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMenteeUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class MenteeReviewServiceIntegrationTest {

    @Autowired
    LoginService loginService;
    @Autowired
    UserLogService userLogService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
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
    SubjectRepository subjectRepository;

    private User mentorUser;
    private Mentor mentor;
    private User menteeUser;
    private Mentee mentee;

    private Lecture lecture1;
    private LecturePrice lecturePrice1;
    private LecturePrice lecturePrice2;

    private Lecture lecture2;
    private LecturePrice lecturePrice3;

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
        mentorUser.verifyEmail(userLogService);
        menteeRepository.save(Mentee.builder()
                .user(mentorUser)
                .build());
        mentor = mentorService.createMentor(mentorUser, mentorSignUpRequest);

        lecture1 = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecture1.approve(lectureLogService);

        lecture2 = lectureService.createLecture(mentorUser, LectureCreateRequest.builder()
                .title("제목2")
                .subTitle("소제목2")
                .introduce("소개2")
                .content("<p>본문2</p>")
                .difficulty(DifficultyType.ADVANCED)
                .systems(Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE))
                .lecturePrices(Arrays.asList(LectureCreateRequest.LecturePriceCreateRequest.builder()
                        .isGroup(false)
                        .pricePerHour(2000L)
                        .timePerLecture(5)
                        .numberOfLectures(5)
                        .totalPrice(2000L * 5 * 5)
                        .build()))
                .lectureSubjects(Arrays.asList(LectureCreateRequest.LectureSubjectCreateRequest.builder()
                        .subjectId(2L)
                        .build()))
                .thumbnail("https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c")
                .build());
        lecture2.approve(lectureLogService);

        lecturePrice1 = lecture1.getLecturePrices().get(0);
        lecturePrice2 = lecture1.getLecturePrices().get(1);
        lecturePrice3 = lecture2.getLecturePrices().get(0);
    }

    @DisplayName("강의 리뷰 리스트")
    @Test
    void get_paged_ReviewResponses_of_lecture() {

        // Given
        User menteeUser1 = saveMenteeUser(loginService);
        Mentee mentee1 = menteeRepository.findByUser(menteeUser1);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("menteeUser2@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("menteeUserName2")
                .gender(GenderType.MALE)
                .birthYear("1995")
                .phoneNumber("01033334444")
                .nickname("menteeUserNickname2")
                .build();
        User menteeUser2 = loginService.signUp(signUpRequest);
        menteeUser2.generateEmailVerifyToken();
        loginService.verifyEmail(menteeUser2.getUsername(), menteeUser2.getEmailVerifyToken());
        Mentee mentee2 = menteeRepository.findByUser(menteeUser2);

        Enrollment enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice1.getId());
        enrollment1.check(mentorUser, enrollmentLogService);
        Enrollment enrollment2 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice2.getId());
        enrollment2.check(mentorUser, enrollmentLogService);
        Enrollment enrollment3 = enrollmentService.createEnrollment(menteeUser2, lecture1.getId(), lecturePrice1.getId());
        enrollment3.check(mentorUser, enrollmentLogService);

        Enrollment enrollment4 = enrollmentService.createEnrollment(menteeUser1, lecture2.getId(), lecturePrice3.getId());
        enrollment4.check(mentorUser, enrollmentLogService);

        // lecture1
        MenteeReview menteeReview1 = MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee1)
                .enrollment(enrollment1)
                .lecture(lecture1)
                .build();
        MenteeReview menteeReview2 = MenteeReview.builder()
                .score(3)
                .content("별로에요")
                .mentee(mentee1)
                .enrollment(enrollment2)
                .lecture(lecture1)
                .build();
        MenteeReview menteeReview3 = MenteeReview.builder()
                .score(1)
                .content("싫어요")
                .mentee(mentee2)
                .enrollment(enrollment3)
                .lecture(lecture1)
                .build();
        menteeReviewRepository.saveAll(Arrays.asList(menteeReview1, menteeReview2, menteeReview3));

        // lecture2
        MenteeReview menteeReview4 = MenteeReview.builder()
                .score(1)
                .content("정말 싫어요")
                .mentee(mentee1)
                .enrollment(enrollment4)
                .lecture(lecture2)
                .build();
        MenteeReview _menteeReview4 = menteeReviewRepository.save(menteeReview4);
        MentorReview mentorReview4 = MentorReview.builder()
                .content("죄송합니다")
                .mentor(mentor)
                .parent(menteeReview4)
                .build();
        MentorReview _mentorReview4 = mentorReviewRepository.save(mentorReview4);

        // When
        // Then
        Page<ReviewResponse> reviews1 = menteeReviewService.getReviewResponsesOfLecture(lecture1.getId(), 1);
        assertThat(reviews1.getTotalElements()).isEqualTo(3L);

        Page<ReviewResponse> reviews2 = menteeReviewService.getReviewResponsesOfLecture(lecture2.getId(), 1);
        assertThat(reviews2.getTotalElements()).isEqualTo(1L);
        ReviewResponse reviewResponse4 = reviews2.getContent().get(0);
        assertAll(
                () -> assertThat(reviewResponse4.getMenteeReviewId()).isEqualTo(_menteeReview4.getId()),
                () -> assertThat(reviewResponse4.getEnrollmentId()).isEqualTo(_menteeReview4.getEnrollment().getId()),
                () -> assertThat(reviewResponse4.getScore()).isEqualTo(_menteeReview4.getScore()),
                () -> assertThat(reviewResponse4.getContent()).isEqualTo(_menteeReview4.getContent()),
                () -> assertThat(reviewResponse4.getUsername()).isEqualTo(_menteeReview4.getMentee().getUser().getUsername()),
                () -> assertThat(reviewResponse4.getUserNickname()).isEqualTo(_menteeReview4.getMentee().getUser().getNickname()),
                () -> assertThat(reviewResponse4.getUserImage()).isEqualTo(_menteeReview4.getMentee().getUser().getImage()),
                () -> assertThat(reviewResponse4.getCreatedAt()).isNotNull(),

                () -> assertThat(reviewResponse4.getChild().getMentorReviewId()).isEqualTo(_mentorReview4.getId()),
                () -> assertThat(reviewResponse4.getChild().getContent()).isEqualTo(_mentorReview4.getContent()),
                () -> assertThat(reviewResponse4.getChild().getUsername()).isEqualTo(_mentorReview4.getMentor().getUser().getUsername()),
                () -> assertThat(reviewResponse4.getChild().getUserNickname()).isEqualTo(_mentorReview4.getMentor().getUser().getNickname()),
                () -> assertThat(reviewResponse4.getChild().getUserImage()).isEqualTo(_mentorReview4.getMentor().getUser().getImage()),
                () -> assertThat(reviewResponse4.getChild().getCreatedAt()).isNotNull()
        );
    }

    @Test
    void get_ReviewResponse_of_lecture() {

        // Given
        User menteeUser1 = saveMenteeUser(loginService);
        Mentee mentee1 = menteeRepository.findByUser(menteeUser1);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("menteeUser2@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("menteeUserName2")
                .gender(GenderType.MALE)
                .birthYear("1995")
                .phoneNumber("01033334444")
                .nickname("menteeUserNickname2")
                .build();
        User menteeUser2 = loginService.signUp(signUpRequest);
        menteeUser2.generateEmailVerifyToken();
        loginService.verifyEmail(menteeUser2.getUsername(), menteeUser2.getEmailVerifyToken());
        Mentee mentee2 = menteeRepository.findByUser(menteeUser2);

        Enrollment enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice1.getId());
        enrollment1.check(mentorUser, enrollmentLogService);
        Enrollment enrollment2 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice2.getId());
        enrollment2.check(mentorUser, enrollmentLogService);
        Enrollment enrollment3 = enrollmentService.createEnrollment(menteeUser2, lecture1.getId(), lecturePrice1.getId());
        enrollment3.check(mentorUser, enrollmentLogService);

        // lecture1
        MenteeReview menteeReview1 = MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee1)
                .enrollment(enrollment1)
                .lecture(lecture1)
                .build();
        MenteeReview _menteeReview1 = menteeReviewRepository.save(menteeReview1);
        MenteeReview menteeReview2 = MenteeReview.builder()
                .score(3)
                .content("별로에요")
                .mentee(mentee1)
                .enrollment(enrollment2)
                .lecture(lecture1)
                .build();
        MenteeReview _menteeReview2 = menteeReviewRepository.save(menteeReview2);
        MenteeReview menteeReview3 = MenteeReview.builder()
                .score(1)
                .content("싫어요")
                .mentee(mentee2)
                .enrollment(enrollment3)
                .lecture(lecture1)
                .build();
        MenteeReview _menteeReview3 = menteeReviewRepository.save(menteeReview3);

        // When
        // Then
        ReviewResponse reviewResponse = menteeReviewService.getReviewResponseOfLecture(lecture1.getId(), _menteeReview2.getId());
        assertAll(
                () -> assertThat(reviewResponse.getMenteeReviewId()).isEqualTo(_menteeReview2.getId()),
                () -> assertThat(reviewResponse.getEnrollmentId()).isEqualTo(_menteeReview2.getEnrollment().getId()),
                () -> assertThat(reviewResponse.getScore()).isEqualTo(_menteeReview2.getScore()),
                () -> assertThat(reviewResponse.getContent()).isEqualTo(_menteeReview2.getContent()),
                () -> assertThat(reviewResponse.getUsername()).isEqualTo(_menteeReview2.getMentee().getUser().getUsername()),
                () -> assertThat(reviewResponse.getUserNickname()).isEqualTo(_menteeReview2.getMentee().getUser().getNickname()),
                () -> assertThat(reviewResponse.getUserImage()).isEqualTo(_menteeReview2.getMentee().getUser().getImage()),
                () -> assertThat(reviewResponse.getCreatedAt()).isNotNull(),

                () -> assertThat(reviewResponse.getChild()).isNull()
        );
    }

    @DisplayName("강의(가격별) 리뷰 리스트")
    @Test
    void get_paged_ReviewResponses_of_lecturePrice() {

        // Given
        User menteeUser1 = saveMenteeUser(loginService);
        Mentee mentee1 = menteeRepository.findByUser(menteeUser1);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("menteeUser2@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("menteeUserName2")
                .gender(GenderType.MALE)
                .birthYear("1995")
                .phoneNumber("01033334444")
                .nickname("menteeUserNickname2")
                .build();
        User menteeUser2 = loginService.signUp(signUpRequest);
        menteeUser2.generateEmailVerifyToken();
        loginService.verifyEmail(menteeUser2.getUsername(), menteeUser2.getEmailVerifyToken());
        Mentee mentee2 = menteeRepository.findByUser(menteeUser2);

        Enrollment enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice1.getId());
        enrollment1.check(mentorUser, enrollmentLogService);
        Enrollment enrollment2 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice2.getId());
        enrollment2.check(mentorUser, enrollmentLogService);
        Enrollment enrollment3 = enrollmentService.createEnrollment(menteeUser2, lecture1.getId(), lecturePrice1.getId());
        enrollment3.check(mentorUser, enrollmentLogService);

        // lecture1
        MenteeReview menteeReview1 = menteeReviewRepository.save(MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee1)
                .enrollment(enrollment1)
                .lecture(lecture1)
                .build());
        MenteeReview menteeReview2 = menteeReviewRepository.save(MenteeReview.builder()
                .score(3)
                .content("별로에요")
                .mentee(mentee1)
                .enrollment(enrollment2)
                .lecture(lecture1)
                .build());
        MenteeReview menteeReview3 = menteeReviewRepository.save(MenteeReview.builder()
                .score(1)
                .content("싫어요")
                .mentee(mentee2)
                .enrollment(enrollment3)
                .lecture(lecture1)
                .build());

        // When
        Page<ReviewResponse> reviewResponses = menteeReviewService.getReviewResponsesOfLecture(lecture1.getId(), lecturePrice1.getId(), 1);

        // Then
        assertThat(reviewResponses.getTotalElements()).isEqualTo(2L);

        List<Long> menteeReviewIds = reviewResponses.getContent().stream()
                .map(ReviewResponse::getMenteeReviewId).collect(Collectors.toList());
        assertThat(menteeReviewIds).containsExactly(menteeReview1.getId(), menteeReview3.getId());
    }

    @Test
    void get_ReviewResponse_of_lecturePrice() {

        // Given
        User menteeUser1 = saveMenteeUser(loginService);
        Mentee mentee1 = menteeRepository.findByUser(menteeUser1);

        Enrollment enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice1.getId());
        enrollment1.check(mentorUser, enrollmentLogService);

        MenteeReview menteeReview1 = menteeReviewRepository.save(MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee1)
                .enrollment(enrollment1)
                .lecture(lecture1)
                .build());
        MentorReview mentorReview1 = mentorReviewRepository.save(MentorReview.builder()
                .content("감사합니다")
                .mentor(mentor)
                .parent(menteeReview1)
                .build());

        // When
        ReviewResponse reviewResponse = menteeReviewService.getReviewResponseOfLecture(lecture1.getId(), lecturePrice1.getId(), menteeReview1.getId());
        // Then
        assertAll(
                () -> assertThat(reviewResponse.getMenteeReviewId()).isEqualTo(menteeReview1.getId()),
                () -> assertThat(reviewResponse.getEnrollmentId()).isEqualTo(menteeReview1.getEnrollment().getId()),
                () -> assertThat(reviewResponse.getScore()).isEqualTo(menteeReview1.getScore()),
                () -> assertThat(reviewResponse.getContent()).isEqualTo(menteeReview1.getContent()),
                () -> assertThat(reviewResponse.getUsername()).isEqualTo(menteeReview1.getMentee().getUser().getUsername()),
                () -> assertThat(reviewResponse.getUserNickname()).isEqualTo(menteeReview1.getMentee().getUser().getNickname()),
                () -> assertThat(reviewResponse.getUserImage()).isEqualTo(menteeReview1.getMentee().getUser().getImage()),
                () -> assertThat(reviewResponse.getCreatedAt()).isNotNull(),

                () -> assertThat(reviewResponse.getChild().getMentorReviewId()).isEqualTo(mentorReview1.getId()),
                () -> assertThat(reviewResponse.getChild().getContent()).isEqualTo(mentorReview1.getContent()),
                () -> assertThat(reviewResponse.getChild().getUsername()).isEqualTo(mentorReview1.getMentor().getUser().getUsername()),
                () -> assertThat(reviewResponse.getChild().getUserNickname()).isEqualTo(mentorReview1.getMentor().getUser().getNickname()),
                () -> assertThat(reviewResponse.getChild().getUserImage()).isEqualTo(mentorReview1.getMentor().getUser().getImage()),
                () -> assertThat(reviewResponse.getChild().getCreatedAt()).isNotNull()
        );
    }

    @Test
    void get_ReviewResponse_of_enrollment() {

        // Given
        User menteeUser1 = saveMenteeUser(loginService);
        Mentee mentee1 = menteeRepository.findByUser(menteeUser1);

        Enrollment enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice1.getId());
        enrollment1.check(mentorUser, enrollmentLogService);

        MenteeReview menteeReview1 = menteeReviewRepository.save(MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee1)
                .enrollment(enrollment1)
                .lecture(lecture1)
                .build());
        MentorReview mentorReview1 = mentorReviewRepository.save(MentorReview.builder()
                .content("감사합니다")
                .mentor(mentor)
                .parent(menteeReview1)
                .build());

        // When
        ReviewResponse reviewResponse = menteeReviewService.getReviewResponseOfEnrollment(mentee1.getId(), enrollment1.getId(), menteeReview1.getId());
        // Then
        assertAll(
                () -> assertThat(reviewResponse.getMenteeReviewId()).isEqualTo(menteeReview1.getId()),
                () -> assertThat(reviewResponse.getEnrollmentId()).isEqualTo(menteeReview1.getEnrollment().getId()),
                () -> assertThat(reviewResponse.getScore()).isEqualTo(menteeReview1.getScore()),
                () -> assertThat(reviewResponse.getContent()).isEqualTo(menteeReview1.getContent()),
                () -> assertThat(reviewResponse.getUsername()).isEqualTo(menteeReview1.getMentee().getUser().getUsername()),
                () -> assertThat(reviewResponse.getUserNickname()).isEqualTo(menteeReview1.getMentee().getUser().getNickname()),
                () -> assertThat(reviewResponse.getUserImage()).isEqualTo(menteeReview1.getMentee().getUser().getImage()),
                () -> assertThat(reviewResponse.getCreatedAt()).isNotNull(),

                () -> assertThat(reviewResponse.getChild().getMentorReviewId()).isEqualTo(mentorReview1.getId()),
                () -> assertThat(reviewResponse.getChild().getContent()).isEqualTo(mentorReview1.getContent()),
                () -> assertThat(reviewResponse.getChild().getUsername()).isEqualTo(mentorReview1.getMentor().getUser().getUsername()),
                () -> assertThat(reviewResponse.getChild().getUserNickname()).isEqualTo(mentorReview1.getMentor().getUser().getNickname()),
                () -> assertThat(reviewResponse.getChild().getUserImage()).isEqualTo(mentorReview1.getMentor().getUser().getImage()),
                () -> assertThat(reviewResponse.getChild().getCreatedAt()).isNotNull()
        );
    }

    @DisplayName("작성한 리뷰(+강의) 리스트 - 페이징")
    @Test
    void get_paged_ReviewResponses_with_SimpleEachLectureResponse() {

        // Given
        User menteeUser1 = saveMenteeUser(loginService);
        Mentee mentee1 = menteeRepository.findByUser(menteeUser1);

        Enrollment enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice1.getId());
        enrollment1.check(mentorUser, enrollmentLogService);
        Enrollment enrollment2 = enrollmentService.createEnrollment(menteeUser1, lecture2.getId(), lecturePrice3.getId());
        enrollment2.check(mentorUser, enrollmentLogService);
        
        MenteeReview menteeReview1 = menteeReviewRepository.save(MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee1)
                .enrollment(enrollment1)
                .lecture(lecture1)
                .build());
        MentorReview mentorReview1 = mentorReviewRepository.save(MentorReview.builder()
                .content("감사합니다")
                .mentor(mentor)
                .parent(menteeReview1)
                .build());

        MenteeReview menteeReview2 = menteeReviewRepository.save(MenteeReview.builder()
                .score(1)
                .content("별로에요")
                .mentee(mentee1)
                .enrollment(enrollment2)
                .lecture(lecture2)
                .build());

        // When
        Page<ReviewWithSimpleEachLectureResponse> reviews = menteeReviewService.getReviewWithSimpleEachLectureResponses(menteeUser1, 1);
        assertThat(reviews.getTotalElements()).isEqualTo(2L);
        // Then
        for (ReviewWithSimpleEachLectureResponse reviewResponse : reviews) {

            if (Objects.equals(reviewResponse.getMenteeReviewId(), menteeReview1.getId())) {

                assertAll(
                        () -> assertThat(reviewResponse.getMenteeReviewId()).isEqualTo(menteeReview1.getId()),
                        () -> assertThat(reviewResponse.getEnrollmentId()).isEqualTo(menteeReview1.getEnrollment().getId()),
                        () -> assertThat(reviewResponse.getScore()).isEqualTo(menteeReview1.getScore()),
                        () -> assertThat(reviewResponse.getContent()).isEqualTo(menteeReview1.getContent()),
                        () -> assertThat(reviewResponse.getUsername()).isEqualTo(menteeReview1.getMentee().getUser().getUsername()),
                        () -> assertThat(reviewResponse.getUserNickname()).isEqualTo(menteeReview1.getMentee().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getUserImage()).isEqualTo(menteeReview1.getMentee().getUser().getImage()),
                        () -> assertThat(reviewResponse.getCreatedAt()).isNotNull(),

                        () -> assertThat(reviewResponse.getChild().getMentorReviewId()).isEqualTo(mentorReview1.getId()),
                        () -> assertThat(reviewResponse.getChild().getContent()).isEqualTo(mentorReview1.getContent()),
                        () -> assertThat(reviewResponse.getChild().getUsername()).isEqualTo(mentorReview1.getMentor().getUser().getUsername()),
                        () -> assertThat(reviewResponse.getChild().getUserNickname()).isEqualTo(mentorReview1.getMentor().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getChild().getUserImage()).isEqualTo(mentorReview1.getMentor().getUser().getImage()),
                        () -> assertThat(reviewResponse.getChild().getCreatedAt()).isNotNull(),

                        // SimpleEachLectureResponse
                        () -> assertThat(reviewResponse.getLecture().getId()).isEqualTo(lecture1.getId()),
                        () -> assertThat(reviewResponse.getLecture().getTitle()).isEqualTo(lecture1.getTitle()),
                        () -> assertThat(reviewResponse.getLecture().getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                        () -> assertThat(reviewResponse.getLecture().getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                        () -> assertThat(reviewResponse.getLecture().getDifficulty()).isEqualTo(lecture1.getDifficulty()),

                        () -> assertThat(reviewResponse.getLecture().getSystems().size()).isEqualTo(lecture1.getSystems().size()),

                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().isGroup()).isEqualTo(lecturePrice1.isGroup()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().isClosed()).isEqualTo(lecturePrice1.isClosed()),

                        () -> assertThat(reviewResponse.getLecture().getLectureSubjects().size()).isEqualTo(lecture1.getLectureSubjects().size()),

                        () -> assertThat(reviewResponse.getLecture().getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                        () -> assertThat(reviewResponse.getLecture().getMentorNickname()).isEqualTo(lecture1.getMentor().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getLecture().getScoreAverage()).isNull(),
                        () -> assertThat(reviewResponse.getLecture().getPickCount()).isNull()
                );

            } else if (Objects.equals(reviewResponse.getMenteeReviewId(), menteeReview2.getId())) {

                assertAll(
                        () -> assertThat(reviewResponse.getMenteeReviewId()).isEqualTo(menteeReview2.getId()),
                        () -> assertThat(reviewResponse.getEnrollmentId()).isEqualTo(menteeReview2.getEnrollment().getId()),
                        () -> assertThat(reviewResponse.getScore()).isEqualTo(menteeReview2.getScore()),
                        () -> assertThat(reviewResponse.getContent()).isEqualTo(menteeReview2.getContent()),
                        () -> assertThat(reviewResponse.getUsername()).isEqualTo(menteeReview2.getMentee().getUser().getUsername()),
                        () -> assertThat(reviewResponse.getUserNickname()).isEqualTo(menteeReview2.getMentee().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getUserImage()).isEqualTo(menteeReview2.getMentee().getUser().getImage()),
                        () -> assertThat(reviewResponse.getCreatedAt()).isNotNull(),

                        () -> assertThat(reviewResponse.getChild()).isNull(),

                        // SimpleEachLectureResponse
                        () -> assertThat(reviewResponse.getLecture().getId()).isEqualTo(lecture2.getId()),
                        () -> assertThat(reviewResponse.getLecture().getTitle()).isEqualTo(lecture2.getTitle()),
                        () -> assertThat(reviewResponse.getLecture().getSubTitle()).isEqualTo(lecture2.getSubTitle()),
                        () -> assertThat(reviewResponse.getLecture().getIntroduce()).isEqualTo(lecture2.getIntroduce()),
                        () -> assertThat(reviewResponse.getLecture().getDifficulty()).isEqualTo(lecture2.getDifficulty()),

                        () -> assertThat(reviewResponse.getLecture().getSystems().size()).isEqualTo(lecture2.getSystems().size()),

                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice3.getId()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().isGroup()).isEqualTo(lecturePrice3.isGroup()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice3.getNumberOfMembers()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice3.getPricePerHour()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice3.getTimePerLecture()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice3.getNumberOfLectures()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice3.getTotalPrice()),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice3.isGroup() ? "그룹강의" : "1:1 개인강의"),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice3.getPricePerHour(), lecturePrice3.getTimePerLecture(), lecturePrice3.getNumberOfLectures())),
                        () -> assertThat(reviewResponse.getLecture().getLecturePrice().isClosed()).isEqualTo(lecturePrice3.isClosed()),

                        () -> assertThat(reviewResponse.getLecture().getLectureSubjects().size()).isEqualTo(lecture2.getLectureSubjects().size()),

                        () -> assertThat(reviewResponse.getLecture().getThumbnail()).isEqualTo(lecture2.getThumbnail()),
                        () -> assertThat(reviewResponse.getLecture().getMentorNickname()).isEqualTo(lecture2.getMentor().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getLecture().getScoreAverage()).isNull(),
                        () -> assertThat(reviewResponse.getLecture().getPickCount()).isNull()
                );
            }
        }
    }

    @Test
    void get_ReviewResponse_by_menteeReviewId() {

        // Given
        User menteeUser1 = saveMenteeUser(loginService);
        Mentee mentee1 = menteeRepository.findByUser(menteeUser1);

        Enrollment enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice1.getId());
        enrollment1.check(mentorUser, enrollmentLogService);

        MenteeReview menteeReview1 = menteeReviewRepository.save(MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee1)
                .enrollment(enrollment1)
                .lecture(lecture1)
                .build());
        MentorReview mentorReview1 = mentorReviewRepository.save(MentorReview.builder()
                .content("감사합니다")
                .mentor(mentor)
                .parent(menteeReview1)
                .build());

        // When
        ReviewResponse reviewResponse = menteeReviewService.getReviewResponse(menteeReview1.getId());
        // Then
        assertAll(
                () -> assertThat(reviewResponse.getMenteeReviewId()).isEqualTo(menteeReview1.getId()),
                () -> assertThat(reviewResponse.getEnrollmentId()).isEqualTo(menteeReview1.getEnrollment().getId()),
                () -> assertThat(reviewResponse.getScore()).isEqualTo(menteeReview1.getScore()),
                () -> assertThat(reviewResponse.getContent()).isEqualTo(menteeReview1.getContent()),
                () -> assertThat(reviewResponse.getUsername()).isEqualTo(menteeReview1.getMentee().getUser().getUsername()),
                () -> assertThat(reviewResponse.getUserNickname()).isEqualTo(menteeReview1.getMentee().getUser().getNickname()),
                () -> assertThat(reviewResponse.getUserImage()).isEqualTo(menteeReview1.getMentee().getUser().getImage()),
                () -> assertThat(reviewResponse.getCreatedAt()).isNotNull(),

                () -> assertThat(reviewResponse.getChild().getMentorReviewId()).isEqualTo(mentorReview1.getId()),
                () -> assertThat(reviewResponse.getChild().getContent()).isEqualTo(mentorReview1.getContent()),
                () -> assertThat(reviewResponse.getChild().getUsername()).isEqualTo(mentorReview1.getMentor().getUser().getUsername()),
                () -> assertThat(reviewResponse.getChild().getUserNickname()).isEqualTo(mentorReview1.getMentor().getUser().getNickname()),
                () -> assertThat(reviewResponse.getChild().getUserImage()).isEqualTo(mentorReview1.getMentor().getUser().getImage()),
                () -> assertThat(reviewResponse.getChild().getCreatedAt()).isNotNull()
        );
    }

    @Test
    void get_ReviewResponse_with_SimpleEachLectureResponse() {

        // Given
        User menteeUser1 = saveMenteeUser(loginService);
        Mentee mentee1 = menteeRepository.findByUser(menteeUser1);

        Enrollment enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture1.getId(), lecturePrice1.getId());
        enrollment1.check(mentorUser, enrollmentLogService);

        MenteeReview menteeReview1 = menteeReviewRepository.save(MenteeReview.builder()
                .score(5)
                .content("좋아요")
                .mentee(mentee1)
                .enrollment(enrollment1)
                .lecture(lecture1)
                .build());
        MentorReview mentorReview1 = mentorReviewRepository.save(MentorReview.builder()
                .content("감사합니다")
                .mentor(mentor)
                .parent(menteeReview1)
                .build());

        // When
        ReviewWithSimpleEachLectureResponse reviewResponse = menteeReviewService.getReviewWithSimpleEachLectureResponse(menteeReview1.getId());
        // Then
        assertAll(
                () -> assertThat(reviewResponse.getMenteeReviewId()).isEqualTo(menteeReview1.getId()),
                () -> assertThat(reviewResponse.getEnrollmentId()).isEqualTo(menteeReview1.getEnrollment().getId()),
                () -> assertThat(reviewResponse.getScore()).isEqualTo(menteeReview1.getScore()),
                () -> assertThat(reviewResponse.getContent()).isEqualTo(menteeReview1.getContent()),
                () -> assertThat(reviewResponse.getUsername()).isEqualTo(menteeReview1.getMentee().getUser().getUsername()),
                () -> assertThat(reviewResponse.getUserNickname()).isEqualTo(menteeReview1.getMentee().getUser().getNickname()),
                () -> assertThat(reviewResponse.getUserImage()).isEqualTo(menteeReview1.getMentee().getUser().getImage()),
                () -> assertThat(reviewResponse.getCreatedAt()).isNotNull(),

                () -> assertThat(reviewResponse.getChild().getMentorReviewId()).isEqualTo(mentorReview1.getId()),
                () -> assertThat(reviewResponse.getChild().getContent()).isEqualTo(mentorReview1.getContent()),
                () -> assertThat(reviewResponse.getChild().getUsername()).isEqualTo(mentorReview1.getMentor().getUser().getUsername()),
                () -> assertThat(reviewResponse.getChild().getUserNickname()).isEqualTo(mentorReview1.getMentor().getUser().getNickname()),
                () -> assertThat(reviewResponse.getChild().getUserImage()).isEqualTo(mentorReview1.getMentor().getUser().getImage()),
                () -> assertThat(reviewResponse.getChild().getCreatedAt()).isNotNull(),

                // SimpleEachLectureResponse
                () -> assertThat(reviewResponse.getLecture().getId()).isEqualTo(lecture1.getId()),
                () -> assertThat(reviewResponse.getLecture().getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(reviewResponse.getLecture().getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(reviewResponse.getLecture().getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(reviewResponse.getLecture().getDifficulty()).isEqualTo(lecture1.getDifficulty()),

                () -> assertThat(reviewResponse.getLecture().getSystems().size()).isEqualTo(lecture1.getSystems().size()),

                () -> assertThat(reviewResponse.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(reviewResponse.getLecture().getLecturePrice().isGroup()).isEqualTo(lecturePrice1.isGroup()),
                () -> assertThat(reviewResponse.getLecture().getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(reviewResponse.getLecture().getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(reviewResponse.getLecture().getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(reviewResponse.getLecture().getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(reviewResponse.getLecture().getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(reviewResponse.getLecture().getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(reviewResponse.getLecture().getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(reviewResponse.getLecture().getLecturePrice().isClosed()).isEqualTo(lecturePrice1.isClosed()),

                () -> assertThat(reviewResponse.getLecture().getLectureSubjects().size()).isEqualTo(lecture1.getLectureSubjects().size()),

                () -> assertThat(reviewResponse.getLecture().getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(reviewResponse.getLecture().getMentorNickname()).isEqualTo(lecture1.getMentor().getUser().getNickname()),
                () -> assertThat(reviewResponse.getLecture().getScoreAverage()).isNull(),
                () -> assertThat(reviewResponse.getLecture().getPickCount()).isNull()
        );
    }

    @DisplayName("멘티 리뷰 등록 - 확인된 등록이 아닌 경우")
    @Test
    void create_menteeReview_when_not_checked_enrollment() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture1.getId(), lecturePrice1.getId());
        assertEquals(1, enrollmentRepository.findByMentee(mentee).size());

        // When
        // Then
        assertThrows(RuntimeException.class,
                () -> menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), menteeReviewCreateRequest)
        );
    }
    
    @DisplayName("멘티 리뷰 등록")
    @Test
    void create_menteeReview() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture1.getId(), lecturePrice1.getId());
        assertEquals(1, enrollmentRepository.findByMentee(mentee).size());
        enrollment.check(mentorUser, enrollmentLogService);

        // When
        menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), menteeReviewCreateRequest);

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

    @DisplayName("멘티 리뷰 등록 - 수강 강의가 아닌 경우")
    @Test
    void create_menteeReview_unEnrolled() {

        // Given
        // When
        // Then
        assertThrows(EntityNotFoundException.class, () -> {
            menteeReviewService.createMenteeReview(menteeUser, 1000L, menteeReviewCreateRequest);
        });
    }

    @DisplayName("멘티 리뷰 수정")
    @Test
    void update_menteeReview() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture1.getId(), lecturePrice1.getId());
        enrollment.check(mentorUser, enrollmentLogService);
        MenteeReview review = menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), menteeReviewCreateRequest);

        // When
        menteeReviewService.updateMenteeReview(menteeUser, review.getId(), menteeReviewUpdateRequest);

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

    @DisplayName("멘티 리뷰 삭제")
    @Test
    void delete_menteeReview() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture1.getId(), lecturePrice1.getId());
        enrollment.check(mentorUser, enrollmentLogService);
        MenteeReview review = menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), menteeReviewCreateRequest);
        assertEquals(1, menteeReviewRepository.findByLecture(lecture1).size());

        // When
        menteeReviewService.deleteMenteeReview(menteeUser, review.getId());

        // Then
        assertEquals(0, menteeReviewRepository.findByLecture(lecture1).size());

    }

    @DisplayName("멘티 리뷰 삭제 - 멘토가 댓글을 단 경우")
    @Test
    void delete_menteeReview_withChildren() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture1.getId(), lecturePrice1.getId());
        enrollment.check(mentorUser, enrollmentLogService);
        MenteeReview parent = menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), menteeReviewCreateRequest);
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lecture1.getId(), parent.getId(), mentorReviewCreateRequest);

        // When
        menteeReviewService.deleteMenteeReview(menteeUser, parent.getId());

        // Then
        // children 삭제 체크
        List<MenteeReview> reviews = menteeReviewRepository.findByLecture(lecture1);
        assertEquals(0, reviews.size());
        assertFalse(menteeReviewRepository.findById(parent.getId()).isPresent());
        assertFalse(mentorReviewRepository.findById(child.getId()).isPresent());
    }

}