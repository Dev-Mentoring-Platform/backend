package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.embeddable.Address;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.embeddable.LearningKind;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.purchase.vo.Cancellation;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.review.vo.Review;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Disabled
@Transactional
@SpringBootTest
public class LectureServiceIntegrationTest extends AbstractTest {

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
                () -> assertThat(lecture).extracting("thumbnail").isEqualTo(lectureCreateRequest.getThumbnailUrl()),
                () -> assertThat(lecture).extracting("title").isEqualTo(lectureCreateRequest.getTitle()),
                () -> assertThat(lecture).extracting("subTitle").isEqualTo(lectureCreateRequest.getSubTitle()),
                () -> assertThat(lecture).extracting("introduce").isEqualTo(lectureCreateRequest.getIntroduce()),
                () -> assertThat(lecture).extracting("difficultyType").isEqualTo(lectureCreateRequest.getDifficulty()),
                () -> assertThat(lecture).extracting("content").isEqualTo(lectureCreateRequest.getContent()),
                () -> assertThat(lecture.getSystemTypes()).hasSize(lectureCreateRequest.getSystems().size()),
                () -> assertThat(lecture.getLecturePrices()).hasSize(lectureCreateRequest.getLecturePrices().size()),
                () -> assertThat(lecture.getLectureSubjects()).hasSize(lectureCreateRequest.getSubjects().size())
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
                () -> assertThat(updatedLecture).extracting("thumbnail").isEqualTo(lectureUpdateRequest.getThumbnailUrl()),
                () -> assertThat(updatedLecture).extracting("title").isEqualTo(lectureUpdateRequest.getTitle()),
                () -> assertThat(updatedLecture).extracting("subTitle").isEqualTo(lectureUpdateRequest.getSubTitle()),
                () -> assertThat(updatedLecture).extracting("introduce").isEqualTo(lectureUpdateRequest.getIntroduce()),
                () -> assertThat(updatedLecture).extracting("difficultyType").isEqualTo(lectureUpdateRequest.getDifficulty()),
                () -> assertThat(updatedLecture).extracting("content").isEqualTo(lectureUpdateRequest.getContent()),
                () -> assertThat(updatedLecture.getSystemTypes()).hasSize(lectureUpdateRequest.getSystems().size()),
                () -> assertThat(updatedLecture.getLecturePrices()).hasSize(lectureUpdateRequest.getLecturePrices().size()),
                () -> assertThat(updatedLecture.getLectureSubjects()).hasSize(lectureUpdateRequest.getSubjects().size())
        );

        List<LectureSubject> lectureSubjects = lectureSubjectRepository.findByLecture(updatedLecture);
        assertEquals(1, lectureSubjects.size());
        LectureSubject lectureSubject = lectureSubjects.get(0);
        assertAll(
                () -> assertThat(lectureSubject.getId()).isNotNull(),
                () -> assertEquals(lectureSubject.getLearningKind().getLearningKind(), lectureSubjectUpdateRequest.getLearningKind()),
                () -> assertEquals(lectureSubject.getLearningKind().getLearningKindId(), lectureSubjectUpdateRequest.getLearningKindId()),
                () -> assertThat(lectureSubject).extracting("krSubject").isEqualTo(lectureSubjectUpdateRequest.getKrSubject())
        );

        List<LecturePrice> lecturePrices = lecturePriceRepository.findByLecture(updatedLecture);
        assertEquals(1, lecturePrices.size());
        LecturePrice lecturePrice = lecturePrices.get(0);
        assertAll(
                () -> assertThat(lecturePrice.getId()).isNotNull(),
                () -> assertThat(lecturePrice).extracting("isGroup").isEqualTo(lecturePriceUpdateRequest.getIsGroup()),
                () -> assertThat(lecturePrice).extracting("groupNumber").isEqualTo(lecturePriceUpdateRequest.getGroupNumber()),
                () -> assertThat(lecturePrice).extracting("pertimeCost").isEqualTo(lecturePriceUpdateRequest.getPertimeCost()),
                () -> assertThat(lecturePrice).extracting("pertimeLecture").isEqualTo(lecturePriceUpdateRequest.getPertimeLecture()),
                () -> assertThat(lecturePrice).extracting("totalTime").isEqualTo(lecturePriceUpdateRequest.getTotalTime()),
                () -> assertThat(lecturePrice).extracting("totalCost").isEqualTo(lecturePriceUpdateRequest.getTotalCost())
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

        List<LectureSubject> lectureSubjects = lectureSubjectRepository.findByLecture(lecture);
        assertEquals(1, lectureSubjects.size());
        LectureSubject lectureSubject = lectureSubjects.get(0);
        Long lectureSubjectId = lectureSubject.getId();

        List<LecturePrice> lecturePrices = lecturePriceRepository.findByLecture(lecture);
        assertEquals(1, lecturePrices.size());
        LecturePrice lecturePrice = lecturePrices.get(0);
        Long lecturePriceId = lecturePrice.getId();

        // 멘티
        SignUpRequest signUpRequest = getSignUpRequest("mentee", "mentee");
        User menteeUser = loginService.signUp(signUpRequest);
        loginService.verifyEmail(menteeUser.getUsername(), menteeUser.getEmailVerifyToken());

        Pick pick = pickService.createPick(menteeUser, lectureId);
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lectureId, lecturePriceId);
        Cancellation cancellation = cancellationService.cancel(menteeUser, lectureId, cancellationCreateRequest);
        reviewService.createMenteeReview(menteeUser, lectureId, menteeReviewCreateRequest);

        Review review = reviewRepository.findByEnrollment(enrollment);
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
        assertTrue(pickRepository.findByLectureId(lectureId).isEmpty());
        // enrollment
        assertTrue(enrollmentRepository.findAllByLectureId(lectureId).isEmpty());
        // cancellation
        assertNull(cancellationRepository.findByEnrollmentId(enrollment.getId()));
        // review
        assertTrue(reviewRepository.findAllByLectureId(lectureId).isEmpty());
        // chatroom
        chatroomRepository.findAll().forEach(chatroom -> {
            assertNotEquals(chatroom.getEnrollment().getLecture().getId(), lectureId);
        });

        // TODO - 보류
        // message
    }

    private List<Subject> subjects = new ArrayList<>();

    @BeforeEach
    void init() {

        subjects.add(Subject.of(LearningKind.of(LearningKindType.IT), "자바"));
        subjects.add(Subject.of(LearningKind.of(LearningKindType.IT), "파이썬"));
        subjects.add(Subject.of(LearningKind.of(LearningKindType.IT), "C/C++"));

    }

    @WithAccount(NAME)
    @Test
    void 강의목록() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Address zone = user.getZone();
        assertAll(
                () -> assertEquals("서울특별시", zone.getState()),
                () -> assertEquals("강남구", zone.getSiGunGu()),
                () -> assertEquals("삼성동", zone.getDongMyunLi())
        );
        mentorService.createMentor(user, mentorSignUpRequest);
        Lecture lecture = lectureService.createLecture(user, lectureCreateRequest);

        // When
        // Then
        // TODO - LectureListRequest 추가해서 테스트
        Page<LectureResponse> lectureResponses = lectureService.getLectureResponses(user, "서울특별시 강남구", null, 1);
        assertEquals(1, lectureResponses.getTotalElements());

        lectureResponses.stream().forEach(lectureResponse -> {
            assertAll(
                    () -> assertEquals(lectureCreateRequest.getTitle(), lectureResponse.getTitle()),
                    () -> assertEquals(1, lectureResponse.getLecturePrices().size()),
                    () -> assertNotNull(lectureResponse.getLectureMentor()),
                    () -> assertEquals(1, lectureResponse.getLectureMentor().getLectureCount()),
                    // TODO - 리뷰 확인
                    () -> assertEquals(0, lectureResponse.getLectureMentor().getReviewCount()),

                    () -> assertFalse(lectureResponse.isPicked())
            );
        });
    }
}