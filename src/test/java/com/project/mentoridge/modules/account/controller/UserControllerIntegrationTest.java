package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.controller.request.UserImageUpdateRequest;
import com.project.mentoridge.modules.account.controller.request.UserPasswordUpdateRequest;
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
import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.modules.chat.enums.MessageType;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import com.project.mentoridge.modules.inquiry.controller.request.InquiryCreateRequest;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import com.project.mentoridge.modules.inquiry.repository.InquiryRepository;
import com.project.mentoridge.modules.inquiry.service.InquiryService;
import com.project.mentoridge.modules.inquiry.vo.Inquiry;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.lecture.repository.LecturePriceRepository;
import com.project.mentoridge.modules.lecture.repository.LectureRepository;
import com.project.mentoridge.modules.lecture.repository.LectureSubjectRepository;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.log.component.EnrollmentLogService;
import com.project.mentoridge.modules.log.component.LectureLogService;
import com.project.mentoridge.modules.notification.repository.NotificationRepository;
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
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
class UserControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final String BASE_URL = "/api/users";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

//    @Autowired
//    EntityManager em;
    @Autowired
    TransactionTemplate transactionTemplate;
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
    LectureRepository lectureRepository;
    @Autowired
    LectureLogService lectureLogService;
    @Autowired
    LecturePriceRepository lecturePriceRepository;
    @Autowired
    LectureSubjectRepository lectureSubjectRepository;

    @Autowired
    ChatroomRepository chatroomRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    EnrollmentService enrollmentService;
    @Autowired
    EnrollmentLogService enrollmentLogService;
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
    @Autowired
    NotificationRepository notificationRepository;

    private User menteeUser;
    private Mentee mentee;
    private String menteeAccessTokenWithPrefix;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessTokenWithPrefix;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

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
        menteeAccessTokenWithPrefix = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessTokenWithPrefix = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);
    }

    @Test
    void get_paged_users() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].userId").value(menteeUser.getId()))
                .andExpect(jsonPath("$.content[0].username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.content[0].role").value(menteeUser.getRole().name()))
                .andExpect(jsonPath("$.content[0].name").value(menteeUser.getName()))
                .andExpect(jsonPath("$.content[0].gender").value(menteeUser.getGender().name()))
                .andExpect(jsonPath("$.content[0].birthYear").value(menteeUser.getBirthYear()))
                .andExpect(jsonPath("$.content[0].phoneNumber").value(menteeUser.getPhoneNumber()))
                .andExpect(jsonPath("$.content[0].nickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.content[0].image").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.content[0].zone").value(AddressUtils.convertEmbeddableToStringAddress(menteeUser.getZone())))

                .andExpect(jsonPath("$.content[1].userId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.content[1].username").value(mentorUser.getUsername()))
                .andExpect(jsonPath("$.content[1].role").value(mentorUser.getRole().name()))
                .andExpect(jsonPath("$.content[1].name").value(mentorUser.getName()))
                .andExpect(jsonPath("$.content[1].gender").value(mentorUser.getGender().name()))
                .andExpect(jsonPath("$.content[1].birthYear").value(mentorUser.getBirthYear()))
                .andExpect(jsonPath("$.content[1].phoneNumber").value(mentorUser.getPhoneNumber()))
                .andExpect(jsonPath("$.content[1].nickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.content[1].image").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.content[1].zone").value(AddressUtils.convertEmbeddableToStringAddress(mentorUser.getZone())));

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
                .andExpect(jsonPath("$.role").value(menteeUser.getRole().name()))
                .andExpect(jsonPath("$.name").value(menteeUser.getName()))
                .andExpect(jsonPath("$.gender").value(menteeUser.getGender().name()))
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
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(menteeUser.getId()))
                .andExpect(jsonPath("$.username").value(menteeUser.getUsername()))
                .andExpect(jsonPath("$.role").value(menteeUser.getRole().name()))
                .andExpect(jsonPath("$.name").value(menteeUser.getName()))
                .andExpect(jsonPath("$.gender").value(menteeUser.getGender().name()))
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
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(userUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User updated = userRepository.findById(menteeUser.getId()).orElseThrow(RuntimeException::new);
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
        mockMvc.perform(put(BASE_URL + "/my-info/image")
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(userImageUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User updated = userRepository.findById(menteeUser.getId()).orElseThrow(RuntimeException::new);
        assertAll(
                () -> assertNotNull(updated),
                () -> assertEquals(userImageUpdateRequest.getImage(), updated.getImage())
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
        mockMvc.perform(put(BASE_URL + "/my-info/image")
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(userImageUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 멘티_회원탈퇴() throws Exception {

        // Given
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
        // 신청 승인
        enrollment.check(mentorUser, enrollmentLogService);

        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        Post post1 = postService.createPost(menteeUser, PostCreateRequest.builder()
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .image("image")
                .build());
        Comment comment1 = commentService.createComment(mentorUser, post1.getId(), CommentCreateRequest.builder()
                .content("content")
                .build());
        postService.likePost(mentorUser, post1.getId());

        Post post2 = postService.createPost(mentorUser, PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build());
        Comment comment2 = commentService.createComment(menteeUser, post1.getId(), CommentCreateRequest.builder()
                .content("content")
                .build());
        postService.likePost(menteeUser, post2.getId());


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

        // When
        UserQuitRequest userQuitRequest = UserQuitRequest.builder()
                .reasonId(1)
                .password("password")
                .build();
        mockMvc.perform(delete(BASE_URL)
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix)
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

        // chatroom
        assertTrue(chatroomRepository.findByMentee(mentee).isEmpty());
        // message
        assertTrue(messageRepository.findBySender(menteeUser).isEmpty());

        // lecture - lecturePrice, lectureSubject
        assertFalse(lectureRepository.findByMentor(mentor).isEmpty());
        assertFalse(lecturePriceRepository.findByLecture(lecture).isEmpty());
        assertFalse(lectureSubjectRepository.findByLecture(lecture).isEmpty());

        // enrollment, pick
        assertTrue(enrollmentRepository.findByMentee(mentee).isEmpty());
        assertTrue(pickRepository.findByMentee(mentee).isEmpty());
        // menteeReview
        assertTrue(menteeReviewRepository.findByMentee(mentee).isEmpty());
        // mentorReview
        assertFalse(mentorReviewRepository.findByParent(menteeReview).isPresent());

        // notification
        assertTrue(notificationRepository.findByUser(menteeUser).isEmpty());
        // post
        assertTrue(postRepository.findByUser(menteeUser).isEmpty());
        // comment
        assertTrue(commentRepository.findByUser(menteeUser).isEmpty());
        // liking
        assertTrue(likingRepository.findByUser(menteeUser).isEmpty());

        // inquiry - 미삭제
        assertFalse(inquiryRepository.findByUser(menteeUser).isEmpty());
    }

    @Test
    void 멘토_회원탈퇴_진행중인_강의가_존재할때() throws Exception {

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
                .sender(mentorUser)
                .text("hello~")
                .checked(false)
                .build());
        Long pickId = savePick(pickService, menteeUser, lecture, lecturePrice);
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        // 신청 승인
        enrollment.check(mentorUser, enrollmentLogService);

        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        Post post1 = postService.createPost(menteeUser, PostCreateRequest.builder()
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .image("image")
                .build());
        Comment comment1 = commentService.createComment(mentorUser, post1.getId(), CommentCreateRequest.builder()
                .content("content")
                .build());
        postService.likePost(mentorUser, post1.getId());

        Post post2 = postService.createPost(mentorUser, PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build());
        Comment comment2 = commentService.createComment(menteeUser, post1.getId(), CommentCreateRequest.builder()
                .content("content")
                .build());
        postService.likePost(menteeUser, post2.getId());


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
        // Then
        UserQuitRequest userQuitRequest = UserQuitRequest.builder()
                .reasonId(1)
                .password("password")
                .build();
        mockMvc.perform(delete(BASE_URL)
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                .content(objectMapper.writeValueAsString(userQuitRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        // 세션
        // TODO - CHECK
        // assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        // 유저
        User _user = userRepository.findAllByUsername(mentorUser.getUsername());
        assertFalse(_user.isDeleted());
        assertNull(_user.getDeletedAt());
        assertEquals(RoleType.MENTOR, _user.getRole());

        // 멘티
        assertNotNull(menteeRepository.findByUser(_user));
        // 멘토
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
        assertFalse(chatroomRepository.findByMentor(mentor).isEmpty());
        // message
        assertFalse(messageRepository.findBySender(mentorUser).isEmpty());
        // lecture - lecturePrice, lectureSubject
        assertFalse(lectureRepository.findByMentor(mentor).isEmpty());
        assertFalse(lecturePriceRepository.findByLecture(lecture).isEmpty());
        assertFalse(lectureSubjectRepository.findByLecture(lecture).isEmpty());

        // enrollment, pick
        assertFalse(enrollmentRepository.findByLecture(lecture).isEmpty());
        assertFalse(pickRepository.findByLecture(lecture).isEmpty());
        // menteeReview
        assertFalse(menteeReviewRepository.findByLecture(lecture).isEmpty());
        // mentorReview
        assertTrue(mentorReviewRepository.findByParent(menteeReview).isPresent());

        // notification
        assertFalse(notificationRepository.findByUser(mentorUser).isEmpty());
        // post
        assertFalse(postRepository.findByUser(mentorUser).isEmpty());
        // comment
        assertFalse(commentRepository.findByUser(mentorUser).isEmpty());
        // liking
        assertFalse(likingRepository.findByUser(mentorUser).isEmpty());

        // inquiry - 미삭제
        assertFalse(inquiryRepository.findByUser(mentorUser).isEmpty());
    }

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
                .sender(mentorUser)
                .text("hello~")
                .checked(false)
                .build());
        Long pickId = savePick(pickService, menteeUser, lecture, lecturePrice);
        Enrollment enrollment = saveEnrollment(enrollmentService, menteeUser, lecture, lecturePrice);
        // 신청 승인
        enrollment.check(mentorUser, enrollmentLogService);
        // 강의 종료
        enrollment.finish(menteeUser, enrollmentLogService);
        MenteeReview menteeReview = saveMenteeReview(menteeReviewService, menteeUser, enrollment);
        MentorReview mentorReview = saveMentorReview(mentorReviewService, mentorUser, lecture, menteeReview);

        Post post1 = postService.createPost(menteeUser, PostCreateRequest.builder()
                .category(CategoryType.LECTURE_REQUEST)
                .title("title")
                .content("content")
                .image("image")
                .build());
        Comment comment1 = commentService.createComment(mentorUser, post1.getId(), CommentCreateRequest.builder()
                .content("content")
                .build());
        postService.likePost(mentorUser, post1.getId());

        Post post2 = postService.createPost(mentorUser, PostCreateRequest.builder()
                .category(CategoryType.TALK)
                .title("title")
                .content("content")
                .image("image")
                .build());
        Comment comment2 = commentService.createComment(menteeUser, post1.getId(), CommentCreateRequest.builder()
                .content("content")
                .build());
        postService.likePost(menteeUser, post2.getId());


        Inquiry inquiry1 = inquiryService.createInquiry(mentorUser, InquiryCreateRequest.builder()
                        .type(InquiryType.LECTURE)
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
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(userQuitRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        // em.flush();
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

        /*
        - Most probably such behaviour occurs when you have bidirectional relationship and you're not synchronizing both sides WHILE having both parent and child persisted (attached to the current session).
        - Note: you can avoid the select when removing an entity using a modifying query delete, example below:
        */
        // chatroom
        assertTrue(chatroomRepository.findByMentor(mentor).isEmpty());
        // message
        assertTrue(messageRepository.findBySender(mentorUser).isEmpty());
        // lecture - lecturePrice, lectureSubject
        assertTrue(lectureRepository.findByMentor(mentor).isEmpty());
        assertTrue(lecturePriceRepository.findByLecture(lecture).isEmpty());
        assertTrue(lectureSubjectRepository.findByLecture(lecture).isEmpty());

        // enrollment, pick
        assertTrue(enrollmentRepository.findByLecture(lecture).isEmpty());
        assertTrue(pickRepository.findByLecture(lecture).isEmpty());
        // menteeReview
        assertTrue(menteeReviewRepository.findByLecture(lecture).isEmpty());
        // mentorReview
        assertFalse(mentorReviewRepository.findByParent(menteeReview).isPresent());

        // notification
        assertTrue(notificationRepository.findByUser(mentorUser).isEmpty());
        // post
        assertTrue(postRepository.findByUser(mentorUser).isEmpty());
        // comment
        assertTrue(commentRepository.findByUser(mentorUser).isEmpty());
        // liking
        assertTrue(likingRepository.findByUser(mentorUser).isEmpty());

        // inquiry - 미삭제
        assertFalse(inquiryRepository.findByUser(mentorUser).isEmpty());
    }

    // TODO - CHECK
    @Test
    void get_quit_reasons() throws Exception {

        // given
        // when
        // then
        String response = mockMvc.perform(get(BASE_URL + "/quit-reasons"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
    }

    @Test
    void change_user_password() throws Exception {

        // Given
        String password = menteeUser.getPassword();
        // When
        UserPasswordUpdateRequest userPasswordUpdateRequest = UserPasswordUpdateRequest.builder()
                .password("password")
                .newPassword("new_password")
                .newPasswordConfirm("new_password")
                .build();
        mockMvc.perform(put(BASE_URL + "/my-password")
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(userPasswordUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        User updated = userRepository.findById(menteeUser.getId()).orElseThrow(RuntimeException::new);
        assertThat(updated.getPassword()).isNotEqualTo(password);
    }

    @Test
    void change_user_password_with_invalid_input() throws Exception {

        // Given
        // When
        // Then
        UserPasswordUpdateRequest userPasswordUpdateRequest = UserPasswordUpdateRequest.builder()
                .password("password")
                .newPassword("new_password")
                .newPasswordConfirm("not_equals")
                .build();
        mockMvc.perform(put(BASE_URL + "/my-password")
                .header(AUTHORIZATION, menteeAccessTokenWithPrefix)
                .content(objectMapper.writeValueAsString(userPasswordUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}