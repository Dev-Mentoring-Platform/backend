package com.project.mentoridge.modules.purchase.service;

import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractIntegrationTest;
import com.project.mentoridge.modules.lecture.controller.request.LectureCreateRequest;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.purchase.controller.response.PickWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.vo.Pick;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class PickServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    PickService pickService;
    @Autowired
    PickRepository pickRepository;

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
    SubjectRepository subjectRepository;

    private User menteeUser;
    private Mentee mentee;

    private User mentorUser;
    private Mentor mentor;

    private Lecture lecture1;
    private LecturePrice lecturePrice1;
    private Lecture lecture2;
    private LecturePrice lecturePrice2;

    @BeforeEach
    @Override
    protected void init() {

        initDatabase();

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

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);

        lecture1 = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lecture1.approve(lectureLogService);
        lecturePrice1 = lecturePriceRepository.findByLecture(lecture1).get(0);

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
        lecture2.approve(lectureLogService);
        lecturePrice2 = lecturePriceRepository.findByLecture(lecture2).get(0);
    }

    @Test
    void get_paged_PickWithSimpleEachLectureResponses() {

        // Given
        Pick pick1 = Pick.buildPick(mentee, lecture1, lecturePrice1);
        Pick pick2 = Pick.buildPick(mentee, lecture2, lecturePrice2);
        pickRepository.saveAll(Arrays.asList(pick1, pick2));

        // When
        Page<PickWithSimpleEachLectureResponse> picks = pickService.getPickWithSimpleEachLectureResponses(menteeUser, 1);

        // Then
        assertThat(picks.getTotalElements()).isEqualTo(2L);
        for (PickWithSimpleEachLectureResponse pick : picks) {

            if (Objects.equals(pick.getPickId(), pick1.getId())) {

                assertAll(
                        () -> assertThat(pick.getPickId()).isEqualTo(pick1.getId()),
                        () -> assertThat(pick.getLecture().getLectureId()).isEqualTo(lecture1.getId()),
                        () -> assertThat(pick.getLecture().getTitle()).isEqualTo(lecture1.getTitle()),
                        () -> assertThat(pick.getLecture().getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                        () -> assertThat(pick.getLecture().getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                        () -> assertThat(pick.getLecture().getDifficulty()).isEqualTo(lecture1.getDifficulty()),

                        () -> assertThat(pick.getLecture().getSystems().size()).isEqualTo(lecture1.getSystems().size()),

                        () -> assertThat(pick.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                        () -> assertThat(pick.getLecture().getLecturePrice().isGroup()).isEqualTo(lecturePrice1.isGroup()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"),
                        () -> assertThat(pick.getLecture().getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                        () -> assertThat(pick.getLecture().getLecturePrice().isClosed()).isEqualTo(lecturePrice1.isClosed()),

                        () -> assertThat(pick.getLecture().getLectureSubjects().size()).isEqualTo(lecture1.getLectureSubjects().size()),

                        () -> assertThat(pick.getLecture().getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                        () -> assertThat(pick.getLecture().getMentorNickname()).isEqualTo(lecture1.getMentor().getUser().getNickname()),
                        () -> assertThat(pick.getLecture().getScoreAverage()).isEqualTo(0.0),
                        () -> assertThat(pick.getLecture().getPickCount()).isEqualTo(1L)
                );

            } else if (Objects.equals(pick.getPickId(), pick2.getId())) {

                assertAll(
                        () -> assertThat(pick.getPickId()).isEqualTo(pick2.getId()),
                        () -> assertThat(pick.getLecture().getLectureId()).isEqualTo(lecture2.getId()),
                        () -> assertThat(pick.getLecture().getTitle()).isEqualTo(lecture2.getTitle()),
                        () -> assertThat(pick.getLecture().getSubTitle()).isEqualTo(lecture2.getSubTitle()),
                        () -> assertThat(pick.getLecture().getIntroduce()).isEqualTo(lecture2.getIntroduce()),
                        () -> assertThat(pick.getLecture().getDifficulty()).isEqualTo(lecture2.getDifficulty()),

                        () -> assertThat(pick.getLecture().getSystems().size()).isEqualTo(lecture2.getSystems().size()),

                        () -> assertThat(pick.getLecture().getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                        () -> assertThat(pick.getLecture().getLecturePrice().isGroup()).isEqualTo(lecturePrice2.isGroup()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                        () -> assertThat(pick.getLecture().getLecturePrice().getIsGroupStr()).isEqualTo(lecturePrice2.isGroup() ? "그룹강의" : "1:1 개인강의"),
                        () -> assertThat(pick.getLecture().getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                        () -> assertThat(pick.getLecture().getLecturePrice().isClosed()).isEqualTo(lecturePrice2.isClosed()),

                        () -> assertThat(pick.getLecture().getLectureSubjects().size()).isEqualTo(lecture2.getLectureSubjects().size()),

                        () -> assertThat(pick.getLecture().getThumbnail()).isEqualTo(lecture2.getThumbnail()),
                        () -> assertThat(pick.getLecture().getMentorNickname()).isEqualTo(lecture2.getMentor().getUser().getNickname()),
                        () -> assertThat(pick.getLecture().getScoreAverage()).isEqualTo(0.0),
                        () -> assertThat(pick.getLecture().getPickCount()).isEqualTo(1L)
                );
            } else {
                fail();
            }
        }

    }

    @Test
    void createPick() {

        // Given
        // When
        Long pickId = pickService.createPick(menteeUser, lecture1.getId(), lecturePrice1.getId());

        // Then
        Pick pick = pickRepository.findById(pickId).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(mentee, pick.getMentee()),
                () -> assertEquals(lecture1, pick.getLecture()),
                () -> assertEquals(lecturePrice1, pick.getLecturePrice())
        );
    }

    @Test
    void cancelPick() {

        // Given
        Long pickId = pickService.createPick(menteeUser, lecture1.getId(), lecturePrice1.getId());

        // When
        Long result = pickService.createPick(menteeUser, lecture1.getId(), lecturePrice1.getId());

        // Then
        assertNull(result);
        assertFalse(pickRepository.findById(pickId).isPresent());
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }

/*
    @Test
    void deletePick() {

        // Given
        Long pickId = pickService.createPick(menteeUser, lecture.getId(), lecturePrice1.getId()).getId();

        // When
        pickService.deletePick(menteeUser, pickId);

        // Then
        assertFalse(pickRepository.findById(pickId).isPresent());
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
    }*/

    @Test
    void deleteAllPicks() {

        // Given
        Long pick1Id = pickService.createPick(menteeUser, lecture1.getId(), lecturePrice1.getId());
        Long pick2Id = pickService.createPick(menteeUser, lecture2.getId(), lecturePrice2.getId());
        assertEquals(2, pickRepository.findByMentee(mentee).size());

        // When
        pickService.deleteAllPicks(menteeUser);

        // Then
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
        assertFalse(pickRepository.findById(pick1Id).isPresent());
        assertFalse(pickRepository.findById(pick2Id).isPresent());
    }
}