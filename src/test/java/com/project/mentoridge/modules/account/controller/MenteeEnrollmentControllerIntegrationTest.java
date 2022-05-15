package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.init.TestDataBuilder.getMenteeReviewCreateRequestWithScoreAndContent;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.TOKEN_PREFIX;
import static com.project.mentoridge.modules.account.controller.ControllerIntegrationTest.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class MenteeEnrollmentControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentees/my-enrollments";

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    JwtTokenManager jwtTokenManager;


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

    private User mentorUser;
    private User menteeUser;
    private Lecture lecture;
    private LecturePrice lecturePrice;
    private Enrollment enrollment;
    private Long enrollmentId;

    @BeforeAll
    void init() {

        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        mentorUser = saveMentorUser(loginService, mentorService);
        menteeUser = saveMenteeUser(loginService);

        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);

        savePick(pickService, menteeUser, lecture, lecturePrice);
        enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentId = enrollment.getId();
    }

    private String getJwtToken(String username, RoleType roleType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", roleType.getType());
        return TOKEN_PREFIX + jwtTokenManager.createToken(USERNAME, claims);
    }

    @DisplayName("수강 중인 강의 리스트")
    @Test
    void getEnrolledLectures() throws Exception {

        // given
        // when
        String jwtToken = getJwtToken(menteeUser.getUsername(), RoleType.MENTEE);
        // then
        mockMvc.perform(get(BASE_URL, 1)
                        .header(HEADER, jwtToken))
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
    void getEnrolledLectures_byMentor() throws Exception {

        // given
        // when
        String jwtToken = getJwtToken(mentorUser.getUsername(), RoleType.MENTOR);
        // then
        mockMvc.perform(get(BASE_URL, 1)
                        .header(HEADER, jwtToken))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

    @DisplayName("수강 중인 강의 개별 조회")
    @Test
    void getEnrolledLecture() throws Exception {

        // given
        // when
        String jwtToken = getJwtToken(menteeUser.getUsername(), RoleType.MENTEE);
        // then
        mockMvc.perform(get(BASE_URL + "/{enrollment_id}/lecture", enrollmentId)
                        .header(HEADER, jwtToken))
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

                .andExpect(jsonPath("$.reviewCount").value(0L))
                .andExpect(jsonPath("$.scoreAverage").value(0.0))
                .andExpect(jsonPath("$.enrollmentCount").value(1L))
                .andExpect(jsonPath("$.lectureMentor").exists())
                .andExpect(jsonPath("$.picked").value(true))
                .andExpect(jsonPath("$.pickCount").value(1L));
    }

    @DisplayName("리뷰 미작성 수강내역 리스트")
    @Test
    void get_unreviewedLectures_of_mentee() throws Exception {

        // given
        // when
        String jwtToken = getJwtToken(menteeUser.getUsername(), RoleType.MENTEE);
        // then
        mockMvc.perform(get(BASE_URL + "/unreviewed")
                        .header(HEADER, jwtToken))
                .andDo(print())
                .andExpect(status().isOk())
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
                .andExpect(jsonPath("$..lecture[0].scoreAverage").value(0.0))
                .andExpect(jsonPath("$..lecture[0].pickCount").value(1L));
    }

    @DisplayName("수강내역 조회")
    @Test
    void get_enrollment() throws Exception {

        // given
        // when
        String jwtToken = getJwtToken(menteeUser.getUsername(), RoleType.MENTEE);
        // then
        mockMvc.perform(get(BASE_URL + "/{enrollment_id}", enrollmentId)
                        .header(HEADER, jwtToken))
                .andDo(print())
                .andExpect(status().isOk())
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
                .andExpect(jsonPath("$..lecture[0].scoreAverage").value(0.0))
                .andExpect(jsonPath("$..lecture[0].pickCount").value(1L));
    }

    @DisplayName("리뷰 작성")
    @Test
    void new_review() throws Exception {

        // given
        // when
        String jwtToken = getJwtToken(menteeUser.getUsername(), RoleType.MENTEE);
        MenteeReviewCreateRequest createRequest = getMenteeReviewCreateRequestWithScoreAndContent(3, "good");

        // then
        mockMvc.perform(post(BASE_URL + "/{enrollment_id}/reviews", enrollmentId)
                        .header(HEADER, jwtToken)
                        .content(objectMapper.writeValueAsString(createRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}