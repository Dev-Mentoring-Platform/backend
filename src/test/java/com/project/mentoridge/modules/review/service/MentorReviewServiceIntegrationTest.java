package com.project.mentoridge.modules.review.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
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
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewListResponse;
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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Objects;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class MentorReviewServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MentorReviewService mentorReviewService;
    @Autowired
    MentorReviewRepository mentorReviewRepository;
    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;

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
    SubjectRepository subjectRepository;

    private User mentorUser;
    private Mentor mentor;

    private User menteeUser1;
    private Mentee mentee1;
    private User menteeUser2;
    private Mentee mentee2;

    private Lecture lecture;
    // private Long lectureId;
    private LecturePrice lecturePrice1;

    private Enrollment enrollment1;
    private MenteeReview parent1;

    private Enrollment enrollment2;
    private MenteeReview parent2;

    @BeforeEach
    @Override
    protected void init() {

        initDatabase();

        // subject
        // if (subjectRepository.count() == 0) {
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
        // }

        mentorUser = loginService.signUp(getSignUpRequestWithNameAndNickname("mentor", "mentor"));
        mentorUser.verifyEmail(userLogService);
        menteeRepository.save(Mentee.builder()
                .user(mentorUser)
                .build());
        mentor = mentorService.createMentor(mentorUser, mentorSignUpRequest);

        lecture = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecture.approve(lectureLogService);
        // lectureId = lecture.getId();
        lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);

        menteeUser1 = saveMenteeUser(loginService);
        mentee1 = menteeRepository.findByUser(menteeUser1);

        enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture.getId(), lecturePrice1.getId());
        enrollment1.check(mentorUser, enrollmentLogService);
        parent1 = menteeReviewService.createMenteeReview(menteeUser1, enrollment1.getId(), menteeReviewCreateRequest);

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
        menteeUser2 = loginService.signUp(signUpRequest);
        loginService.verifyEmail(menteeUser2.getUsername(), menteeUser2.getEmailVerifyToken());
        mentee2 = menteeRepository.findByUser(menteeUser2);

        enrollment2 = saveEnrollment(enrollmentService, menteeUser2, lecture, lecturePrice1);
        enrollmentService.check(mentorUser, enrollment2.getId());
        MenteeReviewCreateRequest menteeReviewCreateRequest2 = MenteeReviewCreateRequest.builder()
                .score(1)
                .content("Bad")
                .build();
        parent2 = menteeReviewService.createMenteeReview(menteeUser2, enrollment2.getId(), menteeReviewCreateRequest2);
    }

    @DisplayName("내 멘티가 작성한 내 리뷰 리스트 - 페이징")
    @Test
    void get_paged_ReviewWithSimpleEachLectureResponses_of_mentor() {

        // Given
        MentorReview mentorReview1 = mentorReviewRepository.save(MentorReview.builder()
                .content("good")
                .mentor(mentor)
                .parent(parent1)
                .build());
        MentorReview mentorReview2 = mentorReviewRepository.save(MentorReview.builder()
                .content("sorry")
                .mentor(mentor)
                .parent(parent2)
                .build());

        // When
        Page<ReviewWithSimpleEachLectureResponse> reviewResponses = mentorReviewService.getReviewWithSimpleEachLectureResponsesOfMentorByMentees(mentorUser, 1);
        // Then
        assertThat(reviewResponses.getTotalElements()).isEqualTo(2L);
        for (ReviewWithSimpleEachLectureResponse reviewResponse : reviewResponses) {

            if (Objects.equals(reviewResponse.getMenteeReviewId(), parent1.getId())) {

                assertAll(
                        () -> assertThat(reviewResponse.getMenteeReviewId()).isEqualTo(parent1.getId()),
                        () -> assertThat(reviewResponse.getEnrollmentId()).isEqualTo(parent1.getEnrollment().getId()),
                        () -> assertThat(reviewResponse.getScore()).isEqualTo(parent1.getScore()),
                        () -> assertThat(reviewResponse.getContent()).isEqualTo(parent1.getContent()),
                        () -> assertThat(reviewResponse.getUsername()).isEqualTo(parent1.getMentee().getUser().getUsername()),
                        () -> assertThat(reviewResponse.getUserNickname()).isEqualTo(parent1.getMentee().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getUserImage()).isEqualTo(parent1.getMentee().getUser().getImage()),
                        () -> assertThat(reviewResponse.getCreatedAt()).isNotNull(),

                        () -> assertThat(reviewResponse.getChild().getMentorReviewId()).isEqualTo(mentorReview1.getId()),
                        () -> assertThat(reviewResponse.getChild().getContent()).isEqualTo(mentorReview1.getContent()),
                        () -> assertThat(reviewResponse.getChild().getUsername()).isEqualTo(mentorReview1.getMentor().getUser().getUsername()),
                        () -> assertThat(reviewResponse.getChild().getUserNickname()).isEqualTo(mentorReview1.getMentor().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getChild().getUserImage()).isEqualTo(mentorReview1.getMentor().getUser().getImage()),
                        () -> assertThat(reviewResponse.getChild().getCreatedAt()).isNotNull(),

                        // SimpleEachLectureResponse
                        () -> assertThat(reviewResponse.getLecture().getId()).isEqualTo(lecture.getId()),
                        () -> assertThat(reviewResponse.getLecture().getTitle()).isEqualTo(lecture.getTitle()),
                        () -> assertThat(reviewResponse.getLecture().getSubTitle()).isEqualTo(lecture.getSubTitle()),
                        () -> assertThat(reviewResponse.getLecture().getIntroduce()).isEqualTo(lecture.getIntroduce()),
                        () -> assertThat(reviewResponse.getLecture().getDifficulty()).isEqualTo(lecture.getDifficulty()),

                        () -> assertThat(reviewResponse.getLecture().getSystems().size()).isEqualTo(lecture.getSystems().size()),

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

                        () -> assertThat(reviewResponse.getLecture().getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),

                        () -> assertThat(reviewResponse.getLecture().getThumbnail()).isEqualTo(lecture.getThumbnail()),
                        () -> assertThat(reviewResponse.getLecture().getMentorNickname()).isEqualTo(lecture.getMentor().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getLecture().getScoreAverage()).isNull(),
                        () -> assertThat(reviewResponse.getLecture().getPickCount()).isNull()
                );

            } else if (Objects.equals(reviewResponse.getMenteeReviewId(), parent2.getId())) {

                assertAll(
                        () -> assertThat(reviewResponse.getMenteeReviewId()).isEqualTo(parent2.getId()),
                        () -> assertThat(reviewResponse.getEnrollmentId()).isEqualTo(parent2.getEnrollment().getId()),
                        () -> assertThat(reviewResponse.getScore()).isEqualTo(parent2.getScore()),
                        () -> assertThat(reviewResponse.getContent()).isEqualTo(parent2.getContent()),
                        () -> assertThat(reviewResponse.getUsername()).isEqualTo(parent2.getMentee().getUser().getUsername()),
                        () -> assertThat(reviewResponse.getUserNickname()).isEqualTo(parent2.getMentee().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getUserImage()).isEqualTo(parent2.getMentee().getUser().getImage()),
                        () -> assertThat(reviewResponse.getCreatedAt()).isNotNull(),

                        () -> assertThat(reviewResponse.getChild().getMentorReviewId()).isEqualTo(mentorReview2.getId()),
                        () -> assertThat(reviewResponse.getChild().getContent()).isEqualTo(mentorReview2.getContent()),
                        () -> assertThat(reviewResponse.getChild().getUsername()).isEqualTo(mentorReview2.getMentor().getUser().getUsername()),
                        () -> assertThat(reviewResponse.getChild().getUserNickname()).isEqualTo(mentorReview2.getMentor().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getChild().getUserImage()).isEqualTo(mentorReview2.getMentor().getUser().getImage()),
                        () -> assertThat(reviewResponse.getChild().getCreatedAt()).isNotNull(),

                        // SimpleEachLectureResponse
                        () -> assertThat(reviewResponse.getLecture().getId()).isEqualTo(lecture.getId()),
                        () -> assertThat(reviewResponse.getLecture().getTitle()).isEqualTo(lecture.getTitle()),
                        () -> assertThat(reviewResponse.getLecture().getSubTitle()).isEqualTo(lecture.getSubTitle()),
                        () -> assertThat(reviewResponse.getLecture().getIntroduce()).isEqualTo(lecture.getIntroduce()),
                        () -> assertThat(reviewResponse.getLecture().getDifficulty()).isEqualTo(lecture.getDifficulty()),

                        () -> assertThat(reviewResponse.getLecture().getSystems().size()).isEqualTo(lecture.getSystems().size()),

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

                        () -> assertThat(reviewResponse.getLecture().getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),

                        () -> assertThat(reviewResponse.getLecture().getThumbnail()).isEqualTo(lecture.getThumbnail()),
                        () -> assertThat(reviewResponse.getLecture().getMentorNickname()).isEqualTo(lecture.getMentor().getUser().getNickname()),
                        () -> assertThat(reviewResponse.getLecture().getScoreAverage()).isNull(),
                        () -> assertThat(reviewResponse.getLecture().getPickCount()).isNull()
                );
            }
        }
    }

    @Test
    void get_ReviewWithSimpleEachLectureResponses_of_mentor() {

        // Given
        MentorReview mentorReview1 = mentorReviewRepository.save(MentorReview.builder()
                .content("good")
                .mentor(mentor)
                .parent(parent1)
                .build());
        MentorReview mentorReview2 = mentorReviewRepository.save(MentorReview.builder()
                .content("sorry")
                .mentor(mentor)
                .parent(parent2)
                .build());

        // When
        ReviewListResponse reviewResponse = mentorReviewService.getReviewWithSimpleEachLectureResponsesOfMentorByMentees(mentor.getId(), 1);
        // Then
        assertThat(reviewResponse.getScoreAverage()).isEqualTo(3.0);
        assertThat(reviewResponse.getReviewCount()).isEqualTo(2L);
        assertThat(reviewResponse.getReviews()).hasSize(2);
        for (ReviewWithSimpleEachLectureResponse review : reviewResponse.getReviews()) {

            if (Objects.equals(review.getMenteeReviewId(), parent1.getId())) {

                assertAll(
                        () -> assertThat(review.getMenteeReviewId()).isEqualTo(parent1.getId()),
                        () -> assertThat(review.getEnrollmentId()).isEqualTo(parent1.getEnrollment().getId()),
                        () -> assertThat(review.getScore()).isEqualTo(parent1.getScore()),
                        () -> assertThat(review.getContent()).isEqualTo(parent1.getContent()),
                        () -> assertThat(review.getUsername()).isEqualTo(parent1.getMentee().getUser().getUsername()),
                        () -> assertThat(review.getUserNickname()).isEqualTo(parent1.getMentee().getUser().getNickname()),
                        () -> assertThat(review.getUserImage()).isEqualTo(parent1.getMentee().getUser().getImage()),
                        () -> assertThat(review.getCreatedAt()).isNotNull(),

                        () -> assertThat(review.getChild().getMentorReviewId()).isEqualTo(mentorReview1.getId()),
                        () -> assertThat(review.getChild().getContent()).isEqualTo(mentorReview1.getContent()),
                        () -> assertThat(review.getChild().getUsername()).isEqualTo(mentorReview1.getMentor().getUser().getUsername()),
                        () -> assertThat(review.getChild().getUserNickname()).isEqualTo(mentorReview1.getMentor().getUser().getNickname()),
                        () -> assertThat(review.getChild().getUserImage()).isEqualTo(mentorReview1.getMentor().getUser().getImage()),
                        () -> assertThat(review.getChild().getCreatedAt()).isNotNull(),

                        // SimpleEachLectureResponse
                        () -> assertThat(review.getLecture().getLectureId()).isEqualTo(lecture.getId()),
                        () -> assertThat(review.getLecture().getTitle()).isEqualTo(lecture.getTitle()),
                        () -> assertThat(review.getLecture().getSubTitle()).isEqualTo(lecture.getSubTitle()),
                        () -> assertThat(review.getLecture().getIntroduce()).isEqualTo(lecture.getIntroduce()),
                        () -> assertThat(review.getLecture().getDifficulty()).isEqualTo(lecture.getDifficulty()),

                        () -> assertThat(review.getLecture().getSystems().size()).isEqualTo(lecture.getSystems().size()),

                        () -> assertThat(review.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                        () -> assertThat(review.getLecture().getLecturePrice().isGroup()).isEqualTo(lecturePrice1.isGroup()),
                        () -> assertThat(review.getLecture().getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                        () -> assertThat(review.getLecture().getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                        () -> assertThat(review.getLecture().getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                        () -> assertThat(review.getLecture().getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                        () -> assertThat(review.getLecture().getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                        () -> assertThat(review.getLecture().getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"),
                        () -> assertThat(review.getLecture().getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                        () -> assertThat(review.getLecture().getLecturePrice().isClosed()).isEqualTo(lecturePrice1.isClosed()),

                        () -> assertThat(review.getLecture().getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),

                        () -> assertThat(review.getLecture().getThumbnail()).isEqualTo(lecture.getThumbnail()),
                        () -> assertThat(review.getLecture().getMentorNickname()).isEqualTo(lecture.getMentor().getUser().getNickname()),
                        () -> assertThat(review.getLecture().getScoreAverage()).isNull(),
                        () -> assertThat(review.getLecture().getPickCount()).isNull()
                );

            } else if (Objects.equals(review.getMenteeReviewId(), parent2.getId())) {

                assertAll(
                        () -> assertThat(review.getMenteeReviewId()).isEqualTo(parent2.getId()),
                        () -> assertThat(review.getEnrollmentId()).isEqualTo(parent2.getEnrollment().getId()),
                        () -> assertThat(review.getScore()).isEqualTo(parent2.getScore()),
                        () -> assertThat(review.getContent()).isEqualTo(parent2.getContent()),
                        () -> assertThat(review.getUsername()).isEqualTo(parent2.getMentee().getUser().getUsername()),
                        () -> assertThat(review.getUserNickname()).isEqualTo(parent2.getMentee().getUser().getNickname()),
                        () -> assertThat(review.getUserImage()).isEqualTo(parent2.getMentee().getUser().getImage()),
                        () -> assertThat(review.getCreatedAt()).isNotNull(),

                        () -> assertThat(review.getChild().getMentorReviewId()).isEqualTo(mentorReview2.getId()),
                        () -> assertThat(review.getChild().getContent()).isEqualTo(mentorReview2.getContent()),
                        () -> assertThat(review.getChild().getUsername()).isEqualTo(mentorReview2.getMentor().getUser().getUsername()),
                        () -> assertThat(review.getChild().getUserNickname()).isEqualTo(mentorReview2.getMentor().getUser().getNickname()),
                        () -> assertThat(review.getChild().getUserImage()).isEqualTo(mentorReview2.getMentor().getUser().getImage()),
                        () -> assertThat(review.getChild().getCreatedAt()).isNotNull(),

                        // SimpleEachLectureResponse
                        () -> assertThat(review.getLecture().getLectureId()).isEqualTo(lecture.getId()),
                        () -> assertThat(review.getLecture().getTitle()).isEqualTo(lecture.getTitle()),
                        () -> assertThat(review.getLecture().getSubTitle()).isEqualTo(lecture.getSubTitle()),
                        () -> assertThat(review.getLecture().getIntroduce()).isEqualTo(lecture.getIntroduce()),
                        () -> assertThat(review.getLecture().getDifficulty()).isEqualTo(lecture.getDifficulty()),

                        () -> assertThat(review.getLecture().getSystems().size()).isEqualTo(lecture.getSystems().size()),

                        () -> assertThat(review.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                        () -> assertThat(review.getLecture().getLecturePrice().isGroup()).isEqualTo(lecturePrice1.isGroup()),
                        () -> assertThat(review.getLecture().getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                        () -> assertThat(review.getLecture().getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                        () -> assertThat(review.getLecture().getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                        () -> assertThat(review.getLecture().getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                        () -> assertThat(review.getLecture().getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                        () -> assertThat(review.getLecture().getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"),
                        () -> assertThat(review.getLecture().getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                        () -> assertThat(review.getLecture().getLecturePrice().isClosed()).isEqualTo(lecturePrice1.isClosed()),

                        () -> assertThat(review.getLecture().getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),

                        () -> assertThat(review.getLecture().getThumbnail()).isEqualTo(lecture.getThumbnail()),
                        () -> assertThat(review.getLecture().getMentorNickname()).isEqualTo(lecture.getMentor().getUser().getNickname()),
                        () -> assertThat(review.getLecture().getScoreAverage()).isNull(),
                        () -> assertThat(review.getLecture().getPickCount()).isNull()
                );
            }
        }
    }

    @DisplayName("멘토 리뷰 등록")
    @Test
    void create_mentorReview() {

        // Given
        // When
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), parent1.getId(), mentorReviewCreateRequest);

        // Then
        MentorReview review = mentorReviewRepository.findByParentAndId(parent1, child.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(parent1, review.getParent()),
                () -> assertEquals(child, review),
                () -> assertEquals(mentorReviewCreateRequest.getContent(), review.getContent())
        );
    }

    @DisplayName("멘토 리뷰 수정")
    @Test
    void update_mentorReview() {

        // Given
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), parent1.getId(), mentorReviewCreateRequest);

        // When
        mentorReviewService.updateMentorReview(mentorUser, lecture.getId(), parent1.getId(), child.getId(), mentorReviewUpdateRequest);

        // Then
        MentorReview review = mentorReviewRepository.findByParentAndId(parent1, child.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(parent1, review.getParent()),
                () -> assertEquals(child, review),
                () -> assertEquals(mentorReviewUpdateRequest.getContent(), review.getContent())
        );

    }

    @DisplayName("멘토 리뷰 삭제")
    @Test
    void delete_mentorReview() {

        // Given
        MentorReview child = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), parent1.getId(), mentorReviewCreateRequest);

        // When
        mentorReviewService.deleteMentorReview(mentorUser, lecture.getId(), parent1.getId(), child.getId());

        // Then
        assertTrue(menteeReviewRepository.findById(parent1.getId()).isPresent());
        assertEquals(menteeReviewRepository.findByEnrollment(enrollment1), menteeReviewRepository.findByLecture(lecture).get(0));

        parent1 = menteeReviewRepository.findByLecture(lecture).get(0);
        assertEquals(0, parent1.getChildren().size());
        assertFalse(mentorReviewRepository.findById(child.getId()).isPresent());
    }
}