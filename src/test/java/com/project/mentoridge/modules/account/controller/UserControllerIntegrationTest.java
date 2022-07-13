package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.controller.request.UserImageUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserQuitRequest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.*;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.util.AddressUtils;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.board.controller.request.CommentCreateRequest;
import com.project.mentoridge.modules.board.controller.request.PostCreateRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.repository.CommentRepository;
import com.project.mentoridge.modules.board.repository.LikingRepository;
import com.project.mentoridge.modules.board.repository.PostRepository;
import com.project.mentoridge.modules.board.service.CommentService;
import com.project.mentoridge.modules.board.service.PostService;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Liking;
import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.chat.enums.MessageType;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import com.project.mentoridge.modules.inquiry.repository.InquiryRepository;
import com.project.mentoridge.modules.inquiry.service.InquiryService;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.log.component.LecturePriceLogService;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.lectureCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.userUpdateRequest;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMentorReview;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class UserControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final String BASE_URL = "/api/users";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

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
    CareerRepository careerRepository;
    @Autowired
    EducationRepository educationRepository;

    @Autowired
    LectureService lectureService;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    LecturePriceLogService lecturePriceLogService;
    @Autowired
    LecturePriceRepository lecturePriceRepository;

    @Autowired
    ChatService chatService;
    @Autowired
    ChatroomRepository chatroomRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentRepository enrollmentRepository;
    @Autowired
    PickService pickService;
    @Autowired
    PickRepository pickRepository;
    @Autowired
    MenteeReviewService menteeReviewService;
    @Autowired
    MenteeReviewRepository menteeReviewRepository;
    @Autowired
    MentorReviewService mentorReviewService;
    @Autowired
    MentorReviewRepository mentorReviewRepository;
    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    InquiryService inquiryService;
    @Autowired
    InquiryRepository inquiryRepository;

    @Autowired
    PostService postService;
    @Autowired
    PostRepository postRepository;
    @Autowired
    CommentService commentService;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    LikingRepository likingRepository;


    private User menteeUser;
    private Mentee mentee;
    private String menteeAccessToken;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessToken;

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

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessToken = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);
    }

    @Test
    void get_paged_users() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].userId").value(menteeUser.getId()))
                .andExpect(jsonPath("$.[0].username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.[0].role").value(menteeUser.getRole()))
                .andExpect(jsonPath("$.[0].name").value(menteeUser.getName()))
                .andExpect(jsonPath("$.[0].gender").value(menteeUser.getGender()))
                .andExpect(jsonPath("$.[0].birthYear").value(menteeUser.getBirthYear()))
                .andExpect(jsonPath("$.[0].phoneNumber").value(menteeUser.getPhoneNumber()))
                .andExpect(jsonPath("$.[0].nickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.[0].image").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.[0].zone").value(AddressUtils.convertEmbeddableToStringAddress(menteeUser.getZone())))

                .andExpect(jsonPath("$.[1].userId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.[1].username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.[1].role").value(mentorUser.getRole()))
                .andExpect(jsonPath("$.[1].name").value(mentorUser.getName()))
                .andExpect(jsonPath("$.[1].gender").value(mentorUser.getGender()))
                .andExpect(jsonPath("$.[1].birthYear").value(mentorUser.getBirthYear()))
                .andExpect(jsonPath("$.[1].phoneNumber").value(mentorUser.getPhoneNumber()))
                .andExpect(jsonPath("$.[1].nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.[1].image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.[1].zone").value(AddressUtils.convertEmbeddableToStringAddress(mentorUser.getZone())));

    }

    @Test
    void get_user() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{user_id}", menteeUser.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(menteeUser.getId()))
                .andExpect(jsonPath("$.username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.role").value(menteeUser.getRole()))
                .andExpect(jsonPath("$.name").value(menteeUser.getName()))
                .andExpect(jsonPath("$.gender").value(menteeUser.getGender()))
                .andExpect(jsonPath("$.birthYear").value(menteeUser.getBirthYear()))
                .andExpect(jsonPath("$.phoneNumber").value(menteeUser.getPhoneNumber()))
                .andExpect(jsonPath("$.nickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.image").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.zone").value(AddressUtils.convertEmbeddableToStringAddress(menteeUser.getZone())));
    }

    @Test
    void get_my_info() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/my-info")
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(menteeUser.getId()))
                .andExpect(jsonPath("$.username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.role").value(menteeUser.getRole()))
                .andExpect(jsonPath("$.name").value(menteeUser.getName()))
                .andExpect(jsonPath("$.gender").value(menteeUser.getGender()))
                .andExpect(jsonPath("$.birthYear").value(menteeUser.getBirthYear()))
                .andExpect(jsonPath("$.phoneNumber").value(menteeUser.getPhoneNumber()))
                .andExpect(jsonPath("$.nickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.image").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.zone").value(AddressUtils.convertEmbeddableToStringAddress(menteeUser.getZone())));
    }

    @Test
    void 회원정보_수정() throws Exception {

        // Given
        // When
        mockMvc.perform(put(BASE_URL + "/my-info")
                        .header(AUTHORIZATION, menteeAccessToken)
                        .content(objectMapper.writeValueAsString(userUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User updated = userRepository.findById(menteeUser.getId()).orElse(null);
        assert updated != null;
        assertAll(
                () -> assertNotNull(updated),
                () -> assertEquals(userUpdateRequest.getGender(), updated.getGender()),
                () -> assertEquals(userUpdateRequest.getBirthYear(), updated.getBirthYear()),
                () -> assertEquals(userUpdateRequest.getPhoneNumber(), updated.getPhoneNumber()),
                () -> assertEquals(userUpdateRequest.getZone(), updated.getZone().toString()),
                () -> assertEquals(userUpdateRequest.getImage(), updated.getImage())
        );
    }

    @Test
    void update_image() throws Exception {

        // Given
        // When
        UserImageUpdateRequest userImageUpdateRequest = UserImageUpdateRequest.builder()
                .image("updated_image")
                .build();
        mockMvc.perform(put(BASE_URL + "/my-info/info")
                        .header(AUTHORIZATION, menteeAccessToken)
                        .content(objectMapper.writeValueAsString(userImageUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User updated = userRepository.findById(menteeUser.getId()).orElse(null);
        assert updated != null;
        assertAll(
                () -> assertNotNull(updated),
                () -> assertEquals(userUpdateRequest.getImage(), updated.getImage())
        );
    }

    @Test
    void update_image_with_no_image() throws Exception {

        // Given
        // When
        // Then
        UserImageUpdateRequest userImageUpdateRequest = UserImageUpdateRequest.builder()
                .image(null)
                .build();
        mockMvc.perform(put(BASE_URL + "/my-info/info")
                        .header(AUTHORIZATION, menteeAccessToken)
                        .content(objectMapper.writeValueAsString(userImageUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 멘티_회원탈퇴() throws Exception {

        // Given
        // When
        UserQuitRequest userQuitRequest = UserQuitRequest.builder()
                .reasonId(1)
                .password("password")
                .build();
        mockMvc.perform(delete(BASE_URL)
                        .header(AUTHORIZATION, menteeAccessToken)
                        .content(objectMapper.writeValueAsString(userQuitRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        // 세션
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 유저
        User deletedUser = userRepository.findAllByUsername(menteeUser.getUsername());
        assertTrue(deletedUser.isDeleted());
        assertNotNull(deletedUser.getDeletedAt());
        assertEquals(RoleType.MENTEE, deletedUser.getRole());

        // 멘티
        assertNull(menteeRepository.findByUser(deletedUser));
    }

    // TODO - 회원 삭제 시 연관 엔티티 전체 삭제
    @Test
    void 멘토_회원탈퇴() throws Exception {

        // Given
        Lecture lecture = lectureService.createLecture(mentorUser, lectureCreateRequest);
        LecturePrice lecturePrice = lecturePriceRepository.findByLecture(lecture).get(0);
        lecture.approve(lectureLogService);

        Chatroom chatroom = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .build());
        Message message = messageRepository.save(Message.builder()
                .type(MessageType.MESSAGE)
                .chatroom(chatroom)
                .sender(menteeUser)
                .text("hello~")
                .checked(false)
                .build());
        Long pickId = savePick(pickService, menteeUser, lecture, lecturePrice);
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);

        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        PostCreateRequest postCreateRequest = PostCreateRequest.builder()
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .image("image")
                .build();
        Post post = postService.createPost(menteeUser, postCreateRequest);
        CommentCreateRequest commentCreateRequest = CommentCreateRequest.builder()
                .content("content")
                .build();
        Comment comment = commentService.createComment(mentorUser, post.getId(), commentCreateRequest);
        postService.likePost(mentorUser, post.getId());

        Inquiry inquiry1 = inquiryService.createInquiry(mentorUser, InquiryCreateRequest.builder()
                        .type(InquiryType.LECTURE)
                        .title("title")
                        .content("content")
                .build());
        Inquiry inquiry2 = inquiryService.createInquiry(menteeUser, InquiryCreateRequest.builder()
                        .type(InquiryType.MENTOR)
                        .title("title")
                        .content("content")
                .build());
        List<Long> careerIds = careerRepository.findByMentor(mentor).stream()
                .map(BaseEntity::getId).collect(Collectors.toList());
        List<Long> educationIds = educationRepository.findByMentor(mentor).stream()
                .map(BaseEntity::getId).collect(Collectors.toList());

        // When
        UserQuitRequest userQuitRequest = UserQuitRequest.builder()
                .reasonId(1)
                .password("password")
                .build();
        mockMvc.perform(delete(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessToken)
                        .content(objectMapper.writeValueAsString(userQuitRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        // 세션
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        // 유저
        User deletedUser = userRepository.findAllByUsername(mentorUser.getUsername());
        assertTrue(deletedUser.isDeleted());
        assertNotNull(deletedUser.getDeletedAt());
        assertEquals(RoleType.MENTEE, deletedUser.getRole());

        // 멘티
        assertNull(menteeRepository.findByUser(deletedUser));
        // 멘토
        assertNull(mentorRepository.findByUser(deletedUser));
        // career
        for (Long careerId : careerIds) {
            assertFalse(careerRepository.findById(careerId).isPresent());
        }
        // education
        for (Long educationId : educationIds) {
            assertFalse(educationRepository.findById(educationId).isPresent());
        }
        // chatroom
        // message
        // lecture - lecturePrice, lectureSubject
        // enrollment, pick, review
        // notification
        // file
    }
}