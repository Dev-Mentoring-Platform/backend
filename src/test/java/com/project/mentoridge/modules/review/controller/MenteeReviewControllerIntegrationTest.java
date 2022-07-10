package com.project.mentoridge.modules.review.controller;

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
import com.project.mentoridge.modules.purchase.service.EnrollmentService;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;

@Transactional
@MockMvcTest
class MenteeReviewControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final static String BASE_URL = "/api/mentees/my-reviews";

    @Autowired
    MockMvc mockMvc;

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
    EnrollmentService enrollmentService;
    @Autowired
    PickService pickService;

    @Autowired
    MenteeReviewService menteeReviewService;

    private User mentorUser;

    private User menteeUser;
    private String menteeAccessToken;

    private Lecture lecture;
    private LecturePrice lecturePrice;

    private Enrollment enrollment;
    private Long pickId;

    @BeforeAll
    void init() {

        saveAddress(addressRepository);
        saveSubject(subjectRepository);
        mentorUser = saveMentorUser(loginService, mentorService);
        menteeUser = saveMenteeUser(loginService);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);

        enrollment = enrollmentService.createEnrollment(menteeUser, lecture.getId(), lecturePrice.getId());
        pickId = savePick(pickService, menteeUser, lecture, lecturePrice);
    }

    @Test
    void get_reviews() {

        // given
        // when
        // then
    }

    @Test
    void get_review() {

    }

    @Test
    void edit_review() {

    }

    @Test
    void delete_review() {

    }
}