package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.embeddable.Address;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.lecture.controller.response.LecturePriceWithLectureResponse;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSubjectRepository;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.log.component.EnrollmentLogService;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.repository.PickRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.review.repository.MentorReviewRepository;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static com.project.mentoridge.configuration.AbstractTest.*;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class LectureServiceIntegrationTest {

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
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    LectureSubjectRepository lectureSubjectRepository;
    @Autowired
    LecturePriceRepository lecturePriceRepository;

    @Autowired
    PickService pickService;
    @Autowired
    PickRepository pickRepository;

    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentLogService enrollmentLogService;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;
    @Autowired
    MentorReviewService mentorReviewService;
    @Autowired
    MentorReviewRepository mentorReviewRepository;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    private User menteeUser;
    private Mentee mentee;

    private User mentorUser;
    private Mentor mentor;
    private Lecture lecture;
    private LecturePrice lecturePrice;
    // private Long lectureId;

    private Subject subject1;
    private Subject subject2;

    @BeforeEach
    void init() {

        saveAddress(addressRepository);
        // saveSubject(subjectRepository);
        subjectRepository.deleteAll();
        subject1 = subjectRepository.save(Subject.builder()
                .subjectId(1L)
                .krSubject("프론트엔드")
                .learningKind(LearningKindType.IT)
                .build());
        subject2 = subjectRepository.save(Subject.builder()
                .subjectId(2L)
                .krSubject("백엔드")
                .learningKind(LearningKindType.IT)
                .build());

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);
    }

    @DisplayName("강의 정보")
    @Test
    void get_LectureResponse() {

        // Given
        Lecture lecture1 = lectureRepository.save(Lecture.builder()
                .mentor(mentor)
                .title("title1")
                .subTitle("subTitle1")
                .introduce("introduce1")
                .content("content1")
                .difficulty(DifficultyType.BEGINNER)
                .systems(Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE))
                .thumbnail("thumbnail1")
                .build());
        LecturePrice lecturePrice1 = lecturePriceRepository.save(LecturePrice.builder()
                .lecture(lecture1)
                .isGroup(Boolean.TRUE)
                .numberOfMembers(3)
                .pricePerHour(10000L)
                .timePerLecture(2)
                .numberOfLectures(5)
                .totalPrice(10000L * 2 * 5)
                .build());
        LecturePrice lecturePrice2 = lecturePriceRepository.save(LecturePrice.builder()
                .lecture(lecture1)
                .isGroup(Boolean.FALSE)
                .numberOfMembers(0)
                .pricePerHour(20000L)
                .timePerLecture(2)
                .numberOfLectures(5)
                .totalPrice(20000L * 2 * 5)
                .build());
        LectureSubject lectureSubject1 = lectureSubjectRepository.save(LectureSubject.builder()
                .lecture(lecture1)
                .subject(subject1)
                .build());
        // 강의 승인
        lecture1.approve(lectureLogService);

        // When
        LectureResponse lectureResponse = lectureService.getLectureResponse(menteeUser, lecture1.getId());
        // Then
        assertAll(
                () -> assertThat(lectureResponse.getId()).isEqualTo(lecture1.getId()),
                () -> assertThat(lectureResponse.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(lectureResponse.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(lectureResponse.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(lectureResponse.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(lectureResponse.getDifficulty()).isEqualTo(lecture1.getDifficulty()),

                // systems
                () -> assertThat(lectureResponse.getSystems().size()).isEqualTo(lecture1.getSystems().size()),

                // lecturePrices
                () -> assertThat(lectureResponse.getLecturePrices().size()).isEqualTo(lecture1.getLecturePrices().size()),
                () -> assertThat(lectureResponse.getLecturePrices().get(0).getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(lectureResponse.getLecturePrices().get(0).getIsGroup()).isEqualTo(lecturePrice1.getIsGroup()),
                () -> assertThat(lectureResponse.getLecturePrices().get(0).getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(lectureResponse.getLecturePrices().get(0).getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(lectureResponse.getLecturePrices().get(0).getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(lectureResponse.getLecturePrices().get(0).getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(lectureResponse.getLecturePrices().get(0).getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(lectureResponse.getLecturePrices().get(0).getIsGroupStr()).isEqualTo("그룹강의"),
                () -> assertThat(lectureResponse.getLecturePrices().get(0).getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(lectureResponse.getLecturePrices().get(0).getClosed()).isEqualTo(lecturePrice1.isClosed()),

                () -> assertThat(lectureResponse.getLecturePrices().get(1).getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(lectureResponse.getLecturePrices().get(1).getIsGroup()).isEqualTo(lecturePrice2.getIsGroup()),
                () -> assertThat(lectureResponse.getLecturePrices().get(1).getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(lectureResponse.getLecturePrices().get(1).getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(lectureResponse.getLecturePrices().get(1).getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(lectureResponse.getLecturePrices().get(1).getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(lectureResponse.getLecturePrices().get(1).getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(lectureResponse.getLecturePrices().get(1).getIsGroupStr()).isEqualTo("1:1 개인강의"),
                () -> assertThat(lectureResponse.getLecturePrices().get(1).getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                () -> assertThat(lectureResponse.getLecturePrices().get(1).getClosed()).isEqualTo(lecturePrice2.isClosed()),

                // lectureSubjects
                () -> assertThat(lectureResponse.getLectureSubjects().size()).isEqualTo(lecture1.getLectureSubjects().size()),
                () -> assertThat(lectureResponse.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lectureSubject1.getSubject().getLearningKind().getName()),
                () -> assertThat(lectureResponse.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lectureSubject1.getSubject().getKrSubject()),

                () -> assertThat(lectureResponse.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(lectureResponse.getApproved()).isEqualTo(lecture1.isApproved()),
                () -> assertThat(lectureResponse.getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(lectureResponse.getEnrollmentCount()).isNull(),

                // lectureMentor
                () -> assertThat(lectureResponse.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(lectureResponse.getLectureMentor().getLectureCount()).isEqualTo(2L),
                () -> assertThat(lectureResponse.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(lectureResponse.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(lectureResponse.getPicked()).isNull()
        );
    }

    @Test
    void get_LecturePriceWithLectureResponse() {

        // Given
        Lecture lecture1 = lectureRepository.save(Lecture.builder()
                .mentor(mentor)
                .title("title1")
                .subTitle("subTitle1")
                .introduce("introduce1")
                .content("content1")
                .difficulty(DifficultyType.BEGINNER)
                .systems(Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE))
                .thumbnail("thumbnail1")
                .build());
        LecturePrice lecturePrice1 = lecturePriceRepository.save(LecturePrice.builder()
                .lecture(lecture1)
                .isGroup(Boolean.TRUE)
                .numberOfMembers(3)
                .pricePerHour(10000L)
                .timePerLecture(2)
                .numberOfLectures(5)
                .totalPrice(10000L * 2 * 5)
                .build());
        LecturePrice lecturePrice2 = lecturePriceRepository.save(LecturePrice.builder()
                .lecture(lecture1)
                .isGroup(Boolean.FALSE)
                .numberOfMembers(0)
                .pricePerHour(20000L)
                .timePerLecture(2)
                .numberOfLectures(5)
                .totalPrice(20000L * 2 * 5)
                .build());
        LectureSubject lectureSubject1 = lectureSubjectRepository.save(LectureSubject.builder()
                .lecture(lecture1)
                .subject(subject1)
                .build());
        // 강의 승인
        lecture1.approve(lectureLogService);

        // When
        LecturePriceWithLectureResponse lectureResponse1 = lectureService.getLectureResponsePerLecturePrice(menteeUser, lecture1.getId(), lecturePrice1.getId());
        LecturePriceWithLectureResponse lectureResponse2 = lectureService.getLectureResponsePerLecturePrice(menteeUser, lecture1.getId(), lecturePrice2.getId());
        // Then
        assertAll(
                () -> assertThat(lectureResponse1.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(lectureResponse1.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(lectureResponse1.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(lectureResponse1.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(lectureResponse1.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(lectureResponse1.getDifficulty()).isEqualTo(lecture1.getDifficulty()),

                // systems
                () -> assertThat(lectureResponse1.getSystems().size()).isEqualTo(lecture1.getSystems().size()),

                // lecturePrice
                () -> assertThat(lectureResponse1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(lectureResponse1.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice1.getIsGroup()),
                () -> assertThat(lectureResponse1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(lectureResponse1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(lectureResponse1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(lectureResponse1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(lectureResponse1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(lectureResponse1.getLecturePrice().getIsGroupStr()).isEqualTo("그룹강의"),
                () -> assertThat(lectureResponse1.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(lectureResponse1.getLecturePrice().getClosed()).isEqualTo(lecturePrice1.isClosed()),
                () -> assertThat(lectureResponse1.getLecturePriceId()).isEqualTo(lecturePrice1.getId()),

                // lectureSubjects
                () -> assertThat(lectureResponse1.getLectureSubjects().size()).isEqualTo(lecture1.getLectureSubjects().size()),
                () -> assertThat(lectureResponse1.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lectureSubject1.getSubject().getLearningKind().getName()),
                () -> assertThat(lectureResponse1.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lectureSubject1.getSubject().getKrSubject()),

                () -> assertThat(lectureResponse1.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(lectureResponse1.getApproved()).isEqualTo(lecture1.isApproved()),
                () -> assertThat(lectureResponse1.getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse1.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(lectureResponse1.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(lectureResponse1.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(lectureResponse1.getLectureMentor().getLectureCount()).isEqualTo(2L),
                () -> assertThat(lectureResponse1.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse1.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(lectureResponse1.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(lectureResponse1.getPicked()).isFalse(),
                () -> assertThat(lectureResponse1.getPickCount()).isEqualTo(0L)
        );
        assertAll(
                () -> assertThat(lectureResponse2.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(lectureResponse2.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(lectureResponse2.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(lectureResponse2.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(lectureResponse2.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(lectureResponse2.getDifficulty()).isEqualTo(lecture1.getDifficulty()),

                // systems
                () -> assertThat(lectureResponse2.getSystems().size()).isEqualTo(lecture1.getSystems().size()),

                // lecturePrice
                () -> assertThat(lectureResponse2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(lectureResponse2.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice2.getIsGroup()),
                () -> assertThat(lectureResponse2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(lectureResponse2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(lectureResponse2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(lectureResponse2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(lectureResponse2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(lectureResponse2.getLecturePrice().getIsGroupStr()).isEqualTo("1:1 개인강의"),
                () -> assertThat(lectureResponse2.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                () -> assertThat(lectureResponse2.getLecturePrice().getClosed()).isEqualTo(lecturePrice2.isClosed()),
                () -> assertThat(lectureResponse2.getLecturePriceId()).isEqualTo(lecturePrice2.getId()),

                // lectureSubjects
                () -> assertThat(lectureResponse2.getLectureSubjects().size()).isEqualTo(lecture1.getLectureSubjects().size()),
                () -> assertThat(lectureResponse2.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lectureSubject1.getSubject().getLearningKind().getName()),
                () -> assertThat(lectureResponse2.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lectureSubject1.getSubject().getKrSubject()),

                () -> assertThat(lectureResponse2.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(lectureResponse2.getApproved()).isEqualTo(lecture1.isApproved()),
                () -> assertThat(lectureResponse2.getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse2.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(lectureResponse2.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(lectureResponse2.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(lectureResponse2.getLectureMentor().getLectureCount()).isEqualTo(2L),
                () -> assertThat(lectureResponse2.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse2.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(lectureResponse2.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(lectureResponse2.getPicked()).isFalse(),
                () -> assertThat(lectureResponse2.getPickCount()).isEqualTo(0L)
        );
    }

    @Test
    void get_paged_LecturePriceWithLectureResponses_when_no_lectures_are_approved() {

        // Given
        lecture.cancelApproval();

        // When
        Page<LecturePriceWithLectureResponse> responses = lectureService.getLectureResponsesPerLecturePrice(menteeUser, null, null, 1);

        // Then
        assertThat(responses.getTotalElements()).isEqualTo(0L);
    }

    // @DisplayName("강의 목록 - LectureListRequest 추가해서 테스트")
    @DisplayName("강의 목록")
    @Test
    void get_paged_LecturePriceWithLectureResponses() {

        // Given
        LecturePrice lecturePrice1 = lecture.getLecturePrices().get(0);
        LecturePrice lecturePrice2 = lecture.getLecturePrices().get(1);
        // When
        Page<LecturePriceWithLectureResponse> responses = lectureService.getLectureResponsesPerLecturePrice(menteeUser, null, null, 1);

        // Then
        LecturePriceWithLectureResponse lectureResponse1 = responses.getContent().get(0);
        LecturePriceWithLectureResponse lectureResponse2 = responses.getContent().get(1);
        assertAll(
                () -> assertThat(lectureResponse1.getLectureId()).isEqualTo(lecture.getId()),
                () -> assertThat(lectureResponse1.getTitle()).isEqualTo(lecture.getTitle()),
                () -> assertThat(lectureResponse1.getSubTitle()).isEqualTo(lecture.getSubTitle()),
                () -> assertThat(lectureResponse1.getIntroduce()).isEqualTo(lecture.getIntroduce()),
                () -> assertThat(lectureResponse1.getContent()).isEqualTo(lecture.getContent()),
                () -> assertThat(lectureResponse1.getDifficulty()).isEqualTo(lecture.getDifficulty()),

                // systems
                () -> assertThat(lectureResponse1.getSystems().size()).isEqualTo(lecture.getSystems().size()),

                // lecturePrice
                () -> assertThat(lectureResponse1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(lectureResponse1.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice1.getIsGroup()),
                () -> assertThat(lectureResponse1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(lectureResponse1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(lectureResponse1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(lectureResponse1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(lectureResponse1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(lectureResponse1.getLecturePrice().getIsGroupStr()).isEqualTo("그룹강의"),
                () -> assertThat(lectureResponse1.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(lectureResponse1.getLecturePrice().getClosed()).isEqualTo(lecturePrice1.isClosed()),
                () -> assertThat(lectureResponse1.getLecturePriceId()).isEqualTo(lecturePrice1.getId()),

                // lectureSubjects
                () -> assertThat(lectureResponse1.getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),
                () -> assertThat(lectureResponse1.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getLearningKind().getName()),
                () -> assertThat(lectureResponse1.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()),

                () -> assertThat(lectureResponse1.getThumbnail()).isEqualTo(lecture.getThumbnail()),
                () -> assertThat(lectureResponse1.getApproved()).isEqualTo(lecture.isApproved()),
                () -> assertThat(lectureResponse1.getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse1.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(lectureResponse1.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(lectureResponse1.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(lectureResponse1.getLectureMentor().getLectureCount()).isEqualTo(1L),
                () -> assertThat(lectureResponse1.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse1.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(lectureResponse1.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(lectureResponse1.getPicked()).isFalse(),
                () -> assertThat(lectureResponse1.getPickCount()).isEqualTo(0L)
        );
        assertAll(
                () -> assertThat(lectureResponse2.getLectureId()).isEqualTo(lecture.getId()),
                () -> assertThat(lectureResponse2.getTitle()).isEqualTo(lecture.getTitle()),
                () -> assertThat(lectureResponse2.getSubTitle()).isEqualTo(lecture.getSubTitle()),
                () -> assertThat(lectureResponse2.getIntroduce()).isEqualTo(lecture.getIntroduce()),
                () -> assertThat(lectureResponse2.getContent()).isEqualTo(lecture.getContent()),
                () -> assertThat(lectureResponse2.getDifficulty()).isEqualTo(lecture.getDifficulty()),

                // systems
                () -> assertThat(lectureResponse2.getSystems().size()).isEqualTo(lecture.getSystems().size()),

                // lecturePrice
                () -> assertThat(lectureResponse2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(lectureResponse2.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice2.getIsGroup()),
                () -> assertThat(lectureResponse2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(lectureResponse2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(lectureResponse2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(lectureResponse2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(lectureResponse2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(lectureResponse2.getLecturePrice().getIsGroupStr()).isEqualTo("1:1 개인강의"),
                () -> assertThat(lectureResponse2.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                () -> assertThat(lectureResponse2.getLecturePrice().getClosed()).isEqualTo(lecturePrice2.isClosed()),
                () -> assertThat(lectureResponse2.getLecturePriceId()).isEqualTo(lecturePrice2.getId()),

                // lectureSubjects
                () -> assertThat(lectureResponse2.getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),
                () -> assertThat(lectureResponse2.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getLearningKind().getName()),
                () -> assertThat(lectureResponse2.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()),

                () -> assertThat(lectureResponse2.getThumbnail()).isEqualTo(lecture.getThumbnail()),
                () -> assertThat(lectureResponse2.getApproved()).isEqualTo(lecture.isApproved()),
                () -> assertThat(lectureResponse2.getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse2.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(lectureResponse2.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(lectureResponse2.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(lectureResponse2.getLectureMentor().getLectureCount()).isEqualTo(1L),
                () -> assertThat(lectureResponse2.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse2.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(lectureResponse2.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(lectureResponse2.getPicked()).isFalse(),
                () -> assertThat(lectureResponse2.getPickCount()).isEqualTo(0L)
        );
    }

    @Test
    void 강의_목록() {

        // Given
        Address zone = mentorUser.getZone();  // 서울특별시 종로구 청운동
        assertAll(
                () -> assertEquals("서울특별시", zone.getState()),
                () -> assertEquals("종로구", zone.getSiGunGu()),
                () -> assertEquals("청운동", zone.getDongMyunLi())
        );

        LecturePrice lecturePrice1 = lecture.getLecturePrices().get(0);
        LecturePrice lecturePrice2 = lecture.getLecturePrices().get(1);

        // When
        Page<LecturePriceWithLectureResponse> lectureResponses = lectureService.getLectureResponsesPerLecturePrice(menteeUser, "서울특별시 종로구", null, 1);

        // Then
        assertEquals(2, lectureResponses.getTotalElements());
        LecturePriceWithLectureResponse lectureResponse1 = lectureResponses.getContent().get(0);
        LecturePriceWithLectureResponse lectureResponse2 = lectureResponses.getContent().get(1);
        assertAll(
                () -> assertThat(lectureResponse1.getLectureId()).isEqualTo(lecture.getId()),
                () -> assertThat(lectureResponse1.getTitle()).isEqualTo(lecture.getTitle()),
                () -> assertThat(lectureResponse1.getSubTitle()).isEqualTo(lecture.getSubTitle()),
                () -> assertThat(lectureResponse1.getIntroduce()).isEqualTo(lecture.getIntroduce()),
                () -> assertThat(lectureResponse1.getContent()).isEqualTo(lecture.getContent()),
                () -> assertThat(lectureResponse1.getDifficulty()).isEqualTo(lecture.getDifficulty()),

                // systems
                () -> assertThat(lectureResponse1.getSystems().size()).isEqualTo(lecture.getSystems().size()),

                // lecturePrice
                () -> assertThat(lectureResponse1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(lectureResponse1.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice1.getIsGroup()),
                () -> assertThat(lectureResponse1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(lectureResponse1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(lectureResponse1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(lectureResponse1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(lectureResponse1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(lectureResponse1.getLecturePrice().getIsGroupStr()).isEqualTo("그룹강의"),
                () -> assertThat(lectureResponse1.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(lectureResponse1.getLecturePrice().getClosed()).isEqualTo(lecturePrice1.isClosed()),
                () -> assertThat(lectureResponse1.getLecturePriceId()).isEqualTo(lecturePrice1.getId()),

                // lectureSubjects
                () -> assertThat(lectureResponse1.getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),
                () -> assertThat(lectureResponse1.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getLearningKind().getName()),
                () -> assertThat(lectureResponse1.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()),

                () -> assertThat(lectureResponse1.getThumbnail()).isEqualTo(lecture.getThumbnail()),
                () -> assertThat(lectureResponse1.getApproved()).isEqualTo(lecture.isApproved()),
                () -> assertThat(lectureResponse1.getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse1.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(lectureResponse1.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(lectureResponse1.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(lectureResponse1.getLectureMentor().getLectureCount()).isEqualTo(1L),
                () -> assertThat(lectureResponse1.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse1.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(lectureResponse1.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(lectureResponse1.getPicked()).isFalse(),
                () -> assertThat(lectureResponse1.getPickCount()).isEqualTo(0L)
        );
        assertAll(
                () -> assertThat(lectureResponse2.getLectureId()).isEqualTo(lecture.getId()),
                () -> assertThat(lectureResponse2.getTitle()).isEqualTo(lecture.getTitle()),
                () -> assertThat(lectureResponse2.getSubTitle()).isEqualTo(lecture.getSubTitle()),
                () -> assertThat(lectureResponse2.getIntroduce()).isEqualTo(lecture.getIntroduce()),
                () -> assertThat(lectureResponse2.getContent()).isEqualTo(lecture.getContent()),
                () -> assertThat(lectureResponse2.getDifficulty()).isEqualTo(lecture.getDifficulty()),

                // systems
                () -> assertThat(lectureResponse2.getSystems().size()).isEqualTo(lecture.getSystems().size()),

                // lecturePrice
                () -> assertThat(lectureResponse2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(lectureResponse2.getLecturePrice().getIsGroup()).isEqualTo(lecturePrice2.getIsGroup()),
                () -> assertThat(lectureResponse2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(lectureResponse2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(lectureResponse2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(lectureResponse2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(lectureResponse2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(lectureResponse2.getLecturePrice().getIsGroupStr()).isEqualTo("1:1 개인강의"),
                () -> assertThat(lectureResponse2.getLecturePrice().getContent()).isEqualTo(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                () -> assertThat(lectureResponse2.getLecturePrice().getClosed()).isEqualTo(lecturePrice2.isClosed()),
                () -> assertThat(lectureResponse2.getLecturePriceId()).isEqualTo(lecturePrice2.getId()),

                // lectureSubjects
                () -> assertThat(lectureResponse2.getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),
                () -> assertThat(lectureResponse2.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getLearningKind().getName()),
                () -> assertThat(lectureResponse2.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()),

                () -> assertThat(lectureResponse2.getThumbnail()).isEqualTo(lecture.getThumbnail()),
                () -> assertThat(lectureResponse2.getApproved()).isEqualTo(lecture.isApproved()),
                () -> assertThat(lectureResponse2.getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse2.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(lectureResponse2.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(lectureResponse2.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(lectureResponse2.getLectureMentor().getLectureCount()).isEqualTo(1L),
                () -> assertThat(lectureResponse2.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(lectureResponse2.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(lectureResponse2.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(lectureResponse2.getPicked()).isFalse(),
                () -> assertThat(lectureResponse2.getPickCount()).isEqualTo(0L)
        );
    }

    @Test
    void 강의_등록() {

        // Given
        // When
        Lecture saved = lectureService.createLecture(mentorUser, lectureCreateRequest);

        // Then
        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved).extracting("mentor").isEqualTo(mentor),
                () -> assertThat(saved).extracting("title").isEqualTo(lectureCreateRequest.getTitle()),
                () -> assertThat(saved).extracting("subTitle").isEqualTo(lectureCreateRequest.getSubTitle()),
                () -> assertThat(saved).extracting("introduce").isEqualTo(lectureCreateRequest.getIntroduce()),
                () -> assertThat(saved).extracting("content").isEqualTo(lectureCreateRequest.getContent()),
                () -> assertThat(saved).extracting("difficulty").isEqualTo(lectureCreateRequest.getDifficulty()),
                () -> assertThat(saved).extracting("thumbnail").isEqualTo(lectureCreateRequest.getThumbnail()),
                () -> assertThat(saved.getSystems()).hasSize(lectureCreateRequest.getSystems().size()),
                () -> assertThat(saved.getLecturePrices()).hasSize(lectureCreateRequest.getLecturePrices().size()),
                () -> assertThat(saved.getLectureSubjects()).hasSize(lectureCreateRequest.getLectureSubjects().size())
        );
    }

    @DisplayName("실패 - 멘티가 강의 등록")
    @Test
    void createLecture_byMentee() {

        // Given
        // When
        // Then
        assertThrows(UnauthorizedException.class, () -> {
            lectureService.createLecture(menteeUser, lectureCreateRequest);
        });
    }

    @DisplayName("등록된 적 있는 강의는 수정 불가")
    @Test
    void should_fail_to_update_enrolledLecture() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        enrollment.check(mentorUser, enrollmentLogService);

        // When
        // Then
        assertThrows(RuntimeException.class, () -> {
            lectureService.updateLecture(mentorUser, lecture.getId(), lectureUpdateRequest);
        });
    }

    @DisplayName("강의 수정")
    @Test
    void updateLecture() {

        // Given
        // When
        lectureService.updateLecture(mentorUser, lecture.getId(), lectureUpdateRequest);

        // Then
        Lecture updatedLecture = lectureRepository.findById(this.lecture.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertThat(updatedLecture.getId()).isNotNull(),
                () -> assertThat(updatedLecture).extracting("mentor").isEqualTo(mentor),
                () -> assertThat(updatedLecture).extracting("title").isEqualTo(lectureUpdateRequest.getTitle()),
                () -> assertThat(updatedLecture).extracting("subTitle").isEqualTo(lectureUpdateRequest.getSubTitle()),
                () -> assertThat(updatedLecture).extracting("introduce").isEqualTo(lectureUpdateRequest.getIntroduce()),
                () -> assertThat(updatedLecture).extracting("content").isEqualTo(lectureUpdateRequest.getContent()),
                () -> assertThat(updatedLecture).extracting("difficulty").isEqualTo(lectureUpdateRequest.getDifficulty()),
                () -> assertThat(updatedLecture).extracting("thumbnail").isEqualTo(lectureUpdateRequest.getThumbnail()),
                () -> assertThat(updatedLecture.getSystems()).hasSize(lectureUpdateRequest.getSystems().size()),
                () -> assertThat(updatedLecture.getLecturePrices()).hasSize(lectureUpdateRequest.getLecturePrices().size()),
                () -> assertThat(updatedLecture.getLectureSubjects()).hasSize(lectureUpdateRequest.getLectureSubjects().size())
        );

        List<LectureSubject> lectureSubjects = lectureSubjectRepository.findByLecture(updatedLecture);
        assertEquals(1, lectureSubjects.size());
        LectureSubject lectureSubject = lectureSubjects.get(0);
        assertAll(
                () -> assertThat(lectureSubject.getId()).isNotNull(),
                () -> assertEquals(lectureSubject.getSubject().getId(), lectureSubjectUpdateRequest.getSubjectId())
        );

        List<LecturePrice> lecturePrices = lecturePriceRepository.findByLecture(updatedLecture);
        assertEquals(1, lecturePrices.size());
        LecturePrice lecturePrice = lecturePrices.get(0);
        assertAll(
                () -> assertThat(lecturePrice.getId()).isNotNull(),
                () -> assertThat(lecturePrice).extracting("isGroup").isEqualTo(lecturePriceUpdateRequest.getIsGroup()),
                () -> assertThat(lecturePrice).extracting("numberOfMembers").isEqualTo(lecturePriceUpdateRequest.getNumberOfMembers()),
                () -> assertThat(lecturePrice).extracting("pricePerHour").isEqualTo(lecturePriceUpdateRequest.getPricePerHour()),
                () -> assertThat(lecturePrice).extracting("timePerLecture").isEqualTo(lecturePriceUpdateRequest.getTimePerLecture()),
                () -> assertThat(lecturePrice).extracting("numberOfLectures").isEqualTo(lecturePriceUpdateRequest.getNumberOfLectures()),
                () -> assertThat(lecturePrice).extracting("totalPrice").isEqualTo(lecturePriceUpdateRequest.getTotalPrice()),

                // 수정된 강의는 재승인 필요
                () -> assertThat(updatedLecture.isApproved()).isEqualTo(false)
        );
    }

    @DisplayName("강의 삭제")
    @Test
    void deleteLecture() {

        // Given
        // pick
        Long pickId = pickService.createPick(menteeUser, lecture.getId(), lecturePrice.getId());
        // enrollment
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        enrollment.check(mentorUser, enrollmentLogService);

        // menteeReview
        MenteeReview menteeReview = menteeReviewService.createMenteeReview(menteeUser, enrollment.getId(), menteeReviewCreateRequest);
//        assertAll(
//                () -> assertNotNull(menteeReview),
//                () -> assertEquals(enrollment, menteeReview.getEnrollment()),
//                () -> assertEquals(0, menteeReview.getChildren().size()),
//                () -> assertEquals(lecture, menteeReview.getLecture()),
//                () -> assertEquals(menteeReviewCreateRequest.getContent(), menteeReview.getContent()),
//                () -> assertEquals(menteeReviewCreateRequest.getScore(), menteeReview.getScore())
//        );
        // mentorReview
        MentorReview mentorReview = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), menteeReview.getId(), mentorReviewCreateRequest);

        // When
        lectureService.deleteLecture(mentorUser, lecture.getId());

        // Then
        // lecture
        assertFalse(lectureRepository.findById(lecture.getId()).isPresent());
        // lectureSubject
        assertTrue(lectureSubjectRepository.findByLectureId(lecture.getId()).isEmpty());
        // lecturePrice
        assertTrue(lecturePriceRepository.findByLectureId(lecture.getId()).isEmpty());

        // pick
        assertFalse(pickRepository.findById(pickId).isPresent());
        // enrollment
        assertFalse(enrollmentRepository.findById(enrollment.getId()).isPresent());
        // menteeReview
        assertFalse(menteeReviewRepository.findById(menteeReview.getId()).isPresent());
        // mentorReview
        assertFalse(mentorReviewRepository.findById(mentorReview.getId()).isPresent());
    }

    @DisplayName("강의 승인 - 관리자")
    @Test
    void approve() {

        // Given
        User adminUser = userRepository.save(User.builder()
                .username("adminUser")
                .password("password")
                .name("adminUserName")
                .nickname("adminUserNickname")
                .role(RoleType.ADMIN)
                .provider(null)
                .providerId(null)
                .build());
        Lecture saved = lectureService.createLecture(mentorUser, lectureCreateRequest);

        // When
        lectureService.approve(adminUser, saved.getId());
        // Then
        assertTrue(lecture.isApproved());
    }

    @DisplayName("이미 승인된 강의 승인 시")
    @Test
    void approve_already_approved_lecture() {

        // Given
        User adminUser = userRepository.save(User.builder()
                .username("adminUser")
                .password("password")
                .name("adminUserName")
                .nickname("adminUserNickname")
                .role(RoleType.ADMIN)
                .provider(null)
                .providerId(null)
                .build());
        Lecture saved = lectureService.createLecture(mentorUser, lectureCreateRequest);
        lectureService.approve(adminUser, saved.getId());

        // When
        // Then
        assertThrows(RuntimeException.class, () -> {
            lectureService.approve(adminUser, saved.getId());
        });
    }

    @DisplayName("강의 모집")
    @Test
    void open() {

        // Given
        // When
        lectureService.open(mentorUser, lecture.getId(), lecturePrice.getId());

        // Then
        assertFalse(lecturePrice.isClosed());
    }

    @DisplayName("강의 모집 종료")
    @Test
    void close() {

        // Given
        // When
        lectureService.close(mentorUser, lecture.getId(), lecturePrice.getId());

        // Then
        assertTrue(lecturePrice.isClosed());
    }
}