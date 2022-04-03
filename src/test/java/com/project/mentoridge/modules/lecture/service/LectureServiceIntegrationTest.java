package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.embeddable.Address;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.review.vo.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
                () -> assertThat(lecturePrice).extracting("totalPrice").isEqualTo(lecturePriceUpdateRequest.getTotalPrice())
        );

        // TODO - 수정된 강의는 재승인 필요
        fail();

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

        Pick pick = pickService.createPick(menteeUser, lectureId);
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lectureId, lecturePriceId);
        // 2022.03.05 - 강의 신청 시 멘토 확인 필요
        enrollment.check();

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
        assertTrue(pickRepository.findByLecture(lecture).isEmpty());
        // enrollment
        assertTrue(enrollmentRepository.findByLecture(lecture).isEmpty());
        // review
        assertTrue(reviewRepository.findByLecture(lecture).isEmpty());

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
        Page<LectureResponse> lectureResponses = lectureService.getLectureResponsesPerLecturePrice(user, "서울특별시 강서구", null, 1);
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