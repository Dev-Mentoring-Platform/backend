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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.getLecturePrice;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
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

    @BeforeEach
    void init() {

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessToken = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);
    }

    @Test
    void get_lectures() throws Exception {

        // given
        Lecture lecture = saveLecture(lectureService, mentorUser);
        LecturePrice lecturePrice = getLecturePrice(lecture);
        // 강의 승인
        lecture.approve(lectureLogService);

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
                // lecturePrice
                .andExpect(jsonPath("$..lecturePrice").exists())
                .andExpect(jsonPath("$..lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$..lecturePrice.isGroup").value(lecturePrice.isGroup()))
                .andExpect(jsonPath("$..lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$..lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$..lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$..lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$..lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$..lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$..lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$..lecturePrice.closed").value(lecturePrice.isClosed()))
                .andExpect(jsonPath("$..lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$..closed").value(lecturePrice.isClosed()))
                // lectureMentor
                .andExpect(jsonPath("$..lectureMentor").exists())
                .andExpect(jsonPath("$..lectureMentor.mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$..lectureMentor.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$..lectureMentor.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$..lectureMentor.lectureCount").value(1L))
                .andExpect(jsonPath("$..lectureMentor.reviewCount").value(0L))

                .andExpect(jsonPath("$..reviewCount").value(0L))
                .andExpect(jsonPath("$..scoreAverage").value(0.0))
                .andExpect(jsonPath("$..enrollmentCount").value(0L))
                .andExpect(jsonPath("$..picked").value(false))
                .andExpect(jsonPath("$..pickCount").value(0L));
    }

    @Test
    void get_lecture() throws Exception {

    }

    @Test
    void get_reviews_of_lecture() throws Exception {

    }

    @Test
    void get_review_of_lecture() throws Exception {

    }

    @DisplayName("멘토 리뷰 작성")
    @Test
    void new_review() throws Exception {

    }

    @Test
    void edit_review() throws Exception {

    }

    @Test
    void delete_review() throws Exception {

    }

    @Test
    void get_mentees_of_lecture() throws Exception {

    }

    @Test
    void get_enrollments_of_lecture() throws Exception {

    }

    @DisplayName("강의 모집 종료")
    @Test
    void close_each_lecture() throws Exception {

    }

    @DisplayName("강의 모집 개시")
    @Test
    void open_each_lecture() throws Exception {

    }
}
