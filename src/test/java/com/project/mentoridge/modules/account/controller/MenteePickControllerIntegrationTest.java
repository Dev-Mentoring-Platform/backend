package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
class MenteePickControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentees/my-picks";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    LoginService loginService;
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
    private String menteeAccessToken;

    private Lecture lecture;
    private LecturePrice lecturePrice;
    private Long pickId;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        mentorUser = saveMentorUser(loginService, mentorService);
        menteeUser = saveMenteeUser(loginService);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);

        pickId = savePick(pickService, menteeUser, lecture, lecturePrice);
    }

    @Test
    void get_picks() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL, 1)
                        .header(AUTHORIZATION, menteeAccessToken))
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
                .andExpect(jsonPath("$..lecture[0].lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$..lecture[0].lecturePrice.isGroup").value(lecturePrice.isGroup()))
                .andExpect(jsonPath("$..lecture[0].lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$..lecture[0].lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$..lecture[0].lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$..lecture[0].lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$..lecture[0].lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$..lecture[0].lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$..lecture[0].lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$..lecture[0].lecturePrice.closed").value(lecturePrice.isClosed()))

                .andExpect(jsonPath("$..lecture[0].lectureSubjects").exists())
                .andExpect(jsonPath("$..lecture[0].thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$..lecture[0].approved").value(lecture.isApproved()))

                .andExpect(jsonPath("$..lecture[0].mentorNickname").value(lecture.getMentor().getUser().getNickname()))
                .andExpect(jsonPath("$..lecture[0].scoreAverage").doesNotExist())
                .andExpect(jsonPath("$..lecture[0].pickCount").doesNotExist());
    }

/*
    @WithAccount(NAME)
    @Test
    void subtractPick() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElseThrow(RuntimeException::new);
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
        Pick pick = pickRepository.findById(pickId).orElseThrow(RuntimeException::new);
        assertNull(pick);
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }*/

    @Test
    void clear() throws Exception {

        // given
        // when
        mockMvc.perform(delete(BASE_URL)
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk());

        // then
        assertFalse(pickRepository.findById(pickId).isPresent());
    }
}