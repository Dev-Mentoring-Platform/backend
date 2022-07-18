package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.mentorReviewCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.mentorReviewUpdateRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
public class MentorLectureControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final String BASE_URL = "/api/mentors/my-lectures";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    LectureRepository lectureRepository;
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
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;

    private User menteeUser;
    private Mentee mentee;
    private String menteeAccessToken;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessToken;

    private Lecture lecture;
    private LecturePrice lecturePrice1;
    private LecturePrice lecturePrice2;

    private Enrollment enrollment1;
    private MenteeReview menteeReview1;
    private MentorReview mentorReview1;

    private Enrollment enrollment2;
    private MenteeReview menteeReview2;

    @BeforeEach
    void init() {

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessToken = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);

        lecture = saveLecture(lectureService, mentorUser);
        List<LecturePrice> lecturePrices = lecturePriceRepository.findByLecture(lecture);
        lecturePrice1 = lecturePrices.get(0);
        lecturePrice2 = lecturePrices.get(1);
        // 강의 승인
        lecture.approve(lectureLogService);

        enrollment1 = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice1.getId());
        enrollmentService.check(mentorUser, enrollment1.getId());
        menteeReview1 = menteeReviewService.createMenteeReview(menteeUser, enrollment1.getId(), MenteeReviewCreateRequest.builder()
                .score(5)
                .content("좋아요")
                .build());
        mentorReview1 = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), menteeReview1.getId(), mentorReviewCreateRequest);

        enrollment2 = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice2.getId());
        enrollmentService.check(mentorUser, enrollment2.getId());
        menteeReview2 = menteeReviewService.createMenteeReview(menteeUser, enrollment2.getId(), MenteeReviewCreateRequest.builder()
                .score(1)
                .content("싫어요")
                .build());
    }

    @Test
    void get_lectures() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..id").value(lecture.getId()))
                .andExpect(jsonPath("$..title").value(lecture.getTitle()))
                .andExpect(jsonPath("$..subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$..introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$..content").value(lecture.getContent()))
                .andExpect(jsonPath("$..difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$..systems").exists())
                .andExpect(jsonPath("$..lectureSubjects").exists())
                .andExpect(jsonPath("$..thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$..approved").value(lecture.isApproved()))
                // lecturePrices
                .andExpect(jsonPath("$..lecturePrices").exists())
                .andExpect(jsonPath("$..lecturePrice[0].lecturePriceId").value(lecturePrice1.getId()))
                .andExpect(jsonPath("$..lecturePrice[0].isGroup").value(lecturePrice1.isGroup()))
                .andExpect(jsonPath("$..lecturePrice[0].numberOfMembers").value(lecturePrice1.getNumberOfMembers()))
                .andExpect(jsonPath("$..lecturePrice[0].pricePerHour").value(lecturePrice1.getPricePerHour()))
                .andExpect(jsonPath("$..lecturePrice[0].timePerLecture").value(lecturePrice1.getTimePerLecture()))
                .andExpect(jsonPath("$..lecturePrice[0].numberOfLectures").value(lecturePrice1.getNumberOfLectures()))
                .andExpect(jsonPath("$..lecturePrice[0].totalPrice").value(lecturePrice1.getTotalPrice()))
                .andExpect(jsonPath("$..lecturePrice[0].isGroupStr").value(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$..lecturePrice[0].content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())))
                .andExpect(jsonPath("$..lecturePrice[0].closed").value(lecturePrice1.isClosed()))
                .andExpect(jsonPath("$..lecturePriceId").value(lecturePrice1.getId()))
                .andExpect(jsonPath("$..closed").value(lecturePrice1.isClosed()))

                .andExpect(jsonPath("$..lecturePrice[1].lecturePriceId").value(lecturePrice2.getId()))
                .andExpect(jsonPath("$..lecturePrice[1].isGroup").value(lecturePrice2.isGroup()))
                .andExpect(jsonPath("$..lecturePrice[1].numberOfMembers").value(lecturePrice2.getNumberOfMembers()))
                .andExpect(jsonPath("$..lecturePrice[1].pricePerHour").value(lecturePrice2.getPricePerHour()))
                .andExpect(jsonPath("$..lecturePrice[1].timePerLecture").value(lecturePrice2.getTimePerLecture()))
                .andExpect(jsonPath("$..lecturePrice[1].numberOfLectures").value(lecturePrice2.getNumberOfLectures()))
                .andExpect(jsonPath("$..lecturePrice[1].totalPrice").value(lecturePrice2.getTotalPrice()))
                .andExpect(jsonPath("$..lecturePrice[1].isGroupStr").value(lecturePrice2.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$..lecturePrice[1].content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())))
                .andExpect(jsonPath("$..lecturePrice[1].closed").value(lecturePrice2.isClosed()))
                .andExpect(jsonPath("$..lecturePriceId").value(lecturePrice2.getId()))
                .andExpect(jsonPath("$..closed").value(lecturePrice2.isClosed()))

                // lectureMentor
                .andExpect(jsonPath("$..lectureMentor").exists())
                .andExpect(jsonPath("$..lectureMentor.mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$..lectureMentor.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$..lectureMentor.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$..lectureMentor.lectureCount").value(1L))
                .andExpect(jsonPath("$..lectureMentor.reviewCount").value(2L))

                .andExpect(jsonPath("$..reviewCount").value(2L))
                .andExpect(jsonPath("$..scoreAverage").value(3.0))
                .andExpect(jsonPath("$..enrollmentCount").value(2L));
    }

    @Test
    void get_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}", lecture.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lecture.getId()))
                .andExpect(jsonPath("$.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$.systems").exists())
                .andExpect(jsonPath("$.lectureSubjects").exists())
                .andExpect(jsonPath("$.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.approved").value(lecture.isApproved()))
                // lecturePrices
                .andExpect(jsonPath("$.lecturePrices").exists())
                .andExpect(jsonPath("$.lecturePrice[0].lecturePriceId").value(lecturePrice1.getId()))
                .andExpect(jsonPath("$.lecturePrice[0].isGroup").value(lecturePrice1.isGroup()))
                .andExpect(jsonPath("$.lecturePrice[0].numberOfMembers").value(lecturePrice1.getNumberOfMembers()))
                .andExpect(jsonPath("$.lecturePrice[0].pricePerHour").value(lecturePrice1.getPricePerHour()))
                .andExpect(jsonPath("$.lecturePrice[0].timePerLecture").value(lecturePrice1.getTimePerLecture()))
                .andExpect(jsonPath("$.lecturePrice[0].numberOfLectures").value(lecturePrice1.getNumberOfLectures()))
                .andExpect(jsonPath("$.lecturePrice[0].totalPrice").value(lecturePrice1.getTotalPrice()))
                .andExpect(jsonPath("$.lecturePrice[0].isGroupStr").value(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.lecturePrice[0].content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())))
                .andExpect(jsonPath("$.lecturePrice[0].closed").value(lecturePrice1.isClosed()))
                .andExpect(jsonPath("$.lecturePriceId").value(lecturePrice1.getId()))
                .andExpect(jsonPath("$.closed").value(lecturePrice1.isClosed()))

                .andExpect(jsonPath("$.lecturePrice[1].lecturePriceId").value(lecturePrice2.getId()))
                .andExpect(jsonPath("$.lecturePrice[1].isGroup").value(lecturePrice2.isGroup()))
                .andExpect(jsonPath("$.lecturePrice[1].numberOfMembers").value(lecturePrice2.getNumberOfMembers()))
                .andExpect(jsonPath("$.lecturePrice[1].pricePerHour").value(lecturePrice2.getPricePerHour()))
                .andExpect(jsonPath("$.lecturePrice[1].timePerLecture").value(lecturePrice2.getTimePerLecture()))
                .andExpect(jsonPath("$.lecturePrice[1].numberOfLectures").value(lecturePrice2.getNumberOfLectures()))
                .andExpect(jsonPath("$.lecturePrice[1].totalPrice").value(lecturePrice2.getTotalPrice()))
                .andExpect(jsonPath("$.lecturePrice[1].isGroupStr").value(lecturePrice2.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.lecturePrice[1].content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행",
                        lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())))
                .andExpect(jsonPath("$.lecturePrice[1].closed").value(lecturePrice2.isClosed()))
                .andExpect(jsonPath("$.lecturePriceId").value(lecturePrice2.getId()))
                .andExpect(jsonPath("$.closed").value(lecturePrice2.isClosed()))

                // lectureMentor
                .andExpect(jsonPath("$..lectureMentor").exists())
                .andExpect(jsonPath("$..lectureMentor.mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$..lectureMentor.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$..lectureMentor.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$..lectureMentor.lectureCount").value(1L))
                .andExpect(jsonPath("$..lectureMentor.reviewCount").value(2L))

                .andExpect(jsonPath("$..reviewCount").value(2L))
                .andExpect(jsonPath("$..scoreAverage").value(3.0))
                .andExpect(jsonPath("$..enrollmentCount").value(2L));
    }

    @Test
    void get_reviews_of_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews", lecture.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                // 최신순
                .andExpect(jsonPath("$.[0].menteeReviewId").value(menteeReview2.getId()))
                .andExpect(jsonPath("$.[0].enrollmentId").value(enrollment2.getId()))
                .andExpect(jsonPath("$.[0].score").value(menteeReview2.getScore()))
                .andExpect(jsonPath("$.[0].content").value(menteeReview2.getContent()))
                .andExpect(jsonPath("$.[0].username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.[0].userNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.[0].userImage").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.[0].createdAt").exists())
                // child
                .andExpect(jsonPath("$.[0].child").doesNotExist())

                .andExpect(jsonPath("$.[1].menteeReviewId").value(menteeReview1.getId()))
                .andExpect(jsonPath("$.[1].enrollmentId").value(enrollment1.getId()))
                .andExpect(jsonPath("$.[1].score").value(menteeReview1.getScore()))
                .andExpect(jsonPath("$.[1].content").value(menteeReview1.getContent()))
                .andExpect(jsonPath("$.[1].username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.[1].userNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.[1].userImage").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.[1].createdAt").exists())
                // child
                .andExpect(jsonPath("$.[1].child").exists())
                .andExpect(jsonPath("$.[1].child.mentorReviewId").value(mentorReview1.getId()))
                .andExpect(jsonPath("$.[1].child.content").value(mentorReview1.getContent()))
                .andExpect(jsonPath("$.[1].child.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.[1].child.userNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.[1].child.userImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.[1].child.createdAt").exists());
    }

    @Test
    void get_review_of_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", lecture.getId(), menteeReview1.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menteeReviewId").value(menteeReview1.getId()))
                .andExpect(jsonPath("$.enrollmentId").value(enrollment1.getId()))
                .andExpect(jsonPath("$.score").value(menteeReview1.getScore()))
                .andExpect(jsonPath("$.content").value(menteeReview1.getContent()))
                .andExpect(jsonPath("$.username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.userNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.userImage").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.createdAt").exists())
                // child
                .andExpect(jsonPath("$.child").exists())
                .andExpect(jsonPath("$.child.mentorReviewId").value(mentorReview1.getId()))
                .andExpect(jsonPath("$.child.content").value(mentorReview1.getContent()))
                .andExpect(jsonPath("$.child.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.child.userNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.child.userImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.child.createdAt").exists());
    }

    @DisplayName("멘토 리뷰 작성")
    @Test
    void new_review() throws Exception {

        // Given
        // When
        mockMvc.perform(post(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", lecture.getId(), menteeReview2.getId())
                .header(AUTHORIZATION, mentorAccessToken)
                .content(objectMapper.writeValueAsString(mentorReviewCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        // Then
        MentorReview created = mentorReviewRepository.findByParent(menteeReview2).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertThat(created.getMentor()).isEqualTo(mentor),
                () -> assertThat(created.getParent()).isEqualTo(menteeReview2),
                () -> assertThat(created.getContent()).isEqualTo(mentorReviewCreateRequest.getContent())
        );
    }

    @Test
    void edit_review() throws Exception {

        // Given
        // When
        mockMvc.perform(put(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}/children/{mentor_review_id}", lecture.getId(), menteeReview1.getId(), mentorReview1.getId())
                .header(AUTHORIZATION, mentorAccessToken)
                .content(objectMapper.writeValueAsString(mentorReviewUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        MentorReview updated = mentorReviewRepository.findByParent(menteeReview1).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertThat(updated.getMentor()).isEqualTo(mentor),
                () -> assertThat(updated.getParent()).isEqualTo(menteeReview1),
                () -> assertThat(updated.getContent()).isEqualTo(mentorReviewUpdateRequest.getContent())
        );
    }

    @Test
    void delete_review() throws Exception {

        // Given
        // When
        mockMvc.perform(delete(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}/children/{mentor_review_id}", lecture.getId(), menteeReview1.getId(), mentorReview1.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        assertThat(mentorReviewRepository.findByParent(menteeReview1).isPresent()).isFalse();
    }

    @Test
    void get_mentees_of_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/mentees", lecture.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                // user
                .andExpect(jsonPath("$.[0].user").exists())
                .andExpect(jsonPath("$.[0].user.userId").value(menteeUser.getId()))
                .andExpect(jsonPath("$.[0].user.username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.[0].user.role").value(menteeUser.getRole()))
                .andExpect(jsonPath("$.[0].user.name").value(menteeUser.getName()))
                .andExpect(jsonPath("$.[0].user.gender").value(menteeUser.getGender()))
                .andExpect(jsonPath("$.[0].user.birthYear").value(menteeUser.getBirthYear()))
                .andExpect(jsonPath("$.[0].user.phoneNumber").value(menteeUser.getPhoneNumber()))
                .andExpect(jsonPath("$.[0].user.nickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.[0].user.image").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.[0].user.zone").value(AddressUtils.convertEmbeddableToStringAddress(menteeUser.getZone())))

                .andExpect(jsonPath("$.[0].subjects").value(mentee.getSubjects()));
    }

    @Test
    void get_enrollments_of_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/enrollments", lecture.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].enrollmentId").value(enrollment1.getId()))
                .andExpect(jsonPath("$.[0].mentee").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.[0].lectureTitle").value(lecture.getTitle()))
                .andExpect(jsonPath("$.[0].createdAt").exists())
                .andExpect(jsonPath("$.[1].enrollmentId").value(enrollment2.getId()))
                .andExpect(jsonPath("$.[1].mentee").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.[1].lectureTitle").value(lecture.getTitle()))
                .andExpect(jsonPath("$.[1].createdAt").exists());
    }

    @DisplayName("강의 모집 종료")
    @Test
    void close_each_lecture() throws Exception {

        // Given
        // When
        mockMvc.perform(put(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}/close", lecture.getId(), lecturePrice1.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        LecturePrice updated = lecturePriceRepository.findById(lecturePrice1.getId()).orElseThrow(RuntimeException::new);
        assertThat(updated.isClosed()).isTrue();
    }

    @DisplayName("강의 모집 개시")
    @Test
    void open_each_lecture() throws Exception {

        // Given
        lectureService.close(mentorUser, lecture.getId(), lecturePrice1.getId());
        // When
        mockMvc.perform(put(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}/open", lecture.getId(), lecturePrice1.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        LecturePrice updated = lecturePriceRepository.findById(lecturePrice1.getId()).orElseThrow(RuntimeException::new);
        assertThat(updated.isClosed()).isFalse();
    }
}
