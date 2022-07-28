package com.project.mentoridge.modules.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.EnrollmentLogService;
import com.project.mentoridge.modules.log.component.LectureLogService;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
class MenteeReviewControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentees/my-reviews";

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
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentLogService enrollmentLogService;
    @Autowired
    PickService pickService;

    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;
    @Autowired
    MentorReviewService mentorReviewService;
    @Autowired
    MentorReviewRepository mentorReviewRepository;

    private User mentorUser;

    private User menteeUser;
    private String menteeAccessTokenWithPrefix;

    private Lecture lecture;
    private LecturePrice lecturePrice;

    private Enrollment enrollment;
    private Long pickId;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        mentorUser = saveMentorUser(loginService, mentorService);
        menteeUser = saveMenteeUser(loginService);
        menteeAccessTokenWithPrefix = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);
        // 강의 승인
        lecture.approve(lectureLogService);

        enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        // 멘토 승인
        enrollment.check(mentorUser, enrollmentLogService);
        pickId = savePick(pickService, menteeUser, lecture, lecturePrice);
    }

    @Test
    void get_reviews() throws Exception {

        // Given
        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        // When
        // Then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].menteeReviewId").value(menteeReview.getId()))
                .andExpect(jsonPath("$.content[0].enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$.content[0].score").value(menteeReview.getScore()))
                .andExpect(jsonPath("$.content[0].content").value(menteeReview.getContent()))
                .andExpect(jsonPath("$.content[0].username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.content[0].userNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.content[0].userImage").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                // child
                .andExpect(jsonPath("$.content[0].child").exists())
                .andExpect(jsonPath("$.content[0].child.mentorReviewId").value(mentorReview.getId()))
                .andExpect(jsonPath("$.content[0].child.content").value(mentorReview.getContent()))
                .andExpect(jsonPath("$.content[0].child.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.content[0].child.userNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.content[0].child.userImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.content[0].child.createdAt").exists())
                // lecture
                .andExpect(jsonPath("$.content[0].lecture").exists())
                .andExpect(jsonPath("$.content[0].lecture.id").value(lecture.getId()))
                .andExpect(jsonPath("$.content[0].lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.content[0].lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.content[0].lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.content[0].lecture.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.content[0].lecture.difficulty").value(lecture.getDifficulty().name()))
                .andExpect(jsonPath("$.content[0].lecture.systems").exists())
                // lecturePrice
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.isGroup").value(lecturePrice.isGroup()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.content")
                        .value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.closed").value(lecturePrice.isClosed()))

                .andExpect(jsonPath("$.content[0].lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$.content[0].lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.content[0].lecture.approved").value(lecture.isApproved()))
                .andExpect(jsonPath("$.content[0].lecture.mentorNickname").value(mentorUser.getNickname()))

                .andExpect(jsonPath("$.content[0].lecture.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.content[0].lecture.pickCount").doesNotExist());
    }

    @Test
    void get_review() throws Exception {

        // Given
        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{mentee_review_id}", menteeReview.getId())
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menteeReviewId").value(menteeReview.getId()))
                .andExpect(jsonPath("$.enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$.score").value(menteeReview.getScore()))
                .andExpect(jsonPath("$.content").value(menteeReview.getContent()))
                .andExpect(jsonPath("$.username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.userNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.userImage").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.createdAt").exists())
                // child
                .andExpect(jsonPath("$.child").exists())
                .andExpect(jsonPath("$.child.mentorReviewId").value(mentorReview.getId()))
                .andExpect(jsonPath("$.child.content").value(mentorReview.getContent()))
                .andExpect(jsonPath("$.child.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.child.userNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.child.userImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.child.createdAt").exists())
                // lecture
                .andExpect(jsonPath("$.lecture").exists())
                .andExpect(jsonPath("$.lecture.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.lecture.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.lecture.difficulty").value(lecture.getDifficulty().name()))
                .andExpect(jsonPath("$.lecture.systems").exists())
                // lecturePrice
                .andExpect(jsonPath("$.lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.lecture.lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.lecture.lecturePrice.isGroup").value(lecturePrice.isGroup()))
                .andExpect(jsonPath("$.lecture.lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$.lecture.lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$.lecture.lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$.lecture.lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$.lecture.lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$.lecture.lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.lecture.lecturePrice.content")
                        .value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$.lecture.lecturePrice.closed").value(lecturePrice.isClosed()))

                .andExpect(jsonPath("$.lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$.lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.lecture.approved").value(lecture.isApproved()))
                .andExpect(jsonPath("$.lecture.mentorNickname").value(mentorUser.getNickname()))

                .andExpect(jsonPath("$.lecture.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.lecture.pickCount").doesNotExist());
    }

    @Test
    void edit_review() throws Exception {

        // Given
        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        // When
        mockMvc.perform(put(BASE_URL + "/{mentee_review_id}", menteeReview.getId())
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(menteeReviewUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        MenteeReview updatedMenteeReview = menteeReviewRepository.findById(menteeReview.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertEquals(menteeReviewUpdateRequest.getScore(), updatedMenteeReview.getScore()),
                () -> assertEquals(menteeReviewUpdateRequest.getContent(), updatedMenteeReview.getContent())
        );
    }

    @Test
    void delete_review() throws Exception {

        // Given
        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        // When
        mockMvc.perform(delete(BASE_URL + "/{mentee_review_id}", menteeReview.getId())
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        // Then
        assertFalse(menteeReviewRepository.findById(menteeReview.getId()).isPresent());
        assertFalse(mentorReviewRepository.findById(mentorReview.getId()).isPresent());
    }
}