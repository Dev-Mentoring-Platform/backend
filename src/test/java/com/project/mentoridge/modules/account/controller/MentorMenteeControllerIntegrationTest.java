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
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.menteeReviewCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.mentorReviewCreateRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
public class MentorMenteeControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentors/my-mentees";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginService loginService;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;

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
    MentorReviewService mentorReviewService;

    private User menteeUser1;
    private Mentee mentee1;
    private String menteeAccessToken1;
    private User menteeUser2;
    private Mentee mentee2;
    private String menteeAccessToken2;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessToken;

    private Lecture lecture;
    private LecturePrice lecturePrice1;
    private LecturePrice lecturePrice2;

    private Enrollment enrollment1;
    private Enrollment enrollment2;

    @BeforeAll
    @Override
    protected void init() {
        super.init();

        menteeUser1 = saveMenteeUser("menteeUser1", loginService);
        mentee1 = menteeRepository.findByUser(menteeUser1);
        menteeAccessToken1 = getAccessToken(menteeUser1.getUsername(), RoleType.MENTEE);
        menteeUser2 = saveMenteeUser("menteeUser2", loginService);
        mentee2 = menteeRepository.findByUser(menteeUser2);
        menteeAccessToken2 = getAccessToken(menteeUser2.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessToken = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);

        lecture = saveLecture(lectureService, mentorUser);
        List<LecturePrice> lecturePrices = lecturePriceRepository.findByLecture(lecture);
        lecturePrice1 = lecturePrices.get(0);
        lecturePrice2 = lecturePrices.get(1);
        // ?????? ??????
        lecture.approve(lectureLogService);

        enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture.getId(), lecturePrice1.getId());
        enrollmentService.check(mentorUser, enrollment1.getId());
        enrollment2 = enrollmentService.createEnrollment(menteeUser2, lecture.getId(), lecturePrice2.getId());
        enrollmentService.check(mentorUser, enrollment2.getId());

        // ?????? ??????
        lectureService.close(mentorUser, lecture.getId(), lecturePrice1.getId());
    }

    @DisplayName("????????? ?????? ????????? - no closed param")
    @Test
    void get_my_mentees_of_closed_lecture_with_no_closed_param() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].menteeId").value(mentee1.getId()))
                .andExpect(jsonPath("$.[0].userId").value(menteeUser1.getId()))
                .andExpect(jsonPath("$.[0].name").value(menteeUser1.getName()))
                .andExpect(jsonPath("$.[0].nickname").value(menteeUser1.getNickname()))
                .andExpect(jsonPath("$.[0].enrollmentId").value(enrollment1.getId()))
                .andExpect(jsonPath("$.[1].menteeId").value(mentee2.getId()))
                .andExpect(jsonPath("$.[1].userId").value(menteeUser2.getId()))
                .andExpect(jsonPath("$.[1].name").value(menteeUser2.getName()))
                .andExpect(jsonPath("$.[1].nickname").value(menteeUser2.getNickname()))
                .andExpect(jsonPath("$.[1].enrollmentId").value(enrollment2.getId()));
    }

    @DisplayName("????????? ?????? ????????? - ?????? ?????? ??????")
    @Test
    void get_my_mentees_of_closed_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessToken)
                        .param("closed", String.valueOf(true)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].menteeId").value(mentee1.getId()))
                .andExpect(jsonPath("$.[0].userId").value(menteeUser1.getId()))
                .andExpect(jsonPath("$.[0].name").value(menteeUser1.getName()))
                .andExpect(jsonPath("$.[0].nickname").value(menteeUser1.getNickname()))
                .andExpect(jsonPath("$.[0].enrollmentId").value(enrollment1.getId()));
    }

    @DisplayName("????????? ?????? ????????? - ?????? ????????? ??????")
    @Test
    void get_my_mentees_of_not_closed_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                .header(AUTHORIZATION, mentorAccessToken)
                .param("closed", String.valueOf(false)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].menteeId").value(mentee2.getId()))
                .andExpect(jsonPath("$.[0].userId").value(menteeUser2.getId()))
                .andExpect(jsonPath("$.[0].name").value(menteeUser2.getName()))
                .andExpect(jsonPath("$.[0].nickname").value(menteeUser2.getNickname()))
                .andExpect(jsonPath("$.[0].enrollmentId").value(enrollment2.getId()));
    }

    @DisplayName("??????-?????? ?????????")
    @Test
    void get_paged_enrollmentInfo_of_my_mentees() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}", mentee2.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].menteeId").value(mentee2.getId()))
                .andExpect(jsonPath("$.[0].enrollmentId").value(menteeUser2.getId()))
                // lecture
                .andExpect(jsonPath("$.[0].lecture").exists())
                .andExpect(jsonPath("$.[0].lecture.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.[0].lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.[0].lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.[0].lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.[0].lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.[0].lecture.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.[0].lecture.difficulty").value(lecture.getDifficulty()))
                // lecturePrice
                .andExpect(jsonPath("$.[0].lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.lecturePriceId").value(lecturePrice2.getId()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.isGroup").value(lecturePrice2.isGroup()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.numberOfMembers").value(lecturePrice2.getNumberOfMembers()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.pricePerHour").value(lecturePrice2.getPricePerHour()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.timePerLecture").value(lecturePrice2.getTimePerLecture()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.numberOfLectures").value(lecturePrice2.getNumberOfLectures()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.totalPrice").value(lecturePrice2.getTotalPrice()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.isGroupStr").value(lecturePrice2.isGroup() ? "????????????" : "1:1 ????????????"))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.content").value(String.format("????????? %d??? x 1??? %d?????? x ??? %d??? ?????? ??????",
                        lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.closed").value(lecturePrice2.isClosed()))

                .andExpect(jsonPath("$.[0].lecture.systemTypes").exists());

    }

    @Test
    void get_enrollmentInfo_of_my_mentee() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}/enrollments/{enrollment_id}", mentee2.getId(), enrollment2.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menteeId").value(mentee2.getId()))
                .andExpect(jsonPath("$.enrollmentId").value(menteeUser2.getId()))
                // lecture
                .andExpect(jsonPath("$.lecture").exists())
                .andExpect(jsonPath("$.lecture.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.lecture.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.lecture.difficulty").value(lecture.getDifficulty()))
                // lecturePrice
                .andExpect(jsonPath("$.lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.lecture.lecturePrice.lecturePriceId").value(lecturePrice2.getId()))
                .andExpect(jsonPath("$.lecture.lecturePrice.isGroup").value(lecturePrice2.isGroup()))
                .andExpect(jsonPath("$.lecture.lecturePrice.numberOfMembers").value(lecturePrice2.getNumberOfMembers()))
                .andExpect(jsonPath("$.lecture.lecturePrice.pricePerHour").value(lecturePrice2.getPricePerHour()))
                .andExpect(jsonPath("$.lecture.lecturePrice.timePerLecture").value(lecturePrice2.getTimePerLecture()))
                .andExpect(jsonPath("$.lecture.lecturePrice.numberOfLectures").value(lecturePrice2.getNumberOfLectures()))
                .andExpect(jsonPath("$.lecture.lecturePrice.totalPrice").value(lecturePrice2.getTotalPrice()))
                .andExpect(jsonPath("$.lecture.lecturePrice.isGroupStr").value(lecturePrice2.isGroup() ? "????????????" : "1:1 ????????????"))
                .andExpect(jsonPath("$.lecture.lecturePrice.content").value(String.format("????????? %d??? x 1??? %d?????? x ??? %d??? ?????? ??????",
                        lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())))
                .andExpect(jsonPath("$.lecture.lecturePrice.closed").value(lecturePrice2.isClosed()))

                .andExpect(jsonPath("$.lecture.systemTypes").exists());
    }

    @Test
    void get_review_of_my_mentee() throws Exception {

        // given
        MenteeReview menteeReview1 = menteeReviewService.createMenteeReview(menteeUser1, enrollment1.getId(), menteeReviewCreateRequest);
        MentorReview mentorReview1 = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), menteeReview1.getId(), mentorReviewCreateRequest);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}/enrollments/{enrollment_id}/reviews/{mentee_review_id}", mentee1.getId(), enrollment1.getId())
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menteeReviewId").value(menteeReview1.getId()))
                .andExpect(jsonPath("$.enrollmentId").value(enrollment1.getId()))
                .andExpect(jsonPath("$.score").value(menteeReview1.getScore()))
                .andExpect(jsonPath("$.content").value(menteeReview1.getContent()))
                .andExpect(jsonPath("$.username").value(menteeUser1.getUsername()))
                .andExpect(jsonPath("$.userNickname").value(menteeUser1.getNickname()))
                .andExpect(jsonPath("$.userImage").value(menteeUser1.getImage()))
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

    @DisplayName("?????? ????????? (?????? ???????????? ??????) ?????? ?????????")
    @Test
    void get_my_unchecked_mentees() throws Exception {

        // given
        // ?????????
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser2, lecture.getId(), lecturePrice1.getId());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/unchecked")
                        .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].menteeId").value(mentee2.getId()))
                .andExpect(jsonPath("$.[0].userId").value(menteeUser2.getId()))
                .andExpect(jsonPath("$.[0].name").value(menteeUser2.getName()))
                .andExpect(jsonPath("$.[0].nickname").value(menteeUser2.getNickname()))
                .andExpect(jsonPath("$.[0].enrollmentId").value(enrollment.getId()));
    }

}
