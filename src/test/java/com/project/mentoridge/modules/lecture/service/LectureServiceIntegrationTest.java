package com.project.mentoridge.modules.lecture.service;

import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.configuration.annotation.ServiceTest;
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
import com.project.mentoridge.modules.lecture.controller.response.EachLectureResponse;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

import static com.project.mentoridge.configuration.AbstractTest.*;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
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

    private Subject subject1;
    private Subject subject2;

    @BeforeAll
    void init() {

        saveAddress(addressRepository);
        // saveSubject(subjectRepository);
        subjectRepository.deleteAll();
        subject1 = subjectRepository.save(Subject.builder()
                .subjectId(1L)
                .krSubject("???????????????")
                .learningKind(LearningKindType.IT)
                .build());
        subject2 = subjectRepository.save(Subject.builder()
                .subjectId(2L)
                .krSubject("?????????")
                .learningKind(LearningKindType.IT)
                .build());

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);
    }

    @DisplayName("?????? ??????")
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
        // ?????? ??????
        lecture1.approve(lectureLogService);

        // When
        LectureResponse response = lectureService.getLectureResponse(menteeUser, lecture1.getId());
        // Then
        assertAll(
                () -> assertThat(response.getId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response.getDifficulty()).isEqualTo(lecture1.getDifficulty()),

                // systems
                () -> assertThat(response.getSystems().size()).isEqualTo(lecture1.getSystems().size()),

                // lecturePrices
                () -> assertThat(response.getLecturePrices().size()).isEqualTo(lecture1.getLecturePrices().size()),
                () -> assertThat(response.getLecturePrices().get(0).getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response.getLecturePrices().get(0).isGroup()).isEqualTo(lecturePrice1.isGroup()),
                () -> assertThat(response.getLecturePrices().get(0).getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(response.getLecturePrices().get(0).getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(response.getLecturePrices().get(0).getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(response.getLecturePrices().get(0).getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(response.getLecturePrices().get(0).getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(response.getLecturePrices().get(0).getIsGroupStr()).isEqualTo("????????????"),
                () -> assertThat(response.getLecturePrices().get(0).getContent()).isEqualTo(String.format("????????? %d??? x 1??? %d?????? x ??? %d??? ?????? ??????", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(response.getLecturePrices().get(0).isClosed()).isEqualTo(lecturePrice1.isClosed()),

                () -> assertThat(response.getLecturePrices().get(1).getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(response.getLecturePrices().get(1).isGroup()).isEqualTo(lecturePrice2.isGroup()),
                () -> assertThat(response.getLecturePrices().get(1).getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(response.getLecturePrices().get(1).getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(response.getLecturePrices().get(1).getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(response.getLecturePrices().get(1).getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(response.getLecturePrices().get(1).getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(response.getLecturePrices().get(1).getIsGroupStr()).isEqualTo("1:1 ????????????"),
                () -> assertThat(response.getLecturePrices().get(1).getContent()).isEqualTo(String.format("????????? %d??? x 1??? %d?????? x ??? %d??? ?????? ??????", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                () -> assertThat(response.getLecturePrices().get(1).isClosed()).isEqualTo(lecturePrice2.isClosed()),

                // lectureSubjects
                () -> assertThat(response.getLectureSubjects().size()).isEqualTo(lecture1.getLectureSubjects().size()),
                () -> assertThat(response.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lectureSubject1.getSubject().getLearningKind().getName()),
                () -> assertThat(response.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lectureSubject1.getSubject().getKrSubject()),

                () -> assertThat(response.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response.isApproved()).isEqualTo(lecture1.isApproved()),
                () -> assertThat(response.getReviewCount()).isEqualTo(0L),
                () -> assertThat(response.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(response.getEnrollmentCount()).isNull(),

                // lectureMentor
                () -> assertThat(response.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response.getLectureMentor().getLectureCount()).isEqualTo(2L),
                () -> assertThat(response.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(response.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage())
        );
    }

    @Test
    void get_EachLectureResponse() {

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
        // ?????? ??????
        lecture1.approve(lectureLogService);

        // When
        EachLectureResponse response1 = lectureService.getEachLectureResponse(menteeUser, lecture1.getId(), lecturePrice1.getId());
        EachLectureResponse response2 = lectureService.getEachLectureResponse(menteeUser, lecture1.getId(), lecturePrice2.getId());
        // Then
        assertAll(
                () -> assertThat(response1.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response1.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response1.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response1.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response1.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response1.getDifficulty()).isEqualTo(lecture1.getDifficulty()),

                // systems
                () -> assertThat(response1.getSystems().size()).isEqualTo(lecture1.getSystems().size()),

                // lecturePrice
                () -> assertThat(response1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response1.getLecturePrice().isGroup()).isEqualTo(lecturePrice1.isGroup()),
                () -> assertThat(response1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(response1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(response1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(response1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(response1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(response1.getLecturePrice().getIsGroupStr()).isEqualTo("????????????"),
                () -> assertThat(response1.getLecturePrice().getContent()).isEqualTo(String.format("????????? %d??? x 1??? %d?????? x ??? %d??? ?????? ??????", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(response1.getLecturePrice().isClosed()).isEqualTo(lecturePrice1.isClosed()),
                () -> assertThat(response1.getLecturePriceId()).isEqualTo(lecturePrice1.getId()),

                // lectureSubjects
                () -> assertThat(response1.getLectureSubjects().size()).isEqualTo(lecture1.getLectureSubjects().size()),
                () -> assertThat(response1.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lectureSubject1.getSubject().getLearningKind().getName()),
                () -> assertThat(response1.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lectureSubject1.getSubject().getKrSubject()),

                () -> assertThat(response1.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response1.isApproved()).isEqualTo(lecture1.isApproved()),
                () -> assertThat(response1.getReviewCount()).isEqualTo(0L),
                () -> assertThat(response1.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(response1.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(response1.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response1.getLectureMentor().getLectureCount()).isEqualTo(2L),
                () -> assertThat(response1.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(response1.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response1.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(response1.getPicked()).isFalse(),
                () -> assertThat(response1.getPickCount()).isEqualTo(0L)
        );
        assertAll(
                () -> assertThat(response2.getLectureId()).isEqualTo(lecture1.getId()),
                () -> assertThat(response2.getTitle()).isEqualTo(lecture1.getTitle()),
                () -> assertThat(response2.getSubTitle()).isEqualTo(lecture1.getSubTitle()),
                () -> assertThat(response2.getIntroduce()).isEqualTo(lecture1.getIntroduce()),
                () -> assertThat(response2.getContent()).isEqualTo(lecture1.getContent()),
                () -> assertThat(response2.getDifficulty()).isEqualTo(lecture1.getDifficulty()),

                // systems
                () -> assertThat(response2.getSystems().size()).isEqualTo(lecture1.getSystems().size()),

                // lecturePrice
                () -> assertThat(response2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(response2.getLecturePrice().isGroup()).isEqualTo(lecturePrice2.isGroup()),
                () -> assertThat(response2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(response2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(response2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(response2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(response2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(response2.getLecturePrice().getIsGroupStr()).isEqualTo("1:1 ????????????"),
                () -> assertThat(response2.getLecturePrice().getContent()).isEqualTo(String.format("????????? %d??? x 1??? %d?????? x ??? %d??? ?????? ??????", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                () -> assertThat(response2.getLecturePrice().isClosed()).isEqualTo(lecturePrice2.isClosed()),
                () -> assertThat(response2.getLecturePriceId()).isEqualTo(lecturePrice2.getId()),

                // lectureSubjects
                () -> assertThat(response2.getLectureSubjects().size()).isEqualTo(lecture1.getLectureSubjects().size()),
                () -> assertThat(response2.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lectureSubject1.getSubject().getLearningKind().getName()),
                () -> assertThat(response2.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lectureSubject1.getSubject().getKrSubject()),

                () -> assertThat(response2.getThumbnail()).isEqualTo(lecture1.getThumbnail()),
                () -> assertThat(response2.isApproved()).isEqualTo(lecture1.isApproved()),
                () -> assertThat(response2.getReviewCount()).isEqualTo(0L),
                () -> assertThat(response2.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(response2.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(response2.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response2.getLectureMentor().getLectureCount()).isEqualTo(2L),
                () -> assertThat(response2.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(response2.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response2.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(response2.getPicked()).isFalse(),
                () -> assertThat(response2.getPickCount()).isEqualTo(0L)
        );
    }

    @Test
    void get_paged_EachLectureResponses_when_no_lectures_are_approved() {

        // Given
        lecture.cancelApproval();

        // When
        Page<EachLectureResponse> responses = lectureService.getEachLectureResponses(menteeUser, null, null, 1);

        // Then
        assertThat(responses.getTotalElements()).isEqualTo(0L);
    }

    // @DisplayName("?????? ?????? - LectureListRequest ???????????? ?????????")
    @DisplayName("?????? ??????")
    @Test
    void get_paged_EachLectureResponses() {

        // Given
        LecturePrice lecturePrice1 = lecture.getLecturePrices().get(0);
        LecturePrice lecturePrice2 = lecture.getLecturePrices().get(1);
        // When
        Page<EachLectureResponse> responses = lectureService.getEachLectureResponses(menteeUser, null, null, 1);

        // Then
        EachLectureResponse response1 = responses.getContent().get(0);
        EachLectureResponse response2 = responses.getContent().get(1);
        assertAll(
                () -> assertThat(response1.getLectureId()).isEqualTo(lecture.getId()),
                () -> assertThat(response1.getTitle()).isEqualTo(lecture.getTitle()),
                () -> assertThat(response1.getSubTitle()).isEqualTo(lecture.getSubTitle()),
                () -> assertThat(response1.getIntroduce()).isEqualTo(lecture.getIntroduce()),
                () -> assertThat(response1.getContent()).isEqualTo(lecture.getContent()),
                () -> assertThat(response1.getDifficulty()).isEqualTo(lecture.getDifficulty()),

                // systems
                () -> assertThat(response1.getSystems().size()).isEqualTo(lecture.getSystems().size()),

                // lecturePrice
                () -> assertThat(response1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response1.getLecturePrice().isGroup()).isEqualTo(lecturePrice1.isGroup()),
                () -> assertThat(response1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(response1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(response1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(response1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(response1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(response1.getLecturePrice().getIsGroupStr()).isEqualTo("????????????"),
                () -> assertThat(response1.getLecturePrice().getContent()).isEqualTo(String.format("????????? %d??? x 1??? %d?????? x ??? %d??? ?????? ??????", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(response1.getLecturePrice().isClosed()).isEqualTo(lecturePrice1.isClosed()),
                () -> assertThat(response1.getLecturePriceId()).isEqualTo(lecturePrice1.getId()),

                // lectureSubjects
                () -> assertThat(response1.getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),
                () -> assertThat(response1.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getLearningKind().getName()),
                () -> assertThat(response1.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()),

                () -> assertThat(response1.getThumbnail()).isEqualTo(lecture.getThumbnail()),
                () -> assertThat(response1.isApproved()).isEqualTo(lecture.isApproved()),
                () -> assertThat(response1.getReviewCount()).isEqualTo(0L),
                () -> assertThat(response1.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(response1.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(response1.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response1.getLectureMentor().getLectureCount()).isEqualTo(1L),
                () -> assertThat(response1.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(response1.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response1.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(response1.getPicked()).isFalse(),
                () -> assertThat(response1.getPickCount()).isEqualTo(0L)
        );
        assertAll(
                () -> assertThat(response2.getLectureId()).isEqualTo(lecture.getId()),
                () -> assertThat(response2.getTitle()).isEqualTo(lecture.getTitle()),
                () -> assertThat(response2.getSubTitle()).isEqualTo(lecture.getSubTitle()),
                () -> assertThat(response2.getIntroduce()).isEqualTo(lecture.getIntroduce()),
                () -> assertThat(response2.getContent()).isEqualTo(lecture.getContent()),
                () -> assertThat(response2.getDifficulty()).isEqualTo(lecture.getDifficulty()),

                // systems
                () -> assertThat(response2.getSystems().size()).isEqualTo(lecture.getSystems().size()),

                // lecturePrice
                () -> assertThat(response2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(response2.getLecturePrice().isGroup()).isEqualTo(lecturePrice2.isGroup()),
                () -> assertThat(response2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(response2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(response2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(response2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(response2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(response2.getLecturePrice().getIsGroupStr()).isEqualTo("1:1 ????????????"),
                () -> assertThat(response2.getLecturePrice().getContent()).isEqualTo(String.format("????????? %d??? x 1??? %d?????? x ??? %d??? ?????? ??????", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                () -> assertThat(response2.getLecturePrice().isClosed()).isEqualTo(lecturePrice2.isClosed()),
                () -> assertThat(response2.getLecturePriceId()).isEqualTo(lecturePrice2.getId()),

                // lectureSubjects
                () -> assertThat(response2.getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),
                () -> assertThat(response2.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getLearningKind().getName()),
                () -> assertThat(response2.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()),

                () -> assertThat(response2.getThumbnail()).isEqualTo(lecture.getThumbnail()),
                () -> assertThat(response2.isApproved()).isEqualTo(lecture.isApproved()),
                () -> assertThat(response2.getReviewCount()).isEqualTo(0L),
                () -> assertThat(response2.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(response2.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(response2.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response2.getLectureMentor().getLectureCount()).isEqualTo(1L),
                () -> assertThat(response2.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(response2.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response2.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(response2.getPicked()).isFalse(),
                () -> assertThat(response2.getPickCount()).isEqualTo(0L)
        );
    }

    @Test
    void ??????_??????() {

        // Given
        Address zone = mentorUser.getZone();  // ??????????????? ????????? ?????????
        assertAll(
                () -> assertEquals("???????????????", zone.getState()),
                () -> assertEquals("?????????", zone.getSiGunGu()),
                () -> assertEquals("?????????", zone.getDongMyunLi())
        );

        LecturePrice lecturePrice1 = lecture.getLecturePrices().get(0);
        LecturePrice lecturePrice2 = lecture.getLecturePrices().get(1);

        // When
        Page<EachLectureResponse> lectureResponses = lectureService.getEachLectureResponses(menteeUser, "??????????????? ?????????", null, 1);

        // Then
        assertEquals(2, lectureResponses.getTotalElements());
        EachLectureResponse response1 = lectureResponses.getContent().get(0);
        EachLectureResponse response2 = lectureResponses.getContent().get(1);
        assertAll(
                () -> assertThat(response1.getLectureId()).isEqualTo(lecture.getId()),
                () -> assertThat(response1.getTitle()).isEqualTo(lecture.getTitle()),
                () -> assertThat(response1.getSubTitle()).isEqualTo(lecture.getSubTitle()),
                () -> assertThat(response1.getIntroduce()).isEqualTo(lecture.getIntroduce()),
                () -> assertThat(response1.getContent()).isEqualTo(lecture.getContent()),
                () -> assertThat(response1.getDifficulty()).isEqualTo(lecture.getDifficulty()),

                // systems
                () -> assertThat(response1.getSystems().size()).isEqualTo(lecture.getSystems().size()),

                // lecturePrice
                () -> assertThat(response1.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice1.getId()),
                () -> assertThat(response1.getLecturePrice().isGroup()).isEqualTo(lecturePrice1.isGroup()),
                () -> assertThat(response1.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice1.getNumberOfMembers()),
                () -> assertThat(response1.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice1.getPricePerHour()),
                () -> assertThat(response1.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice1.getTimePerLecture()),
                () -> assertThat(response1.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice1.getNumberOfLectures()),
                () -> assertThat(response1.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice1.getTotalPrice()),
                () -> assertThat(response1.getLecturePrice().getIsGroupStr()).isEqualTo("????????????"),
                () -> assertThat(response1.getLecturePrice().getContent()).isEqualTo(String.format("????????? %d??? x 1??? %d?????? x ??? %d??? ?????? ??????", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())),
                () -> assertThat(response1.getLecturePrice().isClosed()).isEqualTo(lecturePrice1.isClosed()),
                () -> assertThat(response1.getLecturePriceId()).isEqualTo(lecturePrice1.getId()),

                // lectureSubjects
                () -> assertThat(response1.getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),
                () -> assertThat(response1.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getLearningKind().getName()),
                () -> assertThat(response1.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()),

                () -> assertThat(response1.getThumbnail()).isEqualTo(lecture.getThumbnail()),
                () -> assertThat(response1.isApproved()).isEqualTo(lecture.isApproved()),
                () -> assertThat(response1.getReviewCount()).isEqualTo(0L),
                () -> assertThat(response1.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(response1.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(response1.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response1.getLectureMentor().getLectureCount()).isEqualTo(1L),
                () -> assertThat(response1.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(response1.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response1.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(response1.getPicked()).isFalse(),
                () -> assertThat(response1.getPickCount()).isEqualTo(0L)
        );
        assertAll(
                () -> assertThat(response2.getLectureId()).isEqualTo(lecture.getId()),
                () -> assertThat(response2.getTitle()).isEqualTo(lecture.getTitle()),
                () -> assertThat(response2.getSubTitle()).isEqualTo(lecture.getSubTitle()),
                () -> assertThat(response2.getIntroduce()).isEqualTo(lecture.getIntroduce()),
                () -> assertThat(response2.getContent()).isEqualTo(lecture.getContent()),
                () -> assertThat(response2.getDifficulty()).isEqualTo(lecture.getDifficulty()),

                // systems
                () -> assertThat(response2.getSystems().size()).isEqualTo(lecture.getSystems().size()),

                // lecturePrice
                () -> assertThat(response2.getLecturePrice().getLecturePriceId()).isEqualTo(lecturePrice2.getId()),
                () -> assertThat(response2.getLecturePrice().isGroup()).isEqualTo(lecturePrice2.isGroup()),
                () -> assertThat(response2.getLecturePrice().getNumberOfMembers()).isEqualTo(lecturePrice2.getNumberOfMembers()),
                () -> assertThat(response2.getLecturePrice().getPricePerHour()).isEqualTo(lecturePrice2.getPricePerHour()),
                () -> assertThat(response2.getLecturePrice().getTimePerLecture()).isEqualTo(lecturePrice2.getTimePerLecture()),
                () -> assertThat(response2.getLecturePrice().getNumberOfLectures()).isEqualTo(lecturePrice2.getNumberOfLectures()),
                () -> assertThat(response2.getLecturePrice().getTotalPrice()).isEqualTo(lecturePrice2.getTotalPrice()),
                () -> assertThat(response2.getLecturePrice().getIsGroupStr()).isEqualTo("1:1 ????????????"),
                () -> assertThat(response2.getLecturePrice().getContent()).isEqualTo(String.format("????????? %d??? x 1??? %d?????? x ??? %d??? ?????? ??????", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())),
                () -> assertThat(response2.getLecturePrice().isClosed()).isEqualTo(lecturePrice2.isClosed()),
                () -> assertThat(response2.getLecturePriceId()).isEqualTo(lecturePrice2.getId()),

                // lectureSubjects
                () -> assertThat(response2.getLectureSubjects().size()).isEqualTo(lecture.getLectureSubjects().size()),
                () -> assertThat(response2.getLectureSubjects().get(0).getLearningKind()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getLearningKind().getName()),
                () -> assertThat(response2.getLectureSubjects().get(0).getKrSubject()).isEqualTo(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()),

                () -> assertThat(response2.getThumbnail()).isEqualTo(lecture.getThumbnail()),
                () -> assertThat(response2.isApproved()).isEqualTo(lecture.isApproved()),
                () -> assertThat(response2.getReviewCount()).isEqualTo(0L),
                () -> assertThat(response2.getScoreAverage()).isEqualTo(0.0),
                () -> assertThat(response2.getEnrollmentCount()).isEqualTo(0L),

                // lectureMentor
                () -> assertThat(response2.getLectureMentor().getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(response2.getLectureMentor().getLectureCount()).isEqualTo(1L),
                () -> assertThat(response2.getLectureMentor().getReviewCount()).isEqualTo(0L),
                () -> assertThat(response2.getLectureMentor().getNickname()).isEqualTo(mentorUser.getNickname()),
                () -> assertThat(response2.getLectureMentor().getImage()).isEqualTo(mentorUser.getImage()),

                () -> assertThat(response2.getPicked()).isFalse(),
                () -> assertThat(response2.getPickCount()).isEqualTo(0L)
        );
    }

    @Test
    void ??????_??????() {

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

    @DisplayName("?????? - ????????? ?????? ??????")
    @Test
    void createLecture_byMentee() {

        // Given
        // When
        // Then
        assertThrows(UnauthorizedException.class, () -> {
            lectureService.createLecture(menteeUser, lectureCreateRequest);
        });
    }

    @DisplayName("????????? ??? ?????? ????????? ?????? ??????")
    @Test
    void should_fail_to_update_enrolledLecture() {

        // Given
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        enrollmentService.check(mentorUser, enrollment.getId());

        // When
        // Then
        assertThrows(RuntimeException.class, () -> {
            lectureService.updateLecture(mentorUser, lecture.getId(), lectureUpdateRequest);
        });
    }

    @DisplayName("?????? ??????")
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
                () -> assertThat(lecturePrice).extracting("isGroup").isEqualTo(lecturePriceUpdateRequest.isGroup()),
                () -> assertThat(lecturePrice).extracting("numberOfMembers").isEqualTo(lecturePriceUpdateRequest.getNumberOfMembers()),
                () -> assertThat(lecturePrice).extracting("pricePerHour").isEqualTo(lecturePriceUpdateRequest.getPricePerHour()),
                () -> assertThat(lecturePrice).extracting("timePerLecture").isEqualTo(lecturePriceUpdateRequest.getTimePerLecture()),
                () -> assertThat(lecturePrice).extracting("numberOfLectures").isEqualTo(lecturePriceUpdateRequest.getNumberOfLectures()),
                () -> assertThat(lecturePrice).extracting("totalPrice").isEqualTo(lecturePriceUpdateRequest.getTotalPrice()),

                // ????????? ????????? ????????? ??????
                () -> assertThat(updatedLecture.isApproved()).isEqualTo(false)
        );
    }

    @DisplayName("?????? ??????")
    @Test
    void deleteLecture() {

        // Given
        // pick
        Long pickId = pickService.createPick(menteeUser, lecture.getId(), lecturePrice.getId());
        // enrollment
        Enrollment enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        enrollmentService.check(mentorUser, enrollment.getId());

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

    @DisplayName("?????? ?????? - ?????????")
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

    @DisplayName("?????? ????????? ?????? ?????? ???")
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

    @DisplayName("?????? ??????")
    @Test
    void open() {

        // Given
        // When
        lectureService.open(mentorUser, lecture.getId(), lecturePrice.getId());

        // Then
        assertFalse(lecturePrice.isClosed());
    }

    @DisplayName("?????? ?????? ??????")
    @Test
    void close() {

        // Given
        // When
        lectureService.close(mentorUser, lecture.getId(), lecturePrice.getId());

        // Then
        assertTrue(lecturePrice.isClosed());
    }
}