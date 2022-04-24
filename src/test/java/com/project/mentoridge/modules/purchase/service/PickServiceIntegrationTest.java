package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.configuration.auth.WithAccount;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static com.project.mentoridge.config.init.TestDataBuilder.getSignUpRequestWithNameAndNickname;
import static com.project.mentoridge.configuration.AbstractTest.lectureCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.mentorSignUpRequest;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class PickServiceIntegrationTest {

    private static final String NAME = "user";
    private static final String USERNAME = "user@email.com";

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
    PickService pickService;
    @Autowired
    PickRepository pickRepository;
    @Autowired
    SubjectRepository subjectRepository;

    private Lecture lecture;
    private Lecture lecture2;

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

        User mentorUser = loginService.signUp(getSignUpRequestWithNameAndNickname("mentor", "mentor"));
        // loginService.verifyEmail(mentorUser.getUsername(), mentorUser.getEmailVerifyToken());
        mentorUser.verifyEmail();
        menteeRepository.save(Mentee.builder()
                .user(mentorUser)
                .build());
        Mentor mentor = mentorService.createMentor(mentorUser, mentorSignUpRequest);

        lecture = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecture.approve();

        lecture2 = lectureService.createLecture(mentorUser, LectureCreateRequest.builder()
                .title("제목2")
                .subTitle("소제목2")
                .introduce("소개2")
                .content("<p>본문2</p>")
                .difficulty(DifficultyType.ADVANCED)
                .systems(Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE))
                .lecturePrices(Arrays.asList(LectureCreateRequest.LecturePriceCreateRequest.builder()
                        .isGroup(false)
                        .pricePerHour(2000L)
                        .timePerLecture(5)
                        .numberOfLectures(5)
                        .totalPrice(2000L * 5 * 5)
                        .build()))
                .lectureSubjects(Arrays.asList(LectureCreateRequest.LectureSubjectCreateRequest.builder()
                        .subjectId(2L)
                        .build()))
                .thumbnail("https://mentoridge.s3.ap-northeast-2.amazonaws.com/2bb34d85-dfa5-4b0e-bc1d-094537af475c")
                .build());
        lecture2.approve();
    }

    @WithAccount(NAME)
    @Test
    void createPick() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        // When
        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);
        Long pickId = pickService.createPick(user, lecture.getId(), lecturePrice1.getId()).getId();

        // Then
        Pick pick = pickRepository.findById(pickId).orElse(null);
        assertAll(
                () -> assertNotNull(pick),
                () -> assertEquals(mentee, pick.getMentee()),
                () -> assertEquals(lecture, pick.getLecture()),
                () -> assertEquals(lecturePrice1, pick.getLecturePrice())
        );
    }

    @WithAccount(NAME)
    @Test
    void deletePick() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);
        Long pickId = pickService.createPick(user, lecture.getId(), lecturePrice1.getId()).getId();

        // When
        pickService.deletePick(user, pickId);

        // Then
        Pick pick = pickRepository.findById(pickId).orElse(null);
        assertNull(pick);
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }

    @WithAccount(NAME)
    @Test
    void deleteAllPicks() {

        // Given
        User user = userRepository.findByUsername(USERNAME).orElse(null);
        Mentee mentee = menteeRepository.findByUser(user);
        assertNotNull(user);

        LecturePrice lecturePrice1 = lecturePriceRepository.findByLecture(lecture).get(0);
        LecturePrice lecturePrice2 = lecturePriceRepository.findByLecture(lecture2).get(0);
        Long pick1Id = pickService.createPick(user, lecture.getId(), lecturePrice1.getId()).getId();
        Long pick2Id = pickService.createPick(user, lecture2.getId(), lecturePrice2.getId()).getId();
        assertEquals(2, pickRepository.findByMentee(mentee).size());

        // When
        pickService.deleteAllPicks(user);

        // Then
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }
}