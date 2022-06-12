package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.security.jwt.JwtTokenManager;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.HEADER;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.TOKEN_PREFIX;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class MenteePickControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentees/my-picks";

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    LoginService loginService;
    @Autowired
    JwtTokenManager jwtTokenManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    LectureService lectureService;
    @Autowired
    LecturePriceRepository lecturePriceRepository;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    PickService pickService;
    @Autowired
    PickRepository pickRepository;
    @Autowired
    EnrollmentService enrollmentService;

    private User mentorUser;
    private User menteeUser;
    private Lecture lecture;
    private LecturePrice lecturePrice;
    private Long pickId;

    @BeforeAll
    void init() {

        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        mentorUser = saveMentorUser(loginService, mentorService);
        menteeUser = saveMenteeUser(loginService);

        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);

        pickId = savePick(pickService, menteeUser, lecture, lecturePrice);
    }

    private String getJwtToken(String username, RoleType roleType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("role", roleType.getType());
        return TOKEN_PREFIX + jwtTokenManager.createToken(USERNAME, claims);
    }

    @Test
    void get_picks() throws Exception {

        // given
        // when
        String accessToken = getJwtToken(menteeUser.getUsername(), RoleType.MENTEE);
        // then
        mockMvc.perform(get(BASE_URL, 1)
                        .header(HEADER, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..pickId").exists())
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

/*
    @WithAccount(NAME)
    @Test
    void subtractPick() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        Long lecturePriceId = lecturePrice.getId();

        Long pickId = pickService.createPick(user, lecture.getId(), lecturePriceId);

        // When
        mockMvc.perform(delete(BASE_URL + "/{pick_id}", pickId))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        Pick pick = pickRepository.findById(pickId).orElse(null);
        assertNull(pick);
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }*/

    @Test
    void clear() throws Exception {

        // given
        // when
        String accessToken = getJwtToken(menteeUser.getUsername(), RoleType.MENTEE);
        mockMvc.perform(delete(BASE_URL)
                        .header(HEADER, accessToken))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertFalse(pickRepository.findById(pickId).isPresent());
    }
}