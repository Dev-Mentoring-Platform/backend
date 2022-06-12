package com.project.mentoridge.modules.account.service;

import com.project.mentoridge.configuration.AbstractIntegrationTest;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceWithLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentResponse;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class MentorLectureServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MentorLectureService mentorLectureService;


    @Test
    void get_paged_LectureResponses_by_user() {

        // Given
        // When
        Page<LectureResponse> responses = mentorLectureService.getLectureResponses(mentorUser, 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(1);
        LectureResponse response = responses.getContent().get(0);
        assertAll(
                () -> assertThat(response).extracting("id").isEqualTo(lecture1.getId()),
                () -> assertThat(response).extracting("title").isEqualTo(lecture1.getTitle()),
                () -> assertThat(response).extracting("subTitle").isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response).extracting("introduce").isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response).extracting("content").isEqualTo(lecture1.getContent()),
                () -> assertThat(response).extracting("difficulty").isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response.getSystems()).hasSize(2),
                () -> assertThat(response.getLecturePrices()).hasSize(2),
                () -> assertThat(response.getLectureSubjects()).hasSize(1),
                () -> assertThat(response).extracting("thumbnail").isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response).extracting("approved").isEqualTo(lecture1.isApproved()),
                () -> assertThat(response).extracting("reviewCount").isNull(),
                () -> assertThat(response).extracting("scoreAverage").isNull(),
                () -> assertThat(response).extracting("enrollmentCount").isNull(),

                () -> assertThat(response).extracting("lectureMentor").extracting("mentorId").isEqualTo(mentor.getId()),
                () -> assertThat(response).extracting("lectureMentor").extracting("lectureCount").isEqualTo(1),
                () -> assertThat(response).extracting("lectureMentor").extracting("reviewCount").isEqualTo(4),
                () -> assertThat(response).extracting("lectureMentor").extracting("nickname").isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response).extracting("lectureMentor").extracting("image").isEqualTo(mentorUser.getImage())
                // TODO - TEST
                // () -> assertThat(response).extracting("picked").isEqualTo(false)
        );
    }

    @Test
    void get_paged_LectureResponses_by_mentorId() {

        // Given
        // When
        Page<LectureResponse> responses = mentorLectureService.getLectureResponses(mentor.getId(), 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(1);
        LectureResponse response = responses.getContent().get(0);
        assertAll(
                () -> assertThat(response).extracting("id").isEqualTo(lecture1.getId()),
                () -> assertThat(response).extracting("title").isEqualTo(lecture1.getTitle()),
                () -> assertThat(response).extracting("subTitle").isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response).extracting("introduce").isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response).extracting("content").isEqualTo(lecture1.getContent()),
                () -> assertThat(response).extracting("difficulty").isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response.getSystems()).hasSize(2),
                () -> assertThat(response.getLecturePrices()).hasSize(2),
                () -> assertThat(response.getLectureSubjects()).hasSize(1),
                () -> assertThat(response).extracting("thumbnail").isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response).extracting("approved").isEqualTo(lecture1.isApproved()),
                () -> assertThat(response).extracting("reviewCount").isNull(),
                () -> assertThat(response).extracting("scoreAverage").isNull(),
                () -> assertThat(response).extracting("enrollmentCount").isNull(),

                () -> assertThat(response).extracting("lectureMentor").extracting("mentorId").isEqualTo(mentor.getId()),
                () -> assertThat(response).extracting("lectureMentor").extracting("lectureCount").isEqualTo(1),
                () -> assertThat(response).extracting("lectureMentor").extracting("reviewCount").isEqualTo(4),
                () -> assertThat(response).extracting("lectureMentor").extracting("nickname").isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response).extracting("lectureMentor").extracting("image").isEqualTo(mentorUser.getImage())
                // TODO - TEST
                // () -> assertThat(response).extracting("picked").isEqualTo(false)
        );
    }

    @Test
    void get_LectureResponse_per_lecturePrice() {

        // Given
        // When
        LecturePriceWithLectureResponse response1 = mentorLectureService.getLectureResponsePerLecturePrice(mentor.getId(), lecture1.getId(), lecturePrice1.getId());
        LecturePriceWithLectureResponse response2 = mentorLectureService.getLectureResponsePerLecturePrice(mentor.getId(), lecture1.getId(), lecturePrice2.getId());
        LecturePriceWithLectureResponse response3 = mentorLectureService.getLectureResponsePerLecturePrice(mentor.getId(), lecture2.getId(), lecturePrice3.getId());

        // Then
        assertAll(
                () -> assertThat(response1.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response1.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response1.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response1.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response1.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response1.getDifficulty()).isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response1.getSystems()).hasSize(2),

                () -> assertThat(response1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response1.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice1.getIsGroup()),
                () -> assertThat(response1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(response1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(response1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(response1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(response1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(response1.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1.getIsGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response1.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),

                () -> assertThat(response1.getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response1.getLectureSubjects()).hasSize(2),
                () -> assertThat(response1.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response1.getApproved()).isTrue(),
                () -> assertThat(response1.getClosed()).isFalse(),

                () -> assertThat(response1.getReviewCount()).isEqualTo(3),
                () -> assertThat(response1.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response1.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response1.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response1.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response1.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response1.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response1.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                // TODO - TEST
                () -> assertThat(response1.getPicked()).isNull(),
                () -> assertThat(response1.getPickCount()).isEqualTo(1),
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                () -> assertThat(response2.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response2.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response2.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response2.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response2.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response2.getDifficulty()).isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response2.getSystems()).hasSize(2),

                () -> assertThat(response2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(response2.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice2.getIsGroup()),
                () -> assertThat(response2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(response2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(response2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(response2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(response2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(response2.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice2.getIsGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response2.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),

                () -> assertThat(response2.getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(response2.getLectureSubjects()).hasSize(2),
                () -> assertThat(response2.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response2.getApproved()).isTrue(),
                () -> assertThat(response2.getClosed()).isFalse(),

                () -> assertThat(response2.getReviewCount()).isEqualTo(3),
                () -> assertThat(response2.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response2.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response2.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response2.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response2.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response2.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response2.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                // TODO - TEST
                () -> assertThat(response2.getPicked()).isNull(),
                () -> assertThat(response2.getPickCount()).isEqualTo(1),
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                () -> assertThat(response3.getLectureId()).isEqualTo(lecture2.getId()),
                () -> assertThat(response3.getTitle()).isEqualTo(lecture2.getTitle()),
                () -> assertThat(response3.getSubTitle()).isEqualTo(lecture2.getSubTitle()),
                () -> assertThat(response3.getIntroduce()).isEqualTo(lecture2.getIntroduce()),
                () -> assertThat(response3.getContent()).isEqualTo(lecture2.getContent()),
                () -> assertThat(response3.getDifficulty()).isEqualTo(lecture2.getDifficulty()),
                () -> assertThat(response3.getSystems()).hasSize(2),

                () -> assertThat(response3.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice3.getId()),
                () -> assertThat(response3.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice3.getIsGroup()),
                () -> assertThat(response3.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice3.getNumberOfMembers()),
                () -> assertThat(response3.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice3.getPricePerHour()),
                () -> assertThat(response3.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice3.getTimePerLecture()),
                () -> assertThat(response3.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice3.getNumberOfLectures()),
                () -> assertThat(response3.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice3.getTotalPrice()),
                () -> assertThat(response3.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice3.getIsGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response3.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice3.getPricePerHour(), lecturePrice3.getTimePerLecture(), lecturePrice3.getNumberOfLectures())),

                () -> assertThat(response3.getLecturePriceId()).isEqualTo(lecturePrice3.getId()),
                () -> assertThat(response3.getLectureSubjects()).hasSize(2),
                () -> assertThat(response3.getThumbnail()).isEqualTo(lecture2.getThumbnail()),
                () -> assertThat(response3.getApproved()).isTrue(),
                () -> assertThat(response3.getClosed()).isFalse(),

                () -> assertThat(response3.getReviewCount()).isEqualTo(3),
                () -> assertThat(response3.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response3.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response3.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response3.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response3.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response3.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response3.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                // TODO - TEST
                () -> assertThat(response3.getPicked()).isNull(),
                () -> assertThat(response3.getPickCount()).isEqualTo(1)
        );
    }

    @Test
    void get_paged_LecturePriceWithLectureResponses() {

        // Given
        // When
        Page<LecturePriceWithLectureResponse> responses = mentorLectureService.getLectureResponsesPerLecturePrice(mentor.getId(), 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(3);

        LecturePriceWithLectureResponse response1 = responses.getContent().get(0);
        LecturePriceWithLectureResponse response2 = responses.getContent().get(1);
        LecturePriceWithLectureResponse response3 = responses.getContent().get(2);
        assertAll(
                () -> assertThat(response1.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response1.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response1.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response1.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response1.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response1.getDifficulty()).isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response1.getSystems()).hasSize(2),

                () -> assertThat(response1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response1.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice1.getIsGroup()),
                () -> assertThat(response1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(response1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(response1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(response1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(response1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(response1.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1.getIsGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response1.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),

                () -> assertThat(response1.getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response1.getLectureSubjects()).hasSize(2),
                () -> assertThat(response1.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response1.getApproved()).isTrue(),
                () -> assertThat(response1.getClosed()).isFalse(),

                () -> assertThat(response1.getReviewCount()).isEqualTo(3),
                () -> assertThat(response1.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response1.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response1.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response1.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response1.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response1.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response1.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                // TODO - TEST
                () -> assertThat(response1.getPicked()).isNull(),
                () -> assertThat(response1.getPickCount()).isEqualTo(1),
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                () -> assertThat(response2.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response2.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response2.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response2.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response2.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response2.getDifficulty()).isEqualTo(lecture1.getDifficulty()),
                () -> assertThat(response2.getSystems()).hasSize(2),

                () -> assertThat(response2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(response2.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice2.getIsGroup()),
                () -> assertThat(response2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(response2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(response2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(response2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(response2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(response2.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice2.getIsGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response2.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),

                () -> assertThat(response2.getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(response2.getLectureSubjects()).hasSize(2),
                () -> assertThat(response2.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response2.getApproved()).isTrue(),
                () -> assertThat(response2.getClosed()).isFalse(),

                () -> assertThat(response2.getReviewCount()).isEqualTo(3),
                () -> assertThat(response2.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response2.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response2.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response2.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response2.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response2.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response2.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                // TODO - TEST
                () -> assertThat(response2.getPicked()).isNull(),
                () -> assertThat(response2.getPickCount()).isEqualTo(1),
                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                () -> assertThat(response3.getLectureId()).isEqualTo(lecture2.getId()),
                () -> assertThat(response3.getTitle()).isEqualTo(lecture2.getTitle()),
                () -> assertThat(response3.getSubTitle()).isEqualTo(lecture2.getSubTitle()),
                () -> assertThat(response3.getIntroduce()).isEqualTo(lecture2.getIntroduce()),
                () -> assertThat(response3.getContent()).isEqualTo(lecture2.getContent()),
                () -> assertThat(response3.getDifficulty()).isEqualTo(lecture2.getDifficulty()),
                () -> assertThat(response3.getSystems()).hasSize(2),

                () -> assertThat(response3.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice3.getId()),
                () -> assertThat(response3.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice3.getIsGroup()),
                () -> assertThat(response3.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice3.getNumberOfMembers()),
                () -> assertThat(response3.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice3.getPricePerHour()),
                () -> assertThat(response3.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice3.getTimePerLecture()),
                () -> assertThat(response3.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice3.getNumberOfLectures()),
                () -> assertThat(response3.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice3.getTotalPrice()),
                () -> assertThat(response3.getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice3.getIsGroup() ? "그룹강의" : "1:1 개인강의"),
                () -> assertThat(response3.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice3.getPricePerHour(), lecturePrice3.getTimePerLecture(), lecturePrice3.getNumberOfLectures())),

                () -> assertThat(response3.getLecturePriceId()).isEqualTo(lecturePrice3.getId()),
                () -> assertThat(response3.getLectureSubjects()).hasSize(2),
                () -> assertThat(response3.getThumbnail()).isEqualTo(lecture2.getThumbnail()),
                () -> assertThat(response3.getApproved()).isTrue(),
                () -> assertThat(response3.getClosed()).isFalse(),

                () -> assertThat(response3.getReviewCount()).isEqualTo(3),
                () -> assertThat(response3.getScoreAverage()).isEqualTo(3),
                () -> assertThat(response3.getEnrollmentCount()).isEqualTo(3),

                () -> assertThat(response3.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response3.getLectureMentor().getLectureCount()).isEqualTo(1),
                () -> assertThat(response3.getLectureMentor().getReviewCount()).isEqualTo(4),
                () -> assertThat(response3.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response3.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),
                // TODO - TEST
                () -> assertThat(response3.getPicked()).isNull(),
                () -> assertThat(response3.getPickCount()).isEqualTo(1)
        );
    }

    @Test
    void get_paged_EnrollmentResponses() {

        // Given
        // When
        Page<EnrollmentResponse> responses = mentorLectureService.getEnrollmentResponsesOfLecture(mentorUser, lecture1.getId(), 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(3);
        for (EnrollmentResponse response : responses) {
            if (response.getEnrollmentId().equals(enrollment1.getId())) {

                assertThat(response.getMentee()).isEqualTo(menteeUser1.getNickname());
                assertThat(response.getLectureTitle()).isEqualTo(lecture1.getTitle());
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTimeUtil.getDateTimeToString(enrollment1.getCreatedAt()));

            } else if (response.getEnrollmentId().equals(enrollment2.getId())) {

                assertThat(response.getMentee()).isEqualTo(menteeUser1.getNickname());
                assertThat(response.getLectureTitle()).isEqualTo(lecture1.getTitle());
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTimeUtil.getDateTimeToString(enrollment2.getCreatedAt()));

            } else if (response.getEnrollmentId().equals(enrollment3.getId())) {

                assertThat(response.getMentee()).isEqualTo(menteeUser2.getNickname());
                assertThat(response.getLectureTitle()).isEqualTo(lecture1.getTitle());
                assertThat(response.getCreatedAt()).isEqualTo(LocalDateTimeUtil.getDateTimeToString(enrollment3.getCreatedAt()));
            }
        }
    }

    @Test
    void get_paged_MenteeResponses() {

        // Given
        // When
        Page<MenteeResponse> responses = mentorLectureService.getMenteeResponsesOfLecture(mentorUser, lecture1.getId(), 1);
        // Then
        assertThat(responses.getTotalElements()).isEqualTo(2);
        for (MenteeResponse response : responses) {
            Long userId = response.getUser().getUserId();
            if (userId.equals(menteeUser1.getId())) {

                assertThat(response.getUser().getUserId()).isEqualTo(menteeUser1.getId());
                assertThat(response.getUser().getUsername()).isEqualTo(menteeUser1.getUsername());
                assertThat(response.getUser().getRole()).isEqualTo(menteeUser1.getRole());
                assertThat(response.getUser().getName()).isEqualTo(menteeUser1.getName());
                assertThat(response.getUser().getGender()).isEqualTo(menteeUser1.getGender());
                assertThat(response.getUser().getBirthYear()).isEqualTo(menteeUser1.getBirthYear());
                assertThat(response.getUser().getPhoneNumber()).isEqualTo(menteeUser1.getPhoneNumber());
                assertThat(response.getUser().getNickname()).isEqualTo(menteeUser1.getNickname());
                assertThat(response.getUser().getImage()).isEqualTo(menteeUser1.getImage());
                assertThat(response.getUser().getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(menteeUser1.getZone()));
                assertThat(response.getSubjects()).isEqualTo(mentee1.getSubjects());

            } else if (userId.equals(menteeUser2.getId())) {

                assertThat(response.getUser().getUserId()).isEqualTo(menteeUser2.getId());
                assertThat(response.getUser().getUsername()).isEqualTo(menteeUser2.getUsername());
                assertThat(response.getUser().getRole()).isEqualTo(menteeUser2.getRole());
                assertThat(response.getUser().getName()).isEqualTo(menteeUser2.getName());
                assertThat(response.getUser().getGender()).isEqualTo(menteeUser2.getGender());
                assertThat(response.getUser().getBirthYear()).isEqualTo(menteeUser2.getBirthYear());
                assertThat(response.getUser().getPhoneNumber()).isEqualTo(menteeUser2.getPhoneNumber());
                assertThat(response.getUser().getNickname()).isEqualTo(menteeUser2.getNickname());
                assertThat(response.getUser().getImage()).isEqualTo(menteeUser2.getImage());
                assertThat(response.getUser().getZone()).isEqualTo(AddressUtils.convertEmbeddableToStringAddress(menteeUser2.getZone()));
                assertThat(response.getSubjects()).isEqualTo(mentee2.getSubjects());
            }
        }
    }

}