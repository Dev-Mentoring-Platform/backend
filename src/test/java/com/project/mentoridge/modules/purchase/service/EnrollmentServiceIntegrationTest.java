package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.config.exception.AlreadyExistException;
import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.log.component.LecturePriceLogService;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithEachLectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleEachLectureResponse;
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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class EnrollmentServiceIntegrationTest extends AbstractIntegrationTest {

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
    @Override
    protected void init() {

        initDatabase();

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
        // 신청 승인
        lecture.approve(lectureLogService);
    }

    // 멘티
    @DisplayName("신청 강의 리스트 - 승인/미승인")
    @Test
    void get_paged_checked_EnrollmentWithEachLectureResponses() {

        // Given
        Enrollment enrollment = enrollmentRepository.save(Enrollment.builder()
                .mentee(mentee)
                .lecture(lecture)
                .lecturePrice(lecturePrice)
                .build());
        // 2022.03.05 - 강의 신청 시 멘토 확인 필요
        enrollmentService.check(mentorUser, enrollment.getId());

        // When
        Page<EnrollmentWithEachLectureResponse> checkedResponses = enrollmentService.getEnrollmentWithEachLectureResponsesOfMentee(menteeUser, true, 1);
        Page<EnrollmentWithEachLectureResponse> uncheckedResponses = enrollmentService.getEnrollmentWithEachLectureResponsesOfMentee(menteeUser, false, 1);

        // Then
        assertThat(checkedResponses.getTotalElements()).isEqualTo(1L);
        EnrollmentWithEachLectureResponse checkedResponse = checkedResponses.getContent().get(0);
        assertAll(
                () -> assertThat(checkedResponse.getEnrollmentId()).isEqualTo(enrollment.getId()),
                () -> assertThat(checkedResponse.isChecked()).isEqualTo(enrollment.isChecked()),
                () -> assertThat(checkedResponse.isFinished()).isEqualTo(enrollment.isFinished()),
                () -> assertThat(checkedResponse.getLectureId()).isEqualTo(enrollment.getLecture().getId()),
                () -> assertThat(checkedResponse.getTitle()).isEqualTo(enrollment.getLecture().getTitle()),
                () -> assertThat(checkedResponse.getSubTitle()).isEqualTo(enrollment.getLecture().getSubTitle()),
                () -> assertThat(checkedResponse.getIntroduce()).isEqualTo(enrollment.getLecture().getIntroduce()),
                () -> assertThat(checkedResponse.getContent()).isEqualTo(enrollment.getLecture().getContent()),
                () -> assertThat(checkedResponse.getDifficulty()).isEqualTo(enrollment.getLecture().getDifficulty()),

                // systems
                () -> assertThat(checkedResponse.getSystems().size()).isEqualTo(enrollment.getLecture().getSystems().size()),

                // lectureSubjects
                () -> assertThat(checkedResponse.getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),

                () -> assertThat(checkedResponse.getThumbnail()).isEqualTo(enrollment.getLecture().getThumbnail()),
                () -> assertThat(checkedResponse.isApproved()).isEqualTo(enrollment.getLecture().isApproved()),

                // lectureMentor
                () -> assertThat(checkedResponse.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(checkedResponse.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(checkedResponse.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                () -> assertThat(checkedResponse.getLectureMentor().getLectureCount()).isNull(),
                () -> assertThat(checkedResponse.getLectureMentor().getReviewCount()).isNull(),

                // lecturePrice
                () -> assertThat(checkedResponse.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice.getId()),
                () -> assertThat(checkedResponse.getLecturePrice().isGroup()).isEqualTo(lecturePrice.isGroup()),
                () -> assertThat(checkedResponse.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice.getNumberOfMembers()),
                () -> assertThat(checkedResponse.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice.getPricePerHour()),
                () -> assertThat(checkedResponse.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice.getTimePerLecture()),
                () -> assertThat(checkedResponse.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice.getNumberOfLectures()),
                () -> assertThat(checkedResponse.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice.getTotalPrice()),
                () -> assertThat(checkedResponse.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(checkedResponse.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())),
                () -> assertThat(checkedResponse.getLecturePrice().isClosed()).isEqualTo(lecturePrice.isClosed())
        );


        assertThat(uncheckedResponses.getTotalElements()).isEqualTo(0L);

    }

    @DisplayName("수강 강의 조회")
    @Test
    void get_EachLectureResponse_by_enrollmentId() {

        // Given
        Enrollment enrollment = enrollmentRepository.save(Enrollment.builder()
                .mentee(mentee)
                .lecture(lecture)
                .lecturePrice(lecturePrice)
                .build());
        // 2022.03.05 - 강의 신청 시 멘토 확인 필요
        enrollmentService.check(mentorUser, enrollment.getId());

        // When
        EachLectureResponse response = enrollmentService.getEachLectureResponseOfEnrollment(menteeUser, enrollment.getId(), true);
        // Then
        assertAll(
                () -> assertThat(response.getLectureId()).isEqualTo(enrollment.getLecture().getId()),
                () -> assertThat(response.getTitle()).isEqualTo(enrollment.getLecture().getTitle()),
                () -> assertThat(response.getSubTitle()).isEqualTo(enrollment.getLecture().getSubTitle()),
                () -> assertThat(response.getIntroduce()).isEqualTo(enrollment.getLecture().getIntroduce()),
                () -> assertThat(response.getContent()).isEqualTo(enrollment.getLecture().getContent()),
                () -> assertThat(response.getDifficulty()).isEqualTo(enrollment.getLecture().getDifficulty()),

                // systems
                () -> assertThat(response.getSystems().size()).isEqualTo(enrollment.getLecture().getSystems().size()),

                // lecturePrice
                () -> assertThat(response.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice.getId()),
                () -> assertThat(response.getLecturePrice().isGroup()).isEqualTo(lecturePrice.isGroup()),
                () -> assertThat(response.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice.getNumberOfMembers()),
                () -> assertThat(response.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice.getPricePerHour()),
                () -> assertThat(response.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice.getTimePerLecture()),
                () -> assertThat(response.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice.getNumberOfLectures()),
                () -> assertThat(response.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice.getTotalPrice()),
                () -> assertThat(response.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())),
                () -> assertThat(response.getLecturePrice().isClosed()).isEqualTo(lecturePrice.isClosed()),

                // lectureSubjects
                () -> assertThat(response.getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),

                () -> assertThat(response.getThumbnail()).isEqualTo(enrollment.getLecture().getThumbnail()),
                () -> assertThat(response.isApproved()).isEqualTo(enrollment.getLecture().isApproved()),

                // lectureMentor
                () -> assertThat(response.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                () -> assertThat(response.getLectureMentor().getLectureCount()).isNull(),
                () -> assertThat(response.getLectureMentor().getReviewCount()).isNull()
        );
    }

    // 2022.03.05 - 강의 신청 시 멘토 확인 필요
    @DisplayName("수강 강의 조회 - 신청 승인되지 않은 경우")
    @Test
    void get_EachLectureResponse_by_enrollmentId_if_enrollment_is_not_checked() {

        // Given
        Enrollment enrollment = enrollmentRepository.save(Enrollment.builder()
                .mentee(mentee)
                .lecture(lecture)
                .lecturePrice(lecturePrice)
                .build());

        // When
        EachLectureResponse response = enrollmentService.getEachLectureResponseOfEnrollment(menteeUser, enrollment.getId(), true);
        // Then
        assertNull(response);
    }

    @DisplayName("리뷰 작성 수강내역 리스트")
    @Test
    void get_paged_reviewed_EnrollmentWithSimpleEachLectureResponses() {

        // Given
        Enrollment enrollment = enrollmentRepository.save(Enrollment.builder()
                .mentee(mentee)
                .lecture(lecture)
                .lecturePrice(lecturePrice)
                .build());
        // 2022.03.05 - 강의 신청 시 멘토 확인 필요
        enrollmentService.check(mentorUser, enrollment.getId());

        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);

        // When
        Page<EnrollmentWithSimpleEachLectureResponse> responses = enrollmentService.getEnrollmentWithSimpleEachLectureResponses(menteeUser, true, 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(1L);
        EnrollmentWithSimpleEachLectureResponse response = responses.getContent().get(0);
        assertAll(
                () -> assertThat(response.getEnrollmentId()).isEqualTo(enrollment.getId()),
                () -> assertThat(response.getMentee()).isEqualTo(enrollment.getMentee().getUser().getNickname()),
                () -> assertThat(response.getLectureTitle()).isEqualTo(enrollment.getLecture().getTitle()),
                () -> assertThat(response.getCreatedAt()).isNotNull(),

                () -> assertThat(response.getLecture().getLectureId()).isEqualTo(enrollment.getLecture().getId()),
                () -> assertThat(response.getLecture().getTitle()).isEqualTo(enrollment.getLecture().getTitle()),
                () -> assertThat(response.getLecture().getSubTitle()).isEqualTo(enrollment.getLecture().getSubTitle()),
                () -> assertThat(response.getLecture().getIntroduce()).isEqualTo(enrollment.getLecture().getIntroduce()),
                () -> assertThat(response.getLecture().getDifficulty()).isEqualTo(enrollment.getLecture().getDifficulty()),

                // systems
                () -> assertThat(response.getLecture().getSystems().size()).isEqualTo(enrollment.getLecture().getSystems().size()),

                // lecturePrice
                () -> assertThat(response.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice.getId()),
                () -> assertThat(response.getLecture().getLecturePrice().isGroup()).isEqualTo(lecturePrice.isGroup()),
                () -> assertThat(response.getLecture().getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice.getNumberOfMembers()),
                () -> assertThat(response.getLecture().getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice.getPricePerHour()),
                () -> assertThat(response.getLecture().getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice.getTimePerLecture()),
                () -> assertThat(response.getLecture().getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice.getNumberOfLectures()),
                () -> assertThat(response.getLecture().getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice.getTotalPrice()),
                () -> assertThat(response.getLecture().getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response.getLecture().getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())),
                () -> assertThat(response.getLecture().getLecturePrice().isClosed()).isEqualTo(lecturePrice.isClosed()),

                // lectureSubjects
                () -> assertThat(response.getLecture().getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),

                () -> assertThat(response.getLecture().getThumbnail()).isEqualTo(enrollment.getLecture().getThumbnail()),
                () -> assertThat(response.getLecture().isApproved()).isEqualTo(enrollment.getLecture().isApproved()),

                () -> assertThat(response.getLecture().getMentorNickname()).isEqualTo(mentorUser.getNickname())
        );
    }

    @DisplayName("리뷰 미작성 수강내역 리스트")
    @Test
    void get_paged_unreviewed_EnrollmentWithSimpleEachLectureResponses() {

        // Given
        Enrollment enrollment = enrollmentRepository.save(Enrollment.builder()
                .mentee(mentee)
                .lecture(lecture)
                .lecturePrice(lecturePrice)
                .build());
        // 2022.03.05 - 강의 신청 시 멘토 확인 필요
        enrollmentService.check(mentorUser, enrollment.getId());

        // When
        Page<EnrollmentWithSimpleEachLectureResponse> responses = enrollmentService.getEnrollmentWithSimpleEachLectureResponses(menteeUser, false, 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(1L);
        assertThat(responses.getTotalElements()).isEqualTo(1L);
        EnrollmentWithSimpleEachLectureResponse response = responses.getContent().get(0);
        assertAll(
                () -> assertThat(response.getEnrollmentId()).isEqualTo(enrollment.getId()),
                () -> assertThat(response.getMentee()).isEqualTo(enrollment.getMentee().getUser().getNickname()),
                () -> assertThat(response.getLectureTitle()).isEqualTo(enrollment.getLecture().getTitle()),
                () -> assertThat(response.getCreatedAt()).isNotNull(),

                () -> assertThat(response.getLecture().getLectureId()).isEqualTo(enrollment.getLecture().getId()),
                () -> assertThat(response.getLecture().getTitle()).isEqualTo(enrollment.getLecture().getTitle()),
                () -> assertThat(response.getLecture().getSubTitle()).isEqualTo(enrollment.getLecture().getSubTitle()),
                () -> assertThat(response.getLecture().getIntroduce()).isEqualTo(enrollment.getLecture().getIntroduce()),
                () -> assertThat(response.getLecture().getDifficulty()).isEqualTo(enrollment.getLecture().getDifficulty()),

                // systems
                () -> assertThat(response.getLecture().getSystems().size()).isEqualTo(enrollment.getLecture().getSystems().size()),

                // lecturePrice
                () -> assertThat(response.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice.getId()),
                () -> assertThat(response.getLecture().getLecturePrice().isGroup()).isEqualTo(lecturePrice.isGroup()),
                () -> assertThat(response.getLecture().getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice.getNumberOfMembers()),
                () -> assertThat(response.getLecture().getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice.getPricePerHour()),
                () -> assertThat(response.getLecture().getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice.getTimePerLecture()),
                () -> assertThat(response.getLecture().getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice.getNumberOfLectures()),
                () -> assertThat(response.getLecture().getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice.getTotalPrice()),
                () -> assertThat(response.getLecture().getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response.getLecture().getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())),
                () -> assertThat(response.getLecture().getLecturePrice().isClosed()).isEqualTo(lecturePrice.isClosed()),

                // lectureSubjects
                () -> assertThat(response.getLecture().getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),

                () -> assertThat(response.getLecture().getThumbnail()).isEqualTo(enrollment.getLecture().getThumbnail()),
                () -> assertThat(response.getLecture().isApproved()).isEqualTo(enrollment.getLecture().isApproved()),

                () -> assertThat(response.getLecture().getMentorNickname()).isEqualTo(mentorUser.getNickname())
        );
    }

    @DisplayName("수강 내역 조회")
    @Test
    void get_EnrollmentWithSimpleEachLectureResponse_by_enrollmentId() {

        // Given
        Enrollment enrollment = enrollmentRepository.save(Enrollment.builder()
                .mentee(mentee)
                .lecture(lecture)
                .lecturePrice(lecturePrice)
                .build());
        // 2022.03.05 - 강의 신청 시 멘토 확인 필요
        enrollmentService.check(mentorUser, enrollment.getId());

        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);

        // When
        EnrollmentWithSimpleEachLectureResponse response = enrollmentService.getEnrollmentWithSimpleEachLectureResponse(menteeUser, enrollment.getId());
        // Then
        assertAll(
                () -> assertThat(response.getEnrollmentId()).isEqualTo(enrollment.getId()),
                () -> assertThat(response.getMentee()).isEqualTo(enrollment.getMentee().getUser().getNickname()),
                () -> assertThat(response.getLectureTitle()).isEqualTo(enrollment.getLecture().getTitle()),
                () -> assertThat(response.getCreatedAt()).isNotNull(),

                () -> assertThat(response.getLecture().getLectureId()).isEqualTo(enrollment.getLecture().getId()),
                () -> assertThat(response.getLecture().getTitle()).isEqualTo(enrollment.getLecture().getTitle()),
                () -> assertThat(response.getLecture().getSubTitle()).isEqualTo(enrollment.getLecture().getSubTitle()),
                () -> assertThat(response.getLecture().getIntroduce()).isEqualTo(enrollment.getLecture().getIntroduce()),
                () -> assertThat(response.getLecture().getDifficulty()).isEqualTo(enrollment.getLecture().getDifficulty()),

                // systems
                () -> assertThat(response.getLecture().getSystems().size()).isEqualTo(enrollment.getLecture().getSystems().size()),

                // lecturePrice
                () -> assertThat(response.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice.getId()),
                () -> assertThat(response.getLecture().getLecturePrice().isGroup()).isEqualTo(lecturePrice.isGroup()),
                () -> assertThat(response.getLecture().getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice.getNumberOfMembers()),
                () -> assertThat(response.getLecture().getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice.getPricePerHour()),
                () -> assertThat(response.getLecture().getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice.getTimePerLecture()),
                () -> assertThat(response.getLecture().getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice.getNumberOfLectures()),
                () -> assertThat(response.getLecture().getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice.getTotalPrice()),
                () -> assertThat(response.getLecture().getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response.getLecture().getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())),
                () -> assertThat(response.getLecture().getLecturePrice().isClosed()).isEqualTo(lecturePrice.isClosed()),

                // lectureSubjects
                () -> assertThat(response.getLecture().getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),

                () -> assertThat(response.getLecture().getThumbnail()).isEqualTo(enrollment.getLecture().getThumbnail()),
                () -> assertThat(response.getLecture().isApproved()).isEqualTo(enrollment.getLecture().isApproved()),

                () -> assertThat(response.getLecture().getMentorNickname()).isEqualTo(mentorUser.getNickname())
        );
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
                () -> assertEquals(lecturePrice.isGroup(), enrollment.getLecturePrice().isGroup()),
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
        User user = userRepository.findByUsername(USERNAME).orElseThrow(RuntimeException::new);
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
    void cannot_enroll_closed_lecture() {

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
        User user = userRepository.findByUsername(USERNAME).orElseThrow(RuntimeException::new);
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
        enrollmentService.check(mentorUser, enrollment.getId());

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
//        User user = userRepository.findByUsername(USERNAME).orElseThrow(RuntimeException::new);
//        Mentee mentee = menteeRepository.findByUser(user);
//        assertNotNull(user);
//
//        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
//        Long lecturePriceId = lecturePrice.getId();
//
//        Enrollment enrollment = enrollmentService.createEnrollment(user, lectureId, lecturePriceId);
//        Chatroom chatroom = chatroomRepository.findByEnrollment(enrollment).orElseThrow(RuntimeException::new);
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
////        enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(RuntimeException::new);
////        assertTrue(enrollment.isClosed());
//        assertFalse(enrollmentRepository.findByMenteeAndLecture(mentee, lecture));
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