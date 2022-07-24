package com.project.mentoridge.modules.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.purchase.repository.EnrollmentRepository;
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
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
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
class MentorReviewControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentors/my-reviews";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginService loginService;
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
    LectureRepository lectureRepository;
    @Autowired
    LecturePriceRepository lecturePriceRepository;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MentorReviewService mentorReviewService;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    private User menteeUser1;
    private Mentee mentee1;
    private String menteeAccessTokenWithPrefix1;
    private User menteeUser2;
    private Mentee mentee2;
    private String menteeAccessTokenWithPrefix2;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessTokenWithPrefix;

    private Lecture lecture;
    private LecturePrice lecturePrice1;
    private LecturePrice lecturePrice2;

    private Enrollment enrollment1;
    private MenteeReview menteeReview1;
    private MentorReview mentorReview1;

    private Enrollment enrollment2;
    private MenteeReview menteeReview2;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        saveAddress(addressRepository);
        saveSubject(subjectRepository);

        menteeUser1 = saveMenteeUser("menteeUser1", loginService);
        mentee1 = menteeRepository.findByUser(menteeUser1);
        menteeAccessTokenWithPrefix1 = getAccessToken(menteeUser1.getUsername(), RoleType.MENTEE);
        menteeUser2 = saveMenteeUser("menteeUser2", loginService);
        mentee2 = menteeRepository.findByUser(menteeUser2);
        menteeAccessTokenWithPrefix2 = getAccessToken(menteeUser2.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessTokenWithPrefix = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);

        lecture = saveLecture(lectureService, mentorUser);
        List<LecturePrice> lecturePrices = lecturePriceRepository.findByLecture(lecture);
        lecturePrice1 = lecturePrices.get(0);
        lecturePrice2 = lecturePrices.get(1);
        // 강의 승인
        lecture.approve(lectureLogService);

        enrollment1 = enrollmentService.createEnrollment(menteeUser1, lecture.getId(), lecturePrice1.getId());
        enrollmentService.check(mentorUser, enrollment1.getId());
        enrollment2 = enrollmentService.createEnrollment(menteeUser2, lecture.getId(), lecturePrice2.getId());
        enrollmentService.check(mentorUser, enrollment2.getId());

        menteeReview1 = menteeReviewService.createMenteeReview(menteeUser1, enrollment1.getId(), menteeReviewCreateRequest);
        mentorReview1 = mentorReviewService.createMentorReview(mentorUser, lecture.getId(), menteeReview1.getId(), mentorReviewCreateRequest);

        menteeReview2 = menteeReviewService.createMenteeReview(menteeUser2, enrollment2.getId(), menteeReviewCreateRequest);
    }

    @Test
    void get_paged_my_reviews_written_by_my_mentees() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/by-mentees")
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].menteeReviewId").value(menteeReview2.getId()))
                .andExpect(jsonPath("$.content[0].enrollmentId").value(enrollment2.getId()))
                .andExpect(jsonPath("$.content[0].score").value(menteeReview2.getScore()))
                .andExpect(jsonPath("$.content[0].content").value(menteeReview2.getContent()))
                .andExpect(jsonPath("$.content[0].username").value(menteeUser2.getUsername()))
                .andExpect(jsonPath("$.content[0].userNickname").value(menteeUser2.getNickname()))
                .andExpect(jsonPath("$.content[0].userImage").value(menteeUser2.getImage()))
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                // child
                .andExpect(jsonPath("$.content[0].child").doesNotExist())
                // lecture
                .andExpect(jsonPath("$.content[0].lecture").exists())
                .andExpect(jsonPath("$.content[0].lecture.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.content[0].lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.content[0].lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.content[0].lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.content[0].lecture.difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$.content[0].lecture.systems").exists())
                // lecturePrice
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.lecturePriceId").value(lecturePrice2.getId()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.isGroup").value(lecturePrice2.isGroup()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.numberOfMembers").value(lecturePrice2.getNumberOfMembers()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.pricePerHour").value(lecturePrice2.getPricePerHour()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.timePerLecture").value(lecturePrice2.getTimePerLecture()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.numberOfLectures").value(lecturePrice2.getNumberOfLectures()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.totalPrice").value(lecturePrice2.getTotalPrice()))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.isGroupStr").value(lecturePrice2.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.content")
                        .value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice2.getPricePerHour(), lecturePrice2.getTimePerLecture(), lecturePrice2.getNumberOfLectures())))
                .andExpect(jsonPath("$.content[0].lecture.lecturePrice.closed").value(lecturePrice2.isClosed()))

                .andExpect(jsonPath("$.content[0].lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$.content[0].lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.content[0].lecture.approved").value(lecture.isApproved()))
                .andExpect(jsonPath("$.content[0].lecture.mentorNickname").value(mentorUser.getNickname()))

                .andExpect(jsonPath("$.content[0].lecture.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.content[0].lecture.pickCount").doesNotExist())

                .andExpect(jsonPath("$.content[1].menteeReviewId").value(menteeReview1.getId()))
                .andExpect(jsonPath("$.content[1].enrollmentId").value(enrollment1.getId()))
                .andExpect(jsonPath("$.content[1].score").value(menteeReview1.getScore()))
                .andExpect(jsonPath("$.content[1].content").value(menteeReview1.getContent()))
                .andExpect(jsonPath("$.content[1].username").value(menteeUser1.getUsername()))
                .andExpect(jsonPath("$.content[1].userNickname").value(menteeUser1.getNickname()))
                .andExpect(jsonPath("$.content[1].userImage").value(menteeUser1.getImage()))
                .andExpect(jsonPath("$.content[1].createdAt").exists())
                // child
                .andExpect(jsonPath("$.content[1].child").exists())
                .andExpect(jsonPath("$.content[1].child.mentorReviewId").value(mentorReview1.getId()))
                .andExpect(jsonPath("$.content[1].child.content").value(mentorReview1.getContent()))
                .andExpect(jsonPath("$.content[1].child.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.content[1].child.userNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.content[1].child.userImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.content[1].child.createdAt").exists())
                // lecture
                .andExpect(jsonPath("$.content[1].lecture").exists())
                .andExpect(jsonPath("$.content[1].lecture.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.content[1].lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.content[1].lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.content[1].lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.content[1].lecture.difficulty").value(lecture.getDifficulty()))
                .andExpect(jsonPath("$.content[1].lecture.systems").exists())
                // lecturePrice
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice.lecturePriceId").value(lecturePrice1.getId()))
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice.isGroup").value(lecturePrice1.isGroup()))
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice.numberOfMembers").value(lecturePrice1.getNumberOfMembers()))
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice.pricePerHour").value(lecturePrice1.getPricePerHour()))
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice.timePerLecture").value(lecturePrice1.getTimePerLecture()))
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice.numberOfLectures").value(lecturePrice1.getNumberOfLectures()))
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice.totalPrice").value(lecturePrice1.getTotalPrice()))
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice.isGroupStr").value(lecturePrice1.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice.content")
                        .value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice1.getPricePerHour(), lecturePrice1.getTimePerLecture(), lecturePrice1.getNumberOfLectures())))
                .andExpect(jsonPath("$.content[1].lecture.lecturePrice.closed").value(lecturePrice1.isClosed()))

                .andExpect(jsonPath("$.content[1].lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$.content[1].lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.content[1].lecture.approved").value(lecture.isApproved()))
                .andExpect(jsonPath("$.content[1].lecture.mentorNickname").value(mentorUser.getNickname()))

                .andExpect(jsonPath("$.content[1].lecture.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.content[1].lecture.pickCount").doesNotExist());
    }
}