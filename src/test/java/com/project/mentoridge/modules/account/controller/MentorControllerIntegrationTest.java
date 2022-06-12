package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.CareerRepository;
import com.project.mentoridge.modules.account.repository.EducationRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.TOKEN_PREFIX;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class MentorControllerIntegrationTest {

    private final String BASE_URL = "/api/mentors";

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginService loginService;
    @Autowired
    JwtTokenManager jwtTokenManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CareerRepository careerRepository;
    @Autowired
    EducationRepository educationRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    LectureService lectureService;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MentorReviewService mentorReviewService;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    private User mentorUser;
    private User menteeUser;
    private Lecture lecture;
    private LecturePrice lecturePrice;
    private Enrollment enrollment;
    private MenteeReview menteeReview;
    private MentorReview mentorReview;

    @BeforeAll
    void init() {

        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        mentorUser = saveMentorUser(loginService, mentorService);
        menteeUser = saveMenteeUser(loginService);

        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);

        enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);
    }

    private String getJwtToken(String username, RoleType roleType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", roleType.getType());
        return TOKEN_PREFIX + jwtTokenManager.createToken(USERNAME, claims);
    }

    @Test
    void getMentors() throws Exception {
        // Given
        // When
        String accessToken = getJwtToken(menteeUser.getUsername(), RoleType.MENTEE);
        // Then
        mockMvc.perform(get(BASE_URL)
                        .header(HEADER, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$..mentorId").exists())

                .andExpect(jsonPath("$..user").exists())
                .andExpect(jsonPath("$..bio").exists())
                .andExpect(jsonPath("$..careers").isArray())
                .andExpect(jsonPath("$..careers", hasSize(1)))
                .andExpect(jsonPath("$..educations", hasSize(1)))
                .andExpect(jsonPath("$..accumulatedMenteeCount").exists());
    }

    @Test
    void getMyInfo() throws Exception {

    }

    @Test
    void getMentor() throws Exception {

    }

    @WithAccount(NAME)
    @Test
    void newMentor() throws Exception {

        // Given
        // When
        String content = objectMapper.writeValueAsString(mentorSignUpRequest);
        // System.out.println(content);
        mockMvc.perform(post(BASE_URL)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertEquals(RoleType.MENTOR, user.getRole());
        Mentor mentor = mentorRepository.findByUser(user);
        assertAll(
                () -> assertNotNull(mentor),
                () -> assertEquals(1, careerRepository.findByMentor(mentor).size()),
                () -> assertEquals(1, educationRepository.findByMentor(mentor).size())
        );
    }

    @DisplayName("Mentor 등록 - Invalid Input")
    @WithAccount(NAME)
    @Test
    public void newMentor_withInvalidInput() throws Exception {

        // Given
        // When
        // Then
//        CareerCreateRequest careerCreateRequest = CareerCreateRequest.of(
//                "mentoridge",
//                null,
//                "2007-12-03",
//                "",
//                true
//        );
//
//        mentorSignUpRequest.addCareerCreateRequest(careerCreateRequest);
//        mockMvc.perform(post(BASE_URL)
//                .content(objectMapper.writeValueAsString(mentorSignUpRequest))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(jsonPath("$.message").value("Invalid Input"))
//                .andExpect(jsonPath("$.code").value(400));
    }

    @Disabled
    @Test
    @DisplayName("Mentor 등록 - 인증된 사용자 X")
    public void newMentor_withoutAuthenticatedUser() throws Exception {

        // Given
        User user = loginService.signUp(signUpRequest);
        loginService.verifyEmail(user.getUsername(), user.getEmailVerifyToken());

        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                .content(objectMapper.writeValueAsString(mentorSignUpRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @WithAccount(NAME)
    @Test
    void Mentor_수정() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertEquals(RoleType.MENTEE, user.getRole());
        mentorService.createMentor(user, mentorSignUpRequest);

        // When
        mockMvc.perform(put(BASE_URL + "/my-info")
                .content(objectMapper.writeValueAsString(mentorUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        assertEquals(RoleType.MENTOR, user.getRole());

        Mentor mentor = mentorRepository.findByUser(user);
        // TODO - career, education 확인
    }

    // TODO - Mentor 삭제 시 연관 엔티티 전체 삭제
    @WithAccount(NAME)
    @Test
    void Mentor_탈퇴() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertEquals(RoleType.MENTEE, user.getRole());

        Mentor mentor = mentorService.createMentor(user, mentorSignUpRequest);
        List<Long> careerIds = careerRepository.findByMentor(mentor).stream()
                .map(career -> career.getId()).collect(Collectors.toList());
        List<Long> educationIds = educationRepository.findByMentor(mentor).stream()
                .map(education -> education.getId()).collect(Collectors.toList());

        // When
        mockMvc.perform(delete(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        user = userRepository.findByUsername(USERNAME).orElse(null);
        assertEquals(RoleType.MENTEE, user.getRole());

        // mentor
        assertNull(mentorRepository.findByUser(user));
        // career
        for (Long careerId : careerIds) {
            assertFalse(careerRepository.findById(careerId).isPresent());
        }
        // education
        for (Long educationId : educationIds) {
            assertFalse(educationRepository.findById(educationId).isPresent());
        }
        // chatroom
        // message
        // lecture - lecturePrice, lectureSubject
        // enrollment, pick, review


    }

    // TODO - Mentor 삭제 시 연관 엔티티 전체 삭제
    @WithAccount(NAME)
    @Test
    @DisplayName("Mentor 탈퇴 - 멘토가 아닌 경우")
    void quitMentor_notMentor() throws Exception {

        // Given

        // When

        // Then
    }

    void getCareers() {}

    void getEducations() {

    }

    
    void getLectures() {
        // 강의 가격별로 출력    
    }
    
    void getLecture() {
        
    }
    
    @DisplayName("멘토의 후기 조회")
    @Test
    void getReviews() {
        
    }

}