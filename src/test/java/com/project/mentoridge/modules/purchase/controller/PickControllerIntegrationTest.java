package com.project.mentoridge.modules.purchase.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static com.project.mentoridge.configuration.AbstractTest.lectureCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.mentorSignUpRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMentorUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class PickControllerIntegrationTest {

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

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
    MentorRepository mentorRepository;
    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    LecturePriceRepository lecturePriceRepository;
    @Autowired
    PickRepository pickRepository;

    @Autowired
    SubjectRepository subjectRepository;

    private User mentorUser;
    private Mentor mentor;
    private Lecture lecture;

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

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);

        lecture = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecture.approve(lectureLogService);
    }

    @WithAccount(NAME)
    @Test
    void addPick() throws Exception {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        // When
        mockMvc.perform(post("/api/lectures/{lecture_id}/lecturePrices/{lecture_price_id}/picks", lecture.getId(), lecturePrice.getId()))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        List<Pick> picks = pickRepository.findByMentee(mentee);
        assertEquals(1, picks.size());
        Pick pick = picks.get(0);
        assertAll(
                () -> assertNotNull(pick),
                () -> assertEquals(mentee, pick.getMentee()),
                () -> assertEquals(lecture, pick.getLecture())
        );
    }
}