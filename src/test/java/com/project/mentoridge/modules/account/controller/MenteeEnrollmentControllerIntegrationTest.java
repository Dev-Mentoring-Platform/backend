package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.repository.MenteeReviewRepository;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.init.TestDataBuilder.getMenteeReviewCreateRequestWithScoreAndContent;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
class MenteeEnrollmentControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentees/my-enrollments";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    LoginService loginService;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;

    @Autowired
    PickService pickService;
    @Autowired
    EnrollmentService enrollmentService;

    @Autowired
    MenteeReviewRepository menteeReviewRepository;

    private User mentorUser;
    private Mentor mentor;

    private User menteeUser;
    private String menteeAccessToken;

    private Lecture lecture;
    private LecturePrice lecturePrice;
//    private Enrollment enrollment;
    private Long pickId;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);

        menteeUser = saveMenteeUser(loginService);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        lecture = saveLecture(lectureService, mentorUser);
        // 강의 승인
        lecture.approve(lectureLogService);
        lecturePrice = getLecturePrice(lecture);
        pickId = savePick(pickService, menteeUser, lecture, lecturePrice);
    }

    @DisplayName("승인 예정 강의 리스트")
    @Test
    void get_unchecked_enrollments() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/unchecked", 1)
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$..title").value(lecture.getTitle()))
                .andExpect(jsonPath("$..subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$..introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$..content").value(lecture.getContent()))
                .andExpect(jsonPath("$..difficulty").value(lecture.getDifficulty().name()))

                // systems
                .andExpect(jsonPath("$..lecture.systems").exists())
                .andExpect(jsonPath("$..lecture.systems[0].type").value(lecture.getSystems().get(0).getType()))
                .andExpect(jsonPath("$..lecture.systems[0].name").value(lecture.getSystems().get(0).getName()))
                .andExpect(jsonPath("$..lecture.systems[1].type").value(lecture.getSystems().get(1).getType()))
                .andExpect(jsonPath("$..lecture.systems[1].name").value(lecture.getSystems().get(1).getName()))

                // lectureSubjects
                .andExpect(jsonPath("$..lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$..lecture.lectureSubjects[0].learningKind").value(lecture.getLectureSubjects().get(0).getSubject().getLearningKind()))
                .andExpect(jsonPath("$..lecture.lectureSubjects[0].krSubject").value(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()))

                // lecturePrice
                .andExpect(jsonPath("$..lecture.lecturePrice").exists())
                .andExpect(jsonPath("$..lecture.lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$..lecture.lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$..lecture.lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$..lecture.lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$..lecture.lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$..lecture.lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$..lecture.lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$..lecture.lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$..lecture.lecturePrice.closed").value(lecturePrice.isClosed()))
                .andExpect(jsonPath("$..lecture.lecturePrice.group").value(lecturePrice.isGroup()))

                .andExpect(jsonPath("$..thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$..approved").value(lecture.isApproved()))

                .andExpect(jsonPath("$..enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$..checked").value(enrollment.isChecked()))
                .andExpect(jsonPath("$..finished").value(enrollment.isFinished()))

                .andExpect(jsonPath("$..lectureMentor.mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$..lectureMentor.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$..lectureMentor.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$..lectureMentor.lectureCount").doesNotExist())
                .andExpect(jsonPath("$..lectureMentor.reviewCount").doesNotExist());

    }

    @DisplayName("승인 완료 강의 리스트")
    @Test
    void get_checked_enrollments() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(mentorUser, enrollment.getId());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/checked", 1)
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$..title").value(lecture.getTitle()))
                .andExpect(jsonPath("$..subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$..introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$..content").value(lecture.getContent()))
                .andExpect(jsonPath("$..difficulty").value(lecture.getDifficulty().name()))

                // systems
                .andExpect(jsonPath("$..lecture.systems").exists())
                .andExpect(jsonPath("$..lecture.systems[0].type").value(lecture.getSystems().get(0).getType()))
                .andExpect(jsonPath("$..lecture.systems[0].name").value(lecture.getSystems().get(0).getName()))
                .andExpect(jsonPath("$..lecture.systems[1].type").value(lecture.getSystems().get(1).getType()))
                .andExpect(jsonPath("$..lecture.systems[1].name").value(lecture.getSystems().get(1).getName()))

                // lectureSubjects
                .andExpect(jsonPath("$..lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$..lecture.lectureSubjects[0].learningKind").value(lecture.getLectureSubjects().get(0).getSubject().getLearningKind()))
                .andExpect(jsonPath("$..lecture.lectureSubjects[0].krSubject").value(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()))

                // lecturePrice
                .andExpect(jsonPath("$..lecture.lecturePrice").exists())
                .andExpect(jsonPath("$..lecture.lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$..lecture.lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$..lecture.lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$..lecture.lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$..lecture.lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$..lecture.lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$..lecture.lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$..lecture.lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$..lecture.lecturePrice.closed").value(lecturePrice.isClosed()))
                .andExpect(jsonPath("$..lecture.lecturePrice.group").value(lecturePrice.isGroup()))

                .andExpect(jsonPath("$..thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$..approved").value(lecture.isApproved()))

                .andExpect(jsonPath("$..enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$..checked").value(enrollment.isChecked()))
                .andExpect(jsonPath("$..finished").value(enrollment.isFinished()))

                .andExpect(jsonPath("$..lectureMentor.mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$..lectureMentor.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$..lectureMentor.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$..lectureMentor.lectureCount").doesNotExist())
                .andExpect(jsonPath("$..lectureMentor.reviewCount").doesNotExist());
    }


    @DisplayName("승인 완료 강의 리스트")
    @Test
    void get_checked_enrollments_when_no_enrollment_is_checked() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/checked", 1)
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().isEmpty());
    }

    @DisplayName("승인 완료 강의 개별 조회")
    @Test
    void get_checked_enrolled_lecture() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(mentorUser, enrollment.getId());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{enrollment_id}/lecture", enrollment.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.difficulty").value(lecture.getDifficulty()))

                // systems
                .andExpect(jsonPath("$.systems").exists())
                .andExpect(jsonPath("$.systems[0].type").value(lecture.getSystems().get(0).getType()))
                .andExpect(jsonPath("$.systems[0].name").value(lecture.getSystems().get(0).getName()))
                .andExpect(jsonPath("$.systems[1].type").value(lecture.getSystems().get(1).getType()))
                .andExpect(jsonPath("$.systems[1].name").value(lecture.getSystems().get(1).getName()))

                // lectureSubjects
                .andExpect(jsonPath("$.lectureSubjects").exists())
                .andExpect(jsonPath("$.lectureSubjects[0].learningKind").value(lecture.getLectureSubjects().get(0).getSubject().getLearningKind()))
                .andExpect(jsonPath("$.lectureSubjects[0].krSubject").value(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()))

                .andExpect(jsonPath("$.lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.lecture.approved").value(lecture.isApproved()))
                // lecturePrice
                .andExpect(jsonPath("$.lecturePrice").exists())
                .andExpect(jsonPath("$.lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$.lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$.lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$.lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$.lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$.lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$.lecturePrice.closed").value(lecturePrice.isClosed()))
                .andExpect(jsonPath("$.lecturePrice.group").value(lecturePrice.isGroup()))


                .andExpect(jsonPath("$.lectureMentor.mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.lectureMentor.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.lectureMentor.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.lectureMentor.lectureCount").doesNotExist())
                .andExpect(jsonPath("$.lectureMentor.reviewCount").doesNotExist())

                .andExpect(jsonPath("$.reviewCount").doesNotExist())
                .andExpect(jsonPath("$.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.enrollmentCount").doesNotExist())
                .andExpect(jsonPath("$.picked").doesNotExist())
                .andExpect(jsonPath("$.pickCount").doesNotExist());
    }

    @DisplayName("리뷰 미작성 수강내역 리스트")
    @Test
    void get_unreviewed_enrollments() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(mentorUser, enrollment.getId());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/unreviewed")
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$.mentee").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.lectureTitle").value(lecture.getTitle()))
                .andExpect(jsonPath("$.createdAt").value(LocalDateTimeUtil.getDateTimeToString(enrollment.getCreatedAt())))

                .andExpect(jsonPath("$.lecture.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.lecture.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.lecture.difficulty").value(lecture.getDifficulty()))

                // systems
                .andExpect(jsonPath("$.lecture.systems").exists())
                .andExpect(jsonPath("$.lecture.systems[0].type").value(lecture.getSystems().get(0).getType()))
                .andExpect(jsonPath("$.lecture.systems[0].name").value(lecture.getSystems().get(0).getName()))
                .andExpect(jsonPath("$.lecture.systems[1].type").value(lecture.getSystems().get(1).getType()))
                .andExpect(jsonPath("$.lecture.systems[1].name").value(lecture.getSystems().get(1).getName()))

                // lectureSubjects
                .andExpect(jsonPath("$.lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$.lecture.lectureSubjects[0].learningKind").value(lecture.getLectureSubjects().get(0).getSubject().getLearningKind()))
                .andExpect(jsonPath("$.lecture.lectureSubjects[0].krSubject").value(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()))

                // lecturePrice
                .andExpect(jsonPath("$.lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.lecture.lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.lecture.lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$.lecture.lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$.lecture.lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$.lecture.lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$.lecture.lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))

                .andExpect(jsonPath("$.lecture.lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.lecture.lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$.lecture.lecturePrice.closed").value(lecturePrice.isClosed()))
                .andExpect(jsonPath("$.lecture.lecturePrice.group").value(lecturePrice.isGroup()))

                .andExpect(jsonPath("$.lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.lecture.mentorNickname").value(lecture.getMentor().getUser().getNickname()))
                .andExpect(jsonPath("$.lecture.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.lecture.pickCount").doesNotExist());
    }

    @DisplayName("수강내역 조회")
    @Test
    void get_enrollment() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(mentorUser, enrollment.getId());

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{enrollment_id}", enrollment.getId())
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$.mentee").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.lectureTitle").value(lecture.getTitle()))
                .andExpect(jsonPath("$.createdAt").value(LocalDateTimeUtil.getDateTimeToString(enrollment.getCreatedAt())))

                .andExpect(jsonPath("$.lecture.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.lecture.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.lecture.difficulty").value(lecture.getDifficulty()))

                // systems
                .andExpect(jsonPath("$.lecture.systems").exists())
                .andExpect(jsonPath("$.lecture.systems[0].type").value(lecture.getSystems().get(0).getType()))
                .andExpect(jsonPath("$.lecture.systems[0].name").value(lecture.getSystems().get(0).getName()))
                .andExpect(jsonPath("$.lecture.systems[1].type").value(lecture.getSystems().get(1).getType()))
                .andExpect(jsonPath("$.lecture.systems[1].name").value(lecture.getSystems().get(1).getName()))

                // lectureSubjects
                .andExpect(jsonPath("$.lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$.lecture.lectureSubjects[0].learningKind").value(lecture.getLectureSubjects().get(0).getSubject().getLearningKind()))
                .andExpect(jsonPath("$.lecture.lectureSubjects[0].krSubject").value(lecture.getLectureSubjects().get(0).getSubject().getKrSubject()))

                // lecturePrice
                .andExpect(jsonPath("$.lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.lecture.lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.lecture.lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$.lecture.lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$.lecture.lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$.lecture.lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$.lecture.lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))

                .andExpect(jsonPath("$.lecture.lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.lecture.lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$.lecture.lecturePrice.closed").value(lecturePrice.isClosed()))
                .andExpect(jsonPath("$.lecture.lecturePrice.group").value(lecturePrice.isGroup()))

                .andExpect(jsonPath("$.lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.lecture.mentorNickname").value(lecture.getMentor().getUser().getNickname()))
                .andExpect(jsonPath("$.lecture.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.lecture.pickCount").doesNotExist());
    }

    @DisplayName("리뷰 작성")
    @Test
    void new_review() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(mentorUser, enrollment.getId());

        // when
        // then
        MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequestWithScoreAndContent(3, "good");
        mockMvc.perform(post(BASE_URL + "/{enrollment_id}/reviews", enrollment.getId())
                        .header(AUTHORIZATION, menteeAccessToken)
                        .content(objectMapper.writeValueAsString(menteeReviewCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        assertNotNull(menteeReviewRepository.findByEnrollment(enrollment));
    }


    @DisplayName("리뷰 작성 - invalid input")
    @Test
    void new_review_with_invalid_input() throws Exception {

        // given
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        enrollmentService.check(mentorUser, enrollment.getId());

        // when
        // then
        MenteeReviewCreateRequest menteeReviewCreateRequest = getMenteeReviewCreateRequestWithScoreAndContent(6, "");
        mockMvc.perform(post(BASE_URL + "/{enrollment_id}/reviews", enrollment.getId())
                        .header(AUTHORIZATION, menteeAccessToken)
                        .content(objectMapper.writeValueAsString(menteeReviewCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
        assertNull(menteeReviewRepository.findByEnrollment(enrollment));
    }
}