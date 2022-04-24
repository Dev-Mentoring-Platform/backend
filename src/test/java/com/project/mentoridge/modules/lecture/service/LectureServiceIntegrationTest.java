package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.embeddable.Address;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceWithLectureResponse;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSubjectRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class LectureServiceIntegrationTest {

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    LectureService lectureService;
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    LectureSubjectRepository lectureSubjectRepository;
    @Autowired
    LecturePriceRepository lecturePriceRepository;
    @Autowired
    PickService pickService;
    @Autowired
    PickRepository pickRepository;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;

    @Autowired
    SubjectRepository subjectRepository;

    private User menteeUser;
    private User mentorUser;
    private Mentor mentor;
    private Lecture lecture;
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

        User user = userRepository.findAllByUsername("mentee@email.com");
        if (user != null) {
            Mentee mentee = menteeRepository.findByUser(user);
            if (mentee != null) {
                menteeRepository.delete(mentee);
            }
            userRepository.delete(user);
        }
        menteeUser = loginService.signUp(SignUpRequest.builder()
                .username("mentee@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("mentee")
                .gender(GenderType.FEMALE)
                .birthYear(null)
                .phoneNumber(null)
                .nickname("mentee")
                .zone("서울특별시 강남구 삼성동")
                .image(null)
                .build());
        menteeUser.verifyEmail();
        menteeRepository.save(Mentee.builder()
                .user(menteeUser)
                .build());

        // 멘토
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
    @Test
    void 강의_등록() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        lectureService.createLecture(user, lectureCreateRequest);

        // Then
        Mentor mentor = mentorRepository.findByUser(user);
        List<Lecture> lectures = lectureRepository.findByMentor(mentor);
        assertEquals(1, lectures.size());

        Lecture lecture = lectures.get(0);
        assertAll(
                () -> assertThat(lecture.getId()).isNotNull(),
                () -> assertThat(lecture).extracting("title").isEqualTo(lectureCreateRequest.getTitle()),
                () -> assertThat(lecture).extracting("subTitle").isEqualTo(lectureCreateRequest.getSubTitle()),
                () -> assertThat(lecture).extracting("introduce").isEqualTo(lectureCreateRequest.getIntroduce()),
                () -> assertThat(lecture).extracting("content").isEqualTo(lectureCreateRequest.getContent()),
                () -> assertThat(lecture).extracting("difficulty").isEqualTo(lectureCreateRequest.getDifficulty()),
                () -> assertThat(lecture).extracting("thumbnail").isEqualTo(lectureCreateRequest.getThumbnail()),
                () -> assertThat(lecture.getSystems()).hasSize(lectureCreateRequest.getSystems().size()),
                () -> assertThat(lecture.getLecturePrices()).hasSize(lectureCreateRequest.getLecturePrices().size()),
                () -> assertThat(lecture.getLectureSubjects()).hasSize(lectureCreateRequest.getLectureSubjects().size())
        );

    }

    @DisplayName("실패 - 멘티가 강의 등록")
    @WithAccount(NAME)
    @Test
    void createLecture_notMentor() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);

        // When
        // Then
        assertThrows(UnauthorizedException.class, () -> {
            lectureService.createLecture(user, lectureCreateRequest);
        });
    }

    @WithAccount(NAME)
    @Test
    void updateLecture() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);

        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);
        Long lectureId = lecture.getId();

        // When
        lectureService.updateLecture(user, lectureId, lectureUpdateRequest);
        // Then
        Mentor mentor = mentorRepository.findByUser(user);
        List<Lecture> lectures = lectureRepository.findByMentor(mentor);
        assertEquals(1, lectures.size());

        Lecture updatedLecture = lectures.get(0);
        assertAll(
                () -> assertThat(updatedLecture.getId()).isNotNull(),
                () -> assertThat(updatedLecture).extracting("title").isEqualTo(lectureUpdateRequest.getTitle()),
                () -> assertThat(updatedLecture).extracting("subTitle").isEqualTo(lectureUpdateRequest.getSubTitle()),
                () -> assertThat(updatedLecture).extracting("introduce").isEqualTo(lectureUpdateRequest.getIntroduce()),
                () -> assertThat(updatedLecture).extracting("content").isEqualTo(lectureUpdateRequest.getContent()),
                () -> assertThat(updatedLecture).extracting("difficulty").isEqualTo(lectureUpdateRequest.getDifficulty()),
                () -> assertThat(updatedLecture).extracting("thumbnail").isEqualTo(lectureUpdateRequest.getThumbnail()),
                () -> assertThat(updatedLecture.getSystems()).hasSize(lectureUpdateRequest.getSystems().size()),
                () -> assertThat(updatedLecture.getLecturePrices()).hasSize(lectureUpdateRequest.getLecturePrices().size()),
                () -> assertThat(updatedLecture.getLectureSubjects()).hasSize(lectureUpdateRequest.getLectureSubjects().size())
        );

        List<LectureSubject> lectureSubjects = lectureSubjectRepository.findByLecture(updatedLecture);
        assertEquals(1, lectureSubjects.size());
        LectureSubject lectureSubject = lectureSubjects.get(0);
        assertAll(
                () -> assertThat(lectureSubject.getId()).isNotNull(),
                () -> assertEquals(lectureSubject.getSubject().getId(), lectureSubjectUpdateRequest.getSubjectId())
        );

        List<LecturePrice> lecturePrices = lecturePriceRepository.findByLecture(updatedLecture);
        assertEquals(1, lecturePrices.size());
        LecturePrice lecturePrice = lecturePrices.get(0);
        assertAll(
                () -> assertThat(lecturePrice.getId()).isNotNull(),
                () -> assertThat(lecturePrice).extracting("isGroup").isEqualTo(lecturePriceUpdateRequest.getIsGroup()),
                () -> assertThat(lecturePrice).extracting("numberOfMembers").isEqualTo(lecturePriceUpdateRequest.getNumberOfMembers()),
                () -> assertThat(lecturePrice).extracting("pricePerHour").isEqualTo(lecturePriceUpdateRequest.getPricePerHour()),
                () -> assertThat(lecturePrice).extracting("timePerLecture").isEqualTo(lecturePriceUpdateRequest.getTimePerLecture()),
                () -> assertThat(lecturePrice).extracting("numberOfLectures").isEqualTo(lecturePriceUpdateRequest.getNumberOfLectures()),
                () -> assertThat(lecturePrice).extracting("totalPrice").isEqualTo(lecturePriceUpdateRequest.getTotalPrice()),

                // 수정된 강의는 재승인 필요
                () -> assertThat(updatedLecture.isApproved()).isEqualTo(false)
        );
    }

    // TODO - 연관 엔티티 삭제
    @WithAccount(NAME)
    @Test
    void deleteLecture() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        mentorService.createMentor(user, mentorSignUpRequest);
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);
        Long lectureId = lecture.getId();

        // 강의 승인
        lecture.approve();

        List<LectureSubject> lectureSubjects = lectureSubjectRepository.findByLecture(lecture);
        assertEquals(1, lectureSubjects.size());
        LectureSubject lectureSubject = lectureSubjects.get(0);
        List<LecturePrice> lecturePrices = lecturePriceRepository.findByLecture(lecture);
        assertEquals(1, lecturePrices.size());
        LecturePrice lecturePrice = lecturePrices.get(0);
        Long lecturePriceId = lecturePrice.getId();

        Pick pick = pickService.createPick(menteeUser, lectureId, lecturePriceId);
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lectureId, lecturePriceId);
        // 2022.03.05 - 강의 신청 시 멘토 확인 필요
        enrollment.check();

        menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), menteeReviewCreateRequest);

        MenteeReview review = menteeReviewRepository.findByEnrollment(enrollment);
        assertAll(
                () -> assertNotNull(review),
                () -> assertEquals(enrollment, review.getEnrollment()),
                () -> assertEquals(0, review.getChildren().size()),
                () -> assertEquals(lecture, review.getLecture()),
                () -> assertEquals(menteeReviewCreateRequest.getContent(), review.getContent()),
                () -> assertEquals(menteeReviewCreateRequest.getScore(), review.getScore())
        );

        // When
        lectureService.deleteLecture(user, lectureId);

        // Then

        // lecture
        Mentor mentor = mentorRepository.findByUser(user);
        assertEquals(0, lectureRepository.findByMentor(mentor).size());

        // lectureSubject
        assertTrue(lectureSubjectRepository.findByLectureId(lectureId).isEmpty());
        // lecturePrice
        assertTrue(lecturePriceRepository.findByLectureId(lectureId).isEmpty());
        // pick
        assertTrue(pickRepository.findByLecture(lecture).isEmpty());
        // enrollment
        assertTrue(enrollmentRepository.findByLecture(lecture).isEmpty());
        // review
        assertTrue(menteeReviewRepository.findByLecture(lecture).isEmpty());

        // TODO - message 보류
    }

    @WithAccount(NAME)
    @Test
    void 강의목록() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Address zone = user.getZone();
        assertAll(
                () -> assertEquals("서울특별시", zone.getState()),
                () -> assertEquals("강서구", zone.getSiGunGu()),
                () -> assertEquals("화곡동", zone.getDongMyunLi())
        );
        mentorService.createMentor(user, mentorSignUpRequest);
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);

        // 강의 승인
        lecture.approve();

        // When
        // Then
        // TODO - LectureListRequest 추가해서 테스트
        // Page<LectureResponse> lectureResponses = lectureService.getLectureResponsesPerLecturePrice(user, "서울특별시 강서구", null, 1);
        Page<LecturePriceWithLectureResponse> lectureResponses = lectureService.getLectureResponsesPerLecturePrice(user, "서울특별시 강서구", null, 1);
        assertEquals(1, lectureResponses.getTotalElements());

        lectureResponses.stream().forEach(lectureResponse -> {
            assertAll(
                    () -> assertEquals(lectureCreateRequest.getTitle(), lectureResponse.getTitle()),
                    // () -> assertEquals(1, lectureResponse.getLecturePrices().size()),
                    () -> assertNotNull(lectureResponse.getLectureMentor()),
                    () -> assertEquals(1, lectureResponse.getLectureMentor().getLectureCount()),
                    // TODO - 리뷰 확인
                    () -> assertEquals(0, lectureResponse.getLectureMentor().getReviewCount()),

                    () -> assertFalse(lectureResponse.isPicked())
            );
        });
    }
}