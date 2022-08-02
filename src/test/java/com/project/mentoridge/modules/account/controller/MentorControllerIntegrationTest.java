package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.response.ErrorCode;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.controller.request.CareerUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.EducationUpdateRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.*;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MenteeService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
class MentorControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final String BASE_URL = "/api/mentors";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    LoginService loginService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CareerRepository careerRepository;
    @Autowired
    EducationRepository educationRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    MenteeService menteeService;
    @Autowired
    MenteeRepository menteeRepository;

    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    LectureRepository lectureRepository;
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
    ChatService chatService;
    @Autowired
    ChatroomRepository chatroomRepository;
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    private User menteeUser;
    private Mentee mentee;
    private String menteeAccessTokenWithPrefix;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessTokenWithPrefix;

    private Lecture lecture;
    private LecturePrice lecturePrice;

    private Chatroom chatroom;
    private Long pickId;
    private Enrollment enrollment;

    private MenteeReview menteeReview;
    private MentorReview mentorReview;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        saveAddress(addressRepository);
        saveSubject(subjectRepository);

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessTokenWithPrefix = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessTokenWithPrefix = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);

        lecture = saveLecture(lectureService, mentorUser);
        lecturePrice = getLecturePrice(lecture);
        // 강의 승인
        lecture.approve(lectureLogService);
        chatroom = chatroomRepository.save(Chatroom.builder()
                        .mentor(mentor)
                        .mentee(mentee)
                .build());
        pickId = savePick(pickService, menteeUser, lecture, lecturePrice);
        enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        // 신청 승인
        enrollment.check(mentorUser, enrollmentLogService);

        menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);
    }

    @Test
    void getMentors() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.content[0].user").exists())
                .andExpect(jsonPath("$.content[0].user.userId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.content[0].user.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.content[0].user.role").value(mentorUser.getRole().name()))
                .andExpect(jsonPath("$.content[0].user.name").value(mentorUser.getName()))
                .andExpect(jsonPath("$.content[0].user.gender").value(mentorUser.getGender().name()))
                .andExpect(jsonPath("$.content[0].user.birthYear").value(mentorUser.getBirthYear()))
                .andExpect(jsonPath("$.content[0].user.phoneNumber").value(mentorUser.getPhoneNumber()))
                .andExpect(jsonPath("$.content[0].user.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.content[0].user.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.content[0].user.zone").value(AddressUtils.convertEmbeddableToStringAddress(mentorUser.getZone())))
                .andExpect(jsonPath("$.content[0].bio").value(mentor.getBio()))
                .andExpect(jsonPath("$.content[0].careers").isArray())
                .andExpect(jsonPath("$.content[0].careers", hasSize(mentor.getCareers().size())))
                .andExpect(jsonPath("$.content[0].educations", hasSize(mentor.getEducations().size())))
                .andExpect(jsonPath("$.content[0].accumulatedMenteeCount").doesNotExist());
    }

    @Test
    void getMyInfo() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/my-info")
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mentorId").value(mentor.getId()))

                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.userId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.user.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.user.role").value(mentorUser.getRole().name()))
                .andExpect(jsonPath("$.user.name").value(mentorUser.getName()))
                .andExpect(jsonPath("$.user.gender").value(mentorUser.getGender().name()))
                .andExpect(jsonPath("$.user.birthYear").value(mentorUser.getBirthYear()))
                .andExpect(jsonPath("$.user.phoneNumber").value(mentorUser.getPhoneNumber()))
                .andExpect(jsonPath("$.user.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.user.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.user.zone").value(AddressUtils.convertEmbeddableToStringAddress(mentorUser.getZone())))

                .andExpect(jsonPath("$.bio").value(mentor.getBio()))
                .andExpect(jsonPath("$.careers").isArray())
                .andExpect(jsonPath("$.careers", hasSize(mentor.getCareers().size())))
                .andExpect(jsonPath("$.educations", hasSize(mentor.getEducations().size())))
                .andExpect(jsonPath("$.accumulatedMenteeCount").value(1L));
    }

    @Test
    void getMentor() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}", mentor.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mentorId").value(mentor.getId()))

                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.userId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.user.username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.user.role").value(mentorUser.getRole().name()))
                .andExpect(jsonPath("$.user.name").value(mentorUser.getName()))
                .andExpect(jsonPath("$.user.gender").value(mentorUser.getGender().name()))
                .andExpect(jsonPath("$.user.birthYear").value(mentorUser.getBirthYear()))
                .andExpect(jsonPath("$.user.phoneNumber").value(mentorUser.getPhoneNumber()))
                .andExpect(jsonPath("$.user.nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.user.image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.user.zone").value(AddressUtils.convertEmbeddableToStringAddress(mentorUser.getZone())))

                .andExpect(jsonPath("$.bio").value(mentor.getBio()))
                .andExpect(jsonPath("$.careers").isArray())
                .andExpect(jsonPath("$.careers", hasSize(mentor.getCareers().size())))
                .andExpect(jsonPath("$.educations", hasSize(mentor.getEducations().size())))
                .andExpect(jsonPath("$.accumulatedMenteeCount").value(1L));
    }

    @DisplayName("menteeUser -> mentor")
    @Test
    void newMentor() throws Exception {

        // Given
        // When
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(mentorSignUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());

        // Then
        Mentor mentor = mentorRepository.findByUser(menteeUser);
        assertAll(
                () -> assertNotNull(mentor),
                () -> assertEquals(mentor.getCareers().size(), careerRepository.findByMentor(mentor).size()),
                () -> assertEquals(mentor.getEducations().size(), educationRepository.findByMentor(mentor).size())
        );
    }
/*
    @DisplayName("Mentor 등록 - Invalid Input")
    @Test
    public void newMentor_withInvalidInput() throws Exception {

        // Given
        // When
        // Then
//        CareerCreateRequest careerCreateRequest = CareerCreateRequest.of(
//                "mentoridge",
//                null,
//                "2007-12-03",
//                "",
//                true
//        );
//
//        mentorSignUpRequest.addCareerCreateRequest(careerCreateRequest);
//        mockMvc.perform(post(BASE_URL)
//                .content(objectMapper.writeValueAsString(mentorSignUpRequest))
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(jsonPath("$.message").value("Invalid Input"))
//                .andExpect(jsonPath("$.code").value(400));
    }*/

    @DisplayName("멘토 등록 - 멘티만 가능")
    @Test
    void newMentor_alreadyMentor() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(mentorSignUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @DisplayName("Mentor 등록 - 인증된 사용자 X")
    @Test
    void newMentor_withoutAuthenticatedUser() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(post(BASE_URL)
                        .content(objectMapper.writeValueAsString(mentorSignUpRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(ErrorCode.UNAUTHORIZED.getCode()));
    }

    @Test
    void Mentor_수정() throws Exception {

        // Given
        // When
        CareerUpdateRequest careerUpdateRequest = mentorUpdateRequest.getCareers().get(0);
        EducationUpdateRequest educationUpdateRequest = mentorUpdateRequest.getEducations().get(0);
        mockMvc.perform(put(BASE_URL + "/my-info")
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(mentorUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        Mentor updatedMentor = mentorRepository.findByUser(mentorUser);
        assertAll(
                () -> assertThat(updatedMentor.getBio()).isEqualTo(mentorUpdateRequest.getBio()),

                () -> assertThat(updatedMentor.getCareers().get(0).getMentor()).isEqualTo(mentor),
                () -> assertThat(updatedMentor.getCareers().get(0).getJob()).isEqualTo(mentor.getCareers().get(0).getJob()),
                () -> assertThat(updatedMentor.getCareers().get(0).getCompanyName()).isEqualTo(mentor.getCareers().get(0).getCompanyName()),
                () -> assertThat(updatedMentor.getCareers().get(0).getOthers()).isEqualTo(mentor.getCareers().get(0).getOthers()),
                () -> assertThat(updatedMentor.getCareers().get(0).getLicense()).isEqualTo(mentor.getCareers().get(0).getLicense()),

                () -> assertThat(updatedMentor.getEducations().get(0).getEducationLevel()).isEqualTo(mentor.getEducations().get(0).getEducationLevel()),
                () -> assertThat(updatedMentor.getEducations().get(0).getSchoolName()).isEqualTo(mentor.getEducations().get(0).getSchoolName()),
                () -> assertThat(updatedMentor.getEducations().get(0).getMajor()).isEqualTo(mentor.getEducations().get(0).getMajor()),
                () -> assertThat(updatedMentor.getEducations().get(0).getOthers()).isEqualTo(mentor.getEducations().get(0).getOthers())
        );
    }

    @Test
    void Mentor_탈퇴_진행중인_강의가_존재할때() throws Exception {

        // Given
        List<Long> careerIds = careerRepository.findByMentor(mentor).stream()
                .map(BaseEntity::getId).collect(Collectors.toList());
        List<Long> educationIds = educationRepository.findByMentor(mentor).stream()
                .map(BaseEntity::getId).collect(Collectors.toList());

        // When
        mockMvc.perform(delete(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        // Then
        User _user = userRepository.findByUsername(mentorUser.getUsername()).orElseThrow(RuntimeException::new);
        assertEquals(RoleType.MENTOR, _user.getRole());

        // mentor
        assertNotNull(mentorRepository.findByUser(_user));
        // career
        for (Long careerId : careerIds) {
            assertTrue(careerRepository.findById(careerId).isPresent());
        }
        // education
        for (Long educationId : educationIds) {
            assertTrue(educationRepository.findById(educationId).isPresent());
        }
        // chatroom
        assertTrue(chatroomRepository.findById(chatroom.getId()).isPresent());
        // lecture - lecturePrice, lectureSubject
        assertTrue(lectureRepository.findById(lecture.getId()).isPresent());
        // enrollment
        assertTrue(enrollmentRepository.findById(enrollment.getId()).isPresent());
        // pick
        assertTrue(pickRepository.findById(pickId).isPresent());
        // review
        assertTrue(mentorReviewRepository.findById(mentorReview.getId()).isPresent());
        assertTrue(menteeReviewRepository.findById(menteeReview.getId()).isPresent());
    }

    @Test
    void Mentor_탈퇴() throws Exception {

        // Given
        List<Long> careerIds = careerRepository.findByMentor(mentor).stream()
                .map(BaseEntity::getId).collect(Collectors.toList());
        List<Long> educationIds = educationRepository.findByMentor(mentor).stream()
                .map(BaseEntity::getId).collect(Collectors.toList());
        // 강의 종료
        enrollment.finish(menteeUser, enrollmentLogService);

        // When
        mockMvc.perform(delete(BASE_URL)
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User deletedMentorUser = userRepository.findByUsername(mentorUser.getUsername()).orElseThrow(RuntimeException::new);
        assertEquals(RoleType.MENTEE, deletedMentorUser.getRole());

        // mentor
        assertNull(mentorRepository.findByUser(deletedMentorUser));
        // career
        for (Long careerId : careerIds) {
            assertFalse(careerRepository.findById(careerId).isPresent());
        }
        // education
        for (Long educationId : educationIds) {
            assertFalse(educationRepository.findById(educationId).isPresent());
        }
        // chatroom
        assertTrue(chatroomRepository.findByMentor(mentor).isEmpty());
        // message
        assertTrue(messageRepository.findBySender(mentorUser).isEmpty());
        // lecture - lecturePrice, lectureSubject
        assertTrue(lectureRepository.findByMentor(mentor).isEmpty());

        // enrollment, pick
        assertTrue(enrollmentRepository.findByLecture(lecture).isEmpty());
        assertTrue(pickRepository.findByLecture(lecture).isEmpty());
        // menteeReview
        assertTrue(menteeReviewRepository.findByLecture(lecture).isEmpty());
        // mentorReview
        assertFalse(mentorReviewRepository.findByParent(menteeReview).isPresent());
    }

    // TODO - @PreAuthorize -> 500에러
    @DisplayName("Mentor 탈퇴 - 멘토가 아닌 경우")
    @Test
    void quitMentor_notMentor() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(delete(BASE_URL)
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isInternalServerError());
                //.andExpect(status().isUnauthorized());
    }

    @Test
    void getCareers() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/careers", mentor.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..job").exists())
                .andExpect(jsonPath("$..companyName").exists())
                .andExpect(jsonPath("$..others").exists())
                .andExpect(jsonPath("$..license").exists());
    }

    @Test
    void getEducations() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/educations", mentor.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..educationLevel").exists())
                .andExpect(jsonPath("$..schoolName").exists())
                .andExpect(jsonPath("$..major").exists())
                .andExpect(jsonPath("$..others").exists());
    }

    @Test
    void getEachLectures() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/lectures", mentor.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.content[0].title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.content[0].subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.content[0].introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.content[0].content").value(lecture.getContent()))
                .andExpect(jsonPath("$.content[0].difficulty").value(lecture.getDifficulty().name()))

                .andExpect(jsonPath("$.content[0].systems").exists())
                // lecturePrice
                .andExpect(jsonPath("$.content[0].lecturePrice").exists())
                .andExpect(jsonPath("$.content[0].lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.content[0].lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$.content[0].lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$.content[0].lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$.content[0].lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$.content[0].lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$.content[0].lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.content[0].lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$.content[0].lecturePrice.closed").value(lecturePrice.isClosed()))
                .andExpect(jsonPath("$.content[0].lecturePrice.isGroup").value(lecturePrice.isGroup()))
                // lectureSubjects
                .andExpect(jsonPath("$.content[0].lectureSubjects").exists())
                .andExpect(jsonPath("$.content[0].thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.content[0].approved").value(lecture.isApproved()))
                // lectureMentor
                .andExpect(jsonPath("$.content[0].lectureMentor").exists())
                .andExpect(jsonPath("$.content[0].lectureMentor.mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.content[0].lectureMentor.nickname").value(mentor.getUser().getNickname()))
                .andExpect(jsonPath("$.content[0].lectureMentor.image").value(mentor.getUser().getImage()))
                .andExpect(jsonPath("$.content[0].lectureMentor.lectureCount").value(1L))
                .andExpect(jsonPath("$.content[0].lectureMentor.reviewCount").value(1L))

                .andExpect(jsonPath("$.content[0].reviewCount").value(1L))
                .andExpect(jsonPath("$.content[0].scoreAverage").value(4.0))
                .andExpect(jsonPath("$.content[0].enrollmentCount").value(1L))
                .andExpect(jsonPath("$.content[0].picked").doesNotExist())
                .andExpect(jsonPath("$.content[0].pickCount").value(1L));
    }

    @Test
    void getEachLecture() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/lectures/{lecture_id}/lecturePrices/{lecture_price_id}", mentor.getId(), lecture.getId(), lecturePrice.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.difficulty").value(lecture.getDifficulty().name()))

                .andExpect(jsonPath("$.systems").exists())
                // lecturePrice
                .andExpect(jsonPath("$.lecturePrice").exists())
                .andExpect(jsonPath("$.lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.lecturePrice.isGroup").value(lecturePrice.isGroup()))
                .andExpect(jsonPath("$.lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$.lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$.lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$.lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$.lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$.lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.lecturePrice.content").value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$.lecturePrice.closed").value(lecturePrice.isClosed()))
                // lectureSubjects
                .andExpect(jsonPath("$.lectureSubjects").exists())
                .andExpect(jsonPath("$.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.approved").value(lecture.isApproved()))
                // lectureMentor
                .andExpect(jsonPath("$.lectureMentor").exists())
                .andExpect(jsonPath("$.lectureMentor.mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.lectureMentor.nickname").value(mentor.getUser().getNickname()))
                .andExpect(jsonPath("$.lectureMentor.image").value(mentor.getUser().getImage()))
                .andExpect(jsonPath("$.lectureMentor.lectureCount").doesNotExist())
                .andExpect(jsonPath("$.lectureMentor.reviewCount").doesNotExist())

                .andExpect(jsonPath("$.reviewCount").doesNotExist())
                .andExpect(jsonPath("$.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.enrollmentCount").doesNotExist())
                .andExpect(jsonPath("$.picked").doesNotExist())
                .andExpect(jsonPath("$.pickCount").doesNotExist());
    }
    
    @DisplayName("후기 조회")
    @Test
    void getReviews() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/reviews", mentor.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scoreAverage").value(menteeReview.getScore()))
                .andExpect(jsonPath("$.reviewCount").value(1L))
                // reviews
                .andExpect(jsonPath("$.reviews.content[0].menteeReviewId").value(menteeReview.getId()))
                .andExpect(jsonPath("$.reviews.content[0].enrollmentId").value(enrollment.getId()))
                .andExpect(jsonPath("$.reviews.content[0].score").value(menteeReview.getScore()))
                .andExpect(jsonPath("$.reviews.content[0].content").value(menteeReview.getContent()))
                .andExpect(jsonPath("$.reviews.content[0].username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.reviews.content[0].userNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.reviews.content[0].userImage").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.reviews.content[0].createdAt").exists())
                // child
                .andExpect(jsonPath("$.reviews.content[0].child").doesNotExist())
//                .andExpect(jsonPath("$.reviews.content[0].child.mentorReviewId").value(mentorReview.getId()))
//                .andExpect(jsonPath("$.reviews.content[0].child.content").value(mentorReview.getContent()))
//                .andExpect(jsonPath("$.reviews.content[0].child.username").value(mentorUser.getUsername()))
//                .andExpect(jsonPath("$.reviews.content[0].child.userNickname").value(mentorUser.getNickname()))
//                .andExpect(jsonPath("$.reviews.content[0].child.userImage").value(mentorUser.getImage()))
//                .andExpect(jsonPath("$.reviews.content[0].child.createdAt").exists())
                // lecture
                .andExpect(jsonPath("$.reviews.content[0].lecture").exists())
                .andExpect(jsonPath("$.reviews.content[0].lecture.lectureId").value(lecture.getId()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.title").value(lecture.getTitle()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.subTitle").value(lecture.getSubTitle()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.introduce").value(lecture.getIntroduce()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.content").value(lecture.getContent()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.difficulty").value(lecture.getDifficulty().name()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.systems").exists())
                // lecturePrice
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice").exists())
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice.lecturePriceId").value(lecturePrice.getId()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice.isGroup").value(lecturePrice.isGroup()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice.numberOfMembers").value(lecturePrice.getNumberOfMembers()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice.pricePerHour").value(lecturePrice.getPricePerHour()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice.timePerLecture").value(lecturePrice.getTimePerLecture()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice.numberOfLectures").value(lecturePrice.getNumberOfLectures()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice.totalPrice").value(lecturePrice.getTotalPrice()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice.isGroupStr").value(lecturePrice.isGroup() ? "그룹강의" : "1:1 개인강의"))
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice.content")
                        .value(String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", lecturePrice.getPricePerHour(), lecturePrice.getTimePerLecture(), lecturePrice.getNumberOfLectures())))
                .andExpect(jsonPath("$.reviews.content[0].lecture.lecturePrice.closed").value(lecturePrice.isClosed()))

                .andExpect(jsonPath("$.reviews.content[0].lecture.lectureSubjects").exists())
                .andExpect(jsonPath("$.reviews.content[0].lecture.thumbnail").value(lecture.getThumbnail()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.approved").value(lecture.isApproved()))
                .andExpect(jsonPath("$.reviews.content[0].lecture.mentorNickname").value(mentorUser.getNickname()))

                .andExpect(jsonPath("$.reviews.content[0].lecture.scoreAverage").doesNotExist())
                .andExpect(jsonPath("$.reviews.content[0].lecture.pickCount").doesNotExist());
    }

}