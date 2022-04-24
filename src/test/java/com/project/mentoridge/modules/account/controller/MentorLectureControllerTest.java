package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.controller.response.MenteeResponse;
import com.project.mentoridge.modules.account.service.MentorLectureService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentResponse;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static com.project.mentoridge.configuration.AbstractTest.mentorReviewCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.mentorReviewUpdateRequest;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MentorLectureControllerTest {

    private final static String BASE_URL = "/api/mentors/my-lectures";

    @InjectMocks
    MentorLectureController mentorLectureController;
    @Mock
    MentorLectureService mentorLectureService;
    @Mock
    LectureService lectureService;
    @Mock
    MentorReviewService mentorReviewService;
    @Mock
    MenteeReviewService menteeReviewService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorLectureController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getLectures() throws Exception {

        // given
        Lecture lecture = mock(Lecture.class);
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mock(User.class));
        when(lecture.getMentor()).thenReturn(mentor);
        LectureResponse lectureResponse = new LectureResponse(lecture);
        Page<LectureResponse> lectures = new PageImpl<>(Arrays.asList(lectureResponse), Pageable.ofSize(20), 1);
        doReturn(lectures)
                .when(mentorLectureService).getLectureResponses(any(User.class), anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL, 1))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$..id").hasJsonPath())
//                .andExpect(jsonPath("$..title").hasJsonPath())
//                .andExpect(jsonPath("$..subTitle").hasJsonPath())
//                .andExpect(jsonPath("$..introduce").hasJsonPath())
//                .andExpect(jsonPath("$..content").hasJsonPath())
//                .andExpect(jsonPath("$..difficulty").hasJsonPath())
//                .andExpect(jsonPath("$..systems").hasJsonPath())
//                .andExpect(jsonPath("$..systems..type").hasJsonPath())
//                .andExpect(jsonPath("$..systems..name").hasJsonPath())
//                .andExpect(jsonPath("$..lecturePrices").hasJsonPath())
//                .andExpect(jsonPath("$..lecturePrices..lecturePriceId").hasJsonPath())
//                .andExpect(jsonPath("$..lecturePrices..isGroup").hasJsonPath())
//                .andExpect(jsonPath("$..lecturePrices..numberOfMembers").hasJsonPath())
//                .andExpect(jsonPath("$..lecturePrices..pricePerHour").hasJsonPath())
//                .andExpect(jsonPath("$..lecturePrices..timePerLecture").hasJsonPath())
//                .andExpect(jsonPath("$..lecturePrices..numberOfLectures").hasJsonPath())
//                .andExpect(jsonPath("$..lecturePrices..totalPrice").hasJsonPath())
//                .andExpect(jsonPath("$..lecturePrices..isGroupStr").hasJsonPath())
//                .andExpect(jsonPath("$..lecturePrices..content").hasJsonPath())
//                .andExpect(jsonPath("$..lectureSubjects").hasJsonPath())
//                .andExpect(jsonPath("$..lectureSubjects..learningKind").hasJsonPath())
//                .andExpect(jsonPath("$..lectureSubjects..krSubject").hasJsonPath())
//                .andExpect(jsonPath("$..thumbnail").hasJsonPath())
//                .andExpect(jsonPath("$..approved").hasJsonPath())
//                .andExpect(jsonPath("$..closed").hasJsonPath())
//                .andExpect(jsonPath("$..reviewCount").hasJsonPath())
//                .andExpect(jsonPath("$..scoreAverage").hasJsonPath())
//                .andExpect(jsonPath("$..lectureMentor").hasJsonPath())
//                .andExpect(jsonPath("$..lectureMentor.mentorId").hasJsonPath())
//                .andExpect(jsonPath("$..lectureMentor.lectureCount").hasJsonPath())
//                .andExpect(jsonPath("$..lectureMentor.reviewCount").hasJsonPath())
//                .andExpect(jsonPath("$..lectureMentor.nickname").hasJsonPath())
//                .andExpect(jsonPath("$..lectureMentor.image").hasJsonPath())
//                .andExpect(jsonPath("$..picked").hasJsonPath());
                .andExpect(content().json(objectMapper.writeValueAsString(lectures)));
    }

    @Test
    void getLecture() throws Exception {

        // given
        Lecture lecture = mock(Lecture.class);
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mock(User.class));
        when(lecture.getMentor()).thenReturn(mentor);
        LectureResponse response = new LectureResponse(lecture);
        doReturn(response)
                .when(lectureService).getLectureResponse(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").hasJsonPath())
//                .andExpect(jsonPath("$.title").hasJsonPath())
//                .andExpect(jsonPath("$.subTitle").hasJsonPath())
//                .andExpect(jsonPath("$.introduce").hasJsonPath())
//                .andExpect(jsonPath("$.content").hasJsonPath())
//                .andExpect(jsonPath("$.difficulty").hasJsonPath())
//                .andExpect(jsonPath("$.systems").hasJsonPath())
//                .andExpect(jsonPath("$.systems..type").hasJsonPath())
//                .andExpect(jsonPath("$.systems..name").hasJsonPath())
//                .andExpect(jsonPath("$.lecturePrices").hasJsonPath())
//                .andExpect(jsonPath("$.lecturePrices..lecturePriceId").hasJsonPath())
//                .andExpect(jsonPath("$.lecturePrices..isGroup").hasJsonPath())
//                .andExpect(jsonPath("$.lecturePrices..numberOfMembers").hasJsonPath())
//                .andExpect(jsonPath("$.lecturePrices..pricePerHour").hasJsonPath())
//                .andExpect(jsonPath("$.lecturePrices..timePerLecture").hasJsonPath())
//                .andExpect(jsonPath("$.lecturePrices..numberOfLectures").hasJsonPath())
//                .andExpect(jsonPath("$.lecturePrices..totalPrice").hasJsonPath())
//                .andExpect(jsonPath("$.lecturePrices..isGroupStr").hasJsonPath())
//                .andExpect(jsonPath("$.lecturePrices..content").hasJsonPath())
//                .andExpect(jsonPath("$.lectureSubjects").hasJsonPath())
//                .andExpect(jsonPath("$.lectureSubjects..learningKind").hasJsonPath())
//                .andExpect(jsonPath("$.lectureSubjects..krSubject").hasJsonPath())
//                .andExpect(jsonPath("$.thumbnail").hasJsonPath())
//                .andExpect(jsonPath("$.approved").hasJsonPath())
//                .andExpect(jsonPath("$.closed").hasJsonPath())
//                .andExpect(jsonPath("$.reviewCount").hasJsonPath())
//                .andExpect(jsonPath("$.scoreAverage").hasJsonPath())
//                .andExpect(jsonPath("$.lectureMentor").hasJsonPath())
//                .andExpect(jsonPath("$.lectureMentor.mentorId").hasJsonPath())
//                .andExpect(jsonPath("$.lectureMentor.lectureCount").hasJsonPath())
//                .andExpect(jsonPath("$.lectureMentor.reviewCount").hasJsonPath())
//                .andExpect(jsonPath("$.lectureMentor.nickname").hasJsonPath())
//                .andExpect(jsonPath("$.lectureMentor.image").hasJsonPath())
//                .andExpect(jsonPath("$.picked").hasJsonPath());
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getReviewsOfLecture() throws Exception {

        // given
        MenteeReview parent1 = mock(MenteeReview.class);
        Mentee mentee1 = mock(Mentee.class);
        when(mentee1.getUser()).thenReturn(mock(User.class));
        when(parent1.getMentee()).thenReturn(mentee1);
        when(parent1.getEnrollment()).thenReturn(mock(Enrollment.class));
        ReviewResponse reviewResponse1 = new ReviewResponse(parent1, null);

        MenteeReview parent2 = mock(MenteeReview.class);
        Mentee mentee2 = mock(Mentee.class);
        when(mentee2.getUser()).thenReturn(mock(User.class));
        when(parent2.getMentee()).thenReturn(mentee2);
        when(parent2.getEnrollment()).thenReturn(mock(Enrollment.class));

        MentorReview child = mock(MentorReview.class);
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mock(User.class));
        when(child.getMentor()).thenReturn(mentor);
        ReviewResponse reviewResponse2 = new ReviewResponse(parent2, child);

        Page<ReviewResponse> reviews =
                new PageImpl<>(Arrays.asList(reviewResponse1, reviewResponse2), Pageable.ofSize(20), 2);
        doReturn(reviews)
                .when(menteeReviewService).getReviewResponsesOfLecture(1L, 1);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews", 1L, 1))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$..reviewId").hasJsonPath())
//                .andExpect(jsonPath("$..score").hasJsonPath())
//                .andExpect(jsonPath("$..content").hasJsonPath())
//                .andExpect(jsonPath("$..username").hasJsonPath())
//                .andExpect(jsonPath("$..userNickname").hasJsonPath())
//                .andExpect(jsonPath("$..createdAt").hasJsonPath())
//                .andExpect(jsonPath("$..child").hasJsonPath())
//                .andExpect(jsonPath("$..lecture").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..id").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..title").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..subTitle").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..introduce").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..difficulty").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..systems").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..systems..type").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..systems..name").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lecturePrices").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lecturePrices..lecturePriceId").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lecturePrices..isGroup").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lecturePrices..numberOfMembers").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lecturePrices..pricePerHour").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lecturePrices..timePerLecture").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lecturePrices..numberOfLectures").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lecturePrices..totalPrice").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lecturePrices..isGroupStr").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lecturePrices..content").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lectureSubjects").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lectureSubjects..learningKind").hasJsonPath())
//                .andExpect(jsonPath("$..lecture..lectureSubjects..krSubject").hasJsonPath())
//                .andExpect(jsonPath("$..thumbnail").hasJsonPath());
                .andExpect(content().json(objectMapper.writeValueAsString(reviews)));
    }

    @Test
    void getReviewOfLecture_when_child_isNull() throws Exception {

        // given
        MenteeReview parent = mock(MenteeReview.class);
        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(mock(User.class));
        when(parent.getMentee()).thenReturn(mentee);
        when(parent.getEnrollment()).thenReturn(mock(Enrollment.class));
        ReviewResponse response = new ReviewResponse(parent, null);
        doReturn(response)
                .when(menteeReviewService).getReviewResponseOfLecture(1L, 1L);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menteeReviewId").hasJsonPath())
                .andExpect(jsonPath("$.enrollmentId").hasJsonPath())
                .andExpect(jsonPath("$.score").hasJsonPath())
                .andExpect(jsonPath("$.content").hasJsonPath())
                .andExpect(jsonPath("$.username").hasJsonPath())
                .andExpect(jsonPath("$.userNickname").hasJsonPath())
                .andExpect(jsonPath("$.createdAt").hasJsonPath())
                .andExpect(jsonPath("$.child").hasJsonPath())
//                .andExpect(jsonPath("$.child.mentorReviewId").hasJsonPath())
//                .andExpect(jsonPath("$.child.content").hasJsonPath())
//                .andExpect(jsonPath("$.child.username").hasJsonPath())
//                .andExpect(jsonPath("$.child.userNickname").hasJsonPath())
//                .andExpect(jsonPath("$.child.createdAt").hasJsonPath())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void getReviewOfLecture() throws Exception {

        // given
        MenteeReview parent = mock(MenteeReview.class);
        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(mock(User.class));
        when(parent.getMentee()).thenReturn(mentee);
        when(parent.getEnrollment()).thenReturn(mock(Enrollment.class));

        MentorReview child = mock(MentorReview.class);
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mock(User.class));
        when(child.getMentor()).thenReturn(mentor);

        ReviewResponse response = new ReviewResponse(parent, child);
        doReturn(response)
                .when(menteeReviewService).getReviewResponseOfLecture(1L, 1L);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menteeReviewId").hasJsonPath())
                .andExpect(jsonPath("$.enrollmentId").hasJsonPath())
                .andExpect(jsonPath("$.score").hasJsonPath())
                .andExpect(jsonPath("$.content").hasJsonPath())
                .andExpect(jsonPath("$.username").hasJsonPath())
                .andExpect(jsonPath("$.userNickname").hasJsonPath())
                .andExpect(jsonPath("$.createdAt").hasJsonPath())
                .andExpect(jsonPath("$.child").hasJsonPath())
                .andExpect(jsonPath("$.child.mentorReviewId").hasJsonPath())
                .andExpect(jsonPath("$.child.content").hasJsonPath())
                .andExpect(jsonPath("$.child.username").hasJsonPath())
                .andExpect(jsonPath("$.child.userNickname").hasJsonPath())
                .andExpect(jsonPath("$.child.createdAt").hasJsonPath())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void newReview() throws Exception {

        // given
        MentorReview review = mock(MentorReview.class);
        doReturn(review)
                .when(mentorReviewService).createMentorReview(any(User.class), anyLong(), anyLong(), any(MentorReviewCreateRequest.class));
        // when
        // then
        mockMvc.perform(post(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L)
                .content(objectMapper.writeValueAsString(mentorReviewCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void editReview() throws Exception {

        // given
        doNothing()
                .when(mentorReviewService).updateMentorReview(any(User.class), anyLong(), anyLong(), anyLong(), any(MentorReviewUpdateRequest.class));
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}/children/{mentor_review_id}", 1L, 1L, 2L)
                .content(objectMapper.writeValueAsString(mentorReviewUpdateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteReview() throws Exception {

        // given
        doNothing()
                .when(mentorReviewService).deleteMentorReview(any(User.class), anyLong(), anyLong(), anyLong());
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}/children/{mentor_review_id}", 1L, 1L, 2L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getMenteesOfLecture() throws Exception {

        // given
        User user = getUserWithName("user");
        PrincipalDetails principal = new PrincipalDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(mock(User.class));
        MenteeResponse menteeResponse = new MenteeResponse(mentee);
        Page<MenteeResponse> mentees =
                new PageImpl<>(Arrays.asList(menteeResponse), Pageable.ofSize(20), 1);
        doReturn(mentees)
                .when(mentorLectureService).getMenteeResponsesOfLecture(user, 1L, 1);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/mentees", 1L, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..user").exists())
                .andExpect(jsonPath("$..user").hasJsonPath())
                .andExpect(jsonPath("$..user.userId").exists())
                .andExpect(jsonPath("$..user.username").exists())
                .andExpect(jsonPath("$..user.role").exists())
                .andExpect(jsonPath("$..user.name").exists())
                .andExpect(jsonPath("$..user.gender").exists())
                .andExpect(jsonPath("$..user.birthYear").exists())
                .andExpect(jsonPath("$..user.phoneNumber").exists())
                .andExpect(jsonPath("$..user.nickname").exists())
                .andExpect(jsonPath("$..user.image").exists())
                .andExpect(jsonPath("$..user.zone").exists())
                .andExpect(jsonPath("$..subjects").exists())
                .andExpect(content().json(objectMapper.writeValueAsString(mentees)));
    }

    @Test
    void getEnrollmentsOfLecture() throws Exception {

        // given
        User user = getUserWithName("user");
        PrincipalDetails principal = new PrincipalDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        Enrollment enrollment = mock(Enrollment.class);
        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(mock(User.class));
        when(enrollment.getMentee()).thenReturn(mentee);
        when(enrollment.getLecture()).thenReturn(mock(Lecture.class));
        EnrollmentResponse enrollmentResponse = new EnrollmentResponse(enrollment);
        Page<EnrollmentResponse> enrollments = new PageImpl<>(Arrays.asList(enrollmentResponse), Pageable.ofSize(20), 1);
        doReturn(enrollments)
                .when(mentorLectureService).getEnrollmentResponsesOfLecture(user, 1L, 1);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/enrollments", 1L, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..mentee").exists())
                .andExpect(jsonPath("$..lectureTitle").exists())
                .andExpect(content().json(objectMapper.writeValueAsString(enrollments)));
    }
}