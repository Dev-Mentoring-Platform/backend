package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.controller.request.UserQuitRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.*;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.log.component.LecturePriceLogService;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMenteeUser;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMentorUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class UserControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final String BASE_URL = "/api/users";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    CareerRepository careerRepository;
    @Autowired
    EducationRepository educationRepository;

    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    LecturePriceLogService lecturePriceLogService;
    @Autowired
    LecturePriceRepository lecturePriceRepository;

    @Autowired
    SubjectRepository subjectRepository;

    private User menteeUser;
    private Mentee mentee;
    private String menteeAccessToken;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessToken;

    private Lecture lecture;
    private LecturePrice lecturePrice;

    @BeforeEach
    void init() {

        // subject
        if (subjectRepository.count() == 0) {
            subjectRepository.save(Subject.builder()
                    .subjectId(1L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("백엔드")
                    .build());
            subjectRepository.save(Subject.builder()
                    .subjectId(2L)
                    .learningKind(LearningKindType.IT)
                    .krSubject("프론트엔드")
                    .build());
        }

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessToken = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);

        lecture = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        lecture.approve(lectureLogService);
    }

    @Test
    void get_paged_users() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].userId").value(menteeReview2.getId()))
                .andExpect(jsonPath("$.[0].username").value(enrollment2.getId()))
                .andExpect(jsonPath("$.[0].role").value(menteeReview2.getScore()))
                .andExpect(jsonPath("$.[0].name").value(menteeReview2.getContent()))
                .andExpect(jsonPath("$.[0].gender").value(menteeUser2.getUsername()))
                .andExpect(jsonPath("$.[0].birthYear").value(menteeUser2.getNickname()))
                .andExpect(jsonPath("$.[0].phoneNumber").value(menteeUser2.getImage()))
                .andExpect(jsonPath("$.[0].nickname").exists())
                .andExpect(jsonPath("$.[0].image").doesNotExist())
                .andExpect(jsonPath("$.[0].zone").exists())

                .andExpect(jsonPath("$.[0].lecture.id").value(lecture.getId()))
                .andExpect(jsonPath("$.[0].lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.[0].lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.[0].lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.[0].lecture.difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$.[0].lecture.systems").exists())
                // lecturePrice
                .andExpect(jsonPath("$.[0].lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.lecturePriceId").value(lecturePrice2.getId()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.isGroup").value(lecturePrice2.isGroup()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.numberOfMembers").value(lecturePrice2.getNumberOfMembers()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.pricePerHour").value(lecturePrice2.getPricePerHour()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.timePerLecture").value(lecturePrice2.getTimePerLecture()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.numberOfLectures").value(lecturePrice2.getNumberOfLectures()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.totalPrice").value(lecturePrice2.getTotalPrice()))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.isGroupStr").value(lecturePrice2.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.content")
                        .value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())))
                .andExpect(jsonPath("$.[0].lecture.lecturePrice.closed").value(lecturePrice2.isClosed()))

                .andExpect(jsonPath("$.[0].lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$.[0].lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.[0].lecture.approved").value(lecture.isApproved()))
                .andExpect(jsonPath("$.[0].lecture.mentorNickname").value(mentorUser.getNickname()))

                .andExpect(jsonPath("$.[0].lecture.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.[0].lecture.pickCount").doesNotExist())

                .andExpect(jsonPath("$.[1].menteeReviewId").value(menteeReview1.getId()))
                .andExpect(jsonPath("$.[1].enrollmentId").value(enrollment1.getId()))
                .andExpect(jsonPath("$.[1].score").value(menteeReview1.getScore()))
                .andExpect(jsonPath("$.[1].content").value(menteeReview1.getContent()))
                .andExpect(jsonPath("$.[1].username").value(menteeUser1.getUsername()))
                .andExpect(jsonPath("$.[1].userNickname").value(menteeUser1.getNickname()))
                .andExpect(jsonPath("$.[1].userImage").value(menteeUser1.getImage()))
                .andExpect(jsonPath("$.[1].createdAt").exists())
                // child
                .andExpect(jsonPath("$.[1].child").exists())
                .andExpect(jsonPath("$.[1].child.mentorReviewId").value(mentorReview1.getId()))
                .andExpect(jsonPath("$.[1].child.content").value(mentorReview1.getContent()))
                .andExpect(jsonPath("$.[1].child.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.[1].child.userNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.[1].child.userImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.[1].child.createdAt").exists())
                // lecture
                .andExpect(jsonPath("$.[1].lecture").exists())
                .andExpect(jsonPath("$.[1].lecture.id").value(lecture.getId()))
                .andExpect(jsonPath("$.[1].lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.[1].lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.[1].lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.[1].lecture.difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$.[1].lecture.systems").exists())
                // lecturePrice
                .andExpect(jsonPath("$.[1].lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.[1].lecture.lecturePrice.lecturePriceId").value(lecturePrice1.getId()))
                .andExpect(jsonPath("$.[1].lecture.lecturePrice.isGroup").value(lecturePrice1.isGroup()))
                .andExpect(jsonPath("$.[1].lecture.lecturePrice.numberOfMembers").value(lecturePrice1.getNumberOfMembers()))
                .andExpect(jsonPath("$.[1].lecture.lecturePrice.pricePerHour").value(lecturePrice1.getPricePerHour()))
                .andExpect(jsonPath("$.[1].lecture.lecturePrice.timePerLecture").value(lecturePrice1.getTimePerLecture()))
                .andExpect(jsonPath("$.[1].lecture.lecturePrice.numberOfLectures").value(lecturePrice1.getNumberOfLectures()))
                .andExpect(jsonPath("$.[1].lecture.lecturePrice.totalPrice").value(lecturePrice1.getTotalPrice()))
                .andExpect(jsonPath("$.[1].lecture.lecturePrice.isGroupStr").value(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.[1].lecture.lecturePrice.content")
                        .value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())))
                .andExpect(jsonPath("$.[1].lecture.lecturePrice.closed").value(lecturePrice1.isClosed()))

                .andExpect(jsonPath("$.[1].lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$.[1].lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.[1].lecture.approved").value(lecture.isApproved()))
                .andExpect(jsonPath("$.[1].lecture.mentorNickname").value(mentorUser.getNickname()))

                .andExpect(jsonPath("$.[1].lecture.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.[1].lecture.pickCount").doesNotExist());
    }

    @WithAccount(NAME)
    @Test
    void 회원정보_수정() throws Exception {

        // Given
        // When
        mockMvc.perform(put(BASE_URL + "/my-info")
                .content(objectMapper.writeValueAsString(userUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        assertAll(
                () -> assertNotNull(user),
                () -> assertEquals(userUpdateRequest.getGender(), user.getGender().name()),
                () -> assertEquals(userUpdateRequest.getBirthYear(), user.getBirthYear()),
                () -> assertEquals(userUpdateRequest.getPhoneNumber(), user.getPhoneNumber()),
                () -> assertEquals(userUpdateRequest.getZone(), user.getZone().toString()),
                () -> assertEquals(userUpdateRequest.getImage(), user.getImage())
        );
    }

    // TODO - 회원 삭제 시 연관 엔티티 전체 삭제
    @WithAccount(NAME)
    @Test
    void 회원탈퇴() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);

        Mentor mentor = mentorService.createMentor(user, mentorSignUpRequest);
        List<Long> careerIds = careerRepository.findByMentor(mentor).stream()
                .map(career -> career.getId()).collect(Collectors.toList());
        List<Long> educationIds = educationRepository.findByMentor(mentor).stream()
                .map(education -> education.getId()).collect(Collectors.toList());

        // When
        UserQuitRequest userQuitRequest = UserQuitRequest.builder()
                .reasonId(1)
                .password("password")
                .build();
        mockMvc.perform(delete(BASE_URL)
                .content(objectMapper.writeValueAsString(userQuitRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        // 세션
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 유저
        User deletedUser = userRepository.findAllByUsername(USERNAME);
        assertTrue(deletedUser.isDeleted());
        assertNotNull(deletedUser.getDeletedAt());
        assertEquals(RoleType.MENTEE, deletedUser.getRole());

        // 멘티
        assertNull(menteeRepository.findByUser(deletedUser));
        // 멘토
        assertNull(mentorRepository.findByUser(deletedUser));
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
        // notification
        // file
    }
}