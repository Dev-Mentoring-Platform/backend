package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.init.TestDataBuilder.getMenteeReviewCreateRequestWithScoreAndContent;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class MenteeEnrollmentControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentees/my-enrollments";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    LoginService loginService;
    @Autowired
    MentorService mentorService;
    @Autowired
    LectureService lectureService;
    @Autowired
    PickService pickService;
    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    MenteeReviewRepository menteeReviewRepository;

    private User mentorUser;
    private User menteeUser;
    private String menteeAccessToken;

    private Lecture lecture;
    private LecturePrice lecturePrice;
//    private Enrollment enrollment;
    private Long pickId;

    @BeforeAll
    void init() {

        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        mentorUser = saveMentorUser(loginService, mentorService);
        menteeUser = saveMenteeUser(loginService);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);
        pickId = savePick(pickService, menteeUser, lecture, lecturePrice);
    }
/*
    @DisplayName("신청 강의 리스트")
    @Test
    void get_enrolled_lectures() throws Exception {

        // given
        // when
        String accessToken = getJwtToken(menteeUser.getUsername(), RoleType.MENTEE);
        // then
        mockMvc.perform(get(BASE_URL, 1)
                        .header(HEADER, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..lectureId").exists())
                .andExpect(jsonPath("$..title").exists())
                .andExpect(jsonPath("$..subTitle").exists())
                .andExpect(jsonPath("$..introduce").exists())
                .andExpect(jsonPath("$..content").exists())
                .andExpect(jsonPath("$..difficulty").exists())
                .andExpect(jsonPath("$..systems").exists())
                .andExpect(jsonPath("$..lecturePrice").exists())
                .andExpect(jsonPath("$..lecturePriceId").exists())
                .andExpect(jsonPath("$..lectureSubjects").exists())
                .andExpect(jsonPath("$..thumbnail").exists())
                .andExpect(jsonPath("$..approved").exists())
                .andExpect(jsonPath("$..closed").exists())
                .andExpect(jsonPath("$..reviewCount").exists())
                .andExpect(jsonPath("$..scoreAverage").exists())
                .andExpect(jsonPath("$..enrollmentCount").exists())
                .andExpect(jsonPath("$..lectureMentor").exists())
                .andExpect(jsonPath("$..picked").exists())
                .andExpect(jsonPath("$..pickCount").exists());
    }

    @DisplayName("수강 중인 강의 리스트 - 멘토 접근 불가")
    @Test
    void get_enrolled_lectures_as_mentor() throws Exception {

        // given
        // when
        String accessToken = getJwtToken(mentorUser.getUsername(), RoleType.MENTOR);
        // then
        mockMvc.perform(get(BASE_URL, 1)
                        .header(HEADER, accessToken))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }*/

    @DisplayName("승인 예정 강의 리스트")
    @Test
    void get_unchecked_enrollments() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/unchecked", 1)
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..enrollmentId").exists())
                .andExpect(jsonPath("$..checked").exists())
                .andExpect(jsonPath("$..finished").exists())

                .andExpect(jsonPath("$..lectureId").exists())
                .andExpect(jsonPath("$..title").exists())
                .andExpect(jsonPath("$..subTitle").exists())
                .andExpect(jsonPath("$..introduce").exists())
                .andExpect(jsonPath("$..content").exists())
                .andExpect(jsonPath("$..difficulty").exists())
                .andExpect(jsonPath("$..systems").exists())
                .andExpect(jsonPath("$..lectureSubjects").exists())
                .andExpect(jsonPath("$..thumbnail").exists())
                .andExpect(jsonPath("$..approved").exists())
                .andExpect(jsonPath("$..lectureMentor").exists())
                .andExpect(jsonPath("$..lecturePrice").exists())
                .andExpect(jsonPath("$..lecturePriceId").exists())
                .andExpect(jsonPath("$..closed").exists());
    }

    @DisplayName("승인 완료 강의 리스트")
    @Test
    void get_checked_enrollments() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(mentorUser, enrollment.getId());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/checked", 1)
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..enrollmentId").exists())
                .andExpect(jsonPath("$..checked").exists())
                .andExpect(jsonPath("$..finished").exists())

                .andExpect(jsonPath("$..lectureId").exists())
                .andExpect(jsonPath("$..title").exists())
                .andExpect(jsonPath("$..subTitle").exists())
                .andExpect(jsonPath("$..introduce").exists())
                .andExpect(jsonPath("$..content").exists())
                .andExpect(jsonPath("$..difficulty").exists())
                .andExpect(jsonPath("$..systems").exists())
                .andExpect(jsonPath("$..lectureSubjects").exists())
                .andExpect(jsonPath("$..thumbnail").exists())
                .andExpect(jsonPath("$..approved").exists())
                .andExpect(jsonPath("$..lectureMentor").exists())
                .andExpect(jsonPath("$..lecturePrice").exists())
                .andExpect(jsonPath("$..lecturePriceId").exists())
                .andExpect(jsonPath("$..closed").exists());
    }


    @DisplayName("승인 완료 강의 리스트")
    @Test
    void get_checked_enrollments_when_no_enrollment_is_checked() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/checked", 1)
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().isEmpty());
    }

    @DisplayName("승인 완료 강의 개별 조회")
    @Test
    void get_checked_enrolled_lecture() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(menteeUser, enrollment.getId());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{enrollment_id}/lecture", enrollment.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$.systems").exists())
                .andExpect(jsonPath("$.lecturePrice").exists())
                .andExpect(jsonPath("$.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.lectureSubjects").exists())
                .andExpect(jsonPath("$.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.approved").value(lecture.isApproved()))
                .andExpect(jsonPath("$.closed").value(lecturePrice.isClosed()))
                .andExpect(jsonPath("$.lectureMentor").exists())

                .andExpect(jsonPath("$.reviewCount").doesNotExist())
                .andExpect(jsonPath("$.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.enrollmentCount").doesNotExist())
                .andExpect(jsonPath("$.picked").doesNotExist())
                .andExpect(jsonPath("$.pickCount").doesNotExist());
    }

    @DisplayName("리뷰 미작성 수강내역 리스트")
    @Test
    void get_unreviewed_enrollments() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(menteeUser, enrollment.getId());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/unreviewed")
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$..mentee").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$..lectureTitle").value(lecture.getTitle()))
                .andExpect(jsonPath("$..createdAt").value(LocalDateTimeUtil.getDateTimeToString(enrollment.getCreatedAt())))

                .andExpect(jsonPath("$..lecture[0].id").value(lecture.getId()))
                .andExpect(jsonPath("$..lecture[0].title").value(lecture.getTitle()))
                .andExpect(jsonPath("$..lecture[0].subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$..lecture[0].introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$..lecture[0].difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$..lecture[0].systems").exists())
                .andExpect(jsonPath("$..lecture[0].lecturePrice").exists())
                .andExpect(jsonPath("$..lecture[0].lectureSubjects").exists())

                .andExpect(jsonPath("$..lecture[0].thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$..lecture[0].mentorNickname").value(lecture.getMentor().getUser().getNickname()))
                .andExpect(jsonPath("$..lecture[0].scoreAverage").doesNotExist())
                .andExpect(jsonPath("$..lecture[0].pickCount").doesNotExist());
    }

    @DisplayName("수강내역 조회")
    @Test
    void get_enrollment() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(menteeUser, enrollment.getId());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{enrollment_id}", enrollment.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$.mentee").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.lectureTitle").value(lecture.getTitle()))
                .andExpect(jsonPath("$.createdAt").value(LocalDateTimeUtil.getDateTimeToString(enrollment.getCreatedAt())))

                .andExpect(jsonPath("$.lecture[0].id").value(lecture.getId()))
                .andExpect(jsonPath("$.lecture[0].title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.lecture[0].subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.lecture[0].introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.lecture[0].difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$.lecture[0].systems").exists())
                .andExpect(jsonPath("$.lecture[0].lecturePrice").exists())
                .andExpect(jsonPath("$.lecture[0].lectureSubjects").exists())

                .andExpect(jsonPath("$.lecture[0].thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.lecture[0].mentorNickname").value(lecture.getMentor().getUser().getNickname()))
                .andExpect(jsonPath("$.lecture[0].scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.lecture[0].pickCount").doesNotExist());
    }

    @DisplayName("리뷰 작성")
    @Test
    void new_review() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(menteeUser, enrollment.getId());

        // when
        // then
        MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequestWithScoreAndContent(3, "good");
        mockMvc.perform(post(BASE_URL + "/{enrollment_id}/reviews", enrollment.getId())
                        .header(AUTHORIZATION, menteeAccessToken)
                        .content(objectMapper.writeValueAsString(menteeReviewCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        assertNotNull(menteeReviewRepository.findByEnrollment(enrollment));
    }


    @DisplayName("리뷰 작성 - invalid input")
    @Test
    void new_review_with_invalid_input() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(menteeUser, enrollment.getId());

        // when
        // then
        MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequestWithScoreAndContent(6, "");
        mockMvc.perform(post(BASE_URL + "/{enrollment_id}/reviews", enrollment.getId())
                        .header(AUTHORIZATION, menteeAccessToken)
                        .content(objectMapper.writeValueAsString(menteeReviewCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        assertNull(menteeReviewRepository.findByEnrollment(enrollment));
    }
}