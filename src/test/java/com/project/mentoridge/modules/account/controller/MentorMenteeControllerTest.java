package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.controller.response.MenteeEnrollmentInfoResponse;
import com.project.mentoridge.modules.account.controller.response.SimpleMenteeResponse;
import com.project.mentoridge.modules.account.service.MentorMenteeService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MentorMenteeControllerTest {

    private final static String BASE_URL = "/api/mentors/my-mentees";

    @InjectMocks
    MentorMenteeController mentorMenteeController;
    @Mock
    MentorMenteeService mentorMenteeService;
    @Mock
    MenteeReviewService menteeReviewService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorMenteeController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getMyMentees() throws Exception {

        // given
        User user = getUserWithName("user");
        PrincipalDetails principal = new PrincipalDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        // menteeId, userId, name, nickname
        SimpleMenteeResponse response = SimpleMenteeResponse.builder()
                .menteeId(1L)
                .userId(1L)
                .name("user")
                .nickname("user")
                .build();
        List<SimpleMenteeResponse> mentees = Arrays.asList(response);
        doReturn(mentees)
                .when(mentorMenteeService).getSimpleMenteeResponses(user, false, true);
        // when
        // then
        mockMvc.perform(get(BASE_URL, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..menteeId").exists())
                .andExpect(jsonPath("$..userId").exists())
                .andExpect(jsonPath("$..name").exists())
                .andExpect(content().json(objectMapper.writeValueAsString(mentees)));
    }

    // call real-method
    @Test
    void getMyMenteeEnrollmentInfos() throws Exception {

        // given
        MenteeEnrollmentInfoResponse menteeEnrollmentInfoResponse = MenteeEnrollmentInfoResponse.builder()
                .menteeId(1L)
                .lecture(mock(Lecture.class))
                .lecturePrice(mock(LecturePrice.class))
                .reviewId(1L)
                .chatroomId(1L)
                .build();
        Page<MenteeEnrollmentInfoResponse> menteeEnrollmentInfos =
                new PageImpl<>(Arrays.asList(menteeEnrollmentInfoResponse), Pageable.ofSize(20), 2);
        doReturn(menteeEnrollmentInfos)
                .when(mentorMenteeService).getMenteeLectureResponses(any(User.class), anyLong(), anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}", 1)
                .param("closed", "false")
                .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$..menteeId").exists())
//                .andExpect(jsonPath("$..lecture").exists())
//                .andExpect(jsonPath("$..lecture.lectureId").exists())
//                .andExpect(jsonPath("$..lecture.thumbnail").exists())
//                .andExpect(jsonPath("$..lecture.title").exists())
//                .andExpect(jsonPath("$..lecture.subTitle").exists())
//                .andExpect(jsonPath("$..lecture.introduce").exists())
//                .andExpect(jsonPath("$..lecture.content").exists())
//                .andExpect(jsonPath("$..lecture.lecturePrice").exists())
//                .andExpect(jsonPath("$..lecture.lecturePrice.lecturePriceId").exists())
//                .andExpect(jsonPath("$..lecture.lecturePrice.isGroup").exists())
//                .andExpect(jsonPath("$..lecture.lecturePrice.numberOfMembers").exists())
//                .andExpect(jsonPath("$..lecture.lecturePrice.pricePerHour").exists())
//                .andExpect(jsonPath("$..lecture.lecturePrice.timePerLecture").exists())
//                .andExpect(jsonPath("$..lecture.lecturePrice.numberOfLectures").exists())
//                .andExpect(jsonPath("$..lecture.lecturePrice.totalPrice").exists())
//                .andExpect(jsonPath("$..lecture.lecturePrice.isGroupStr").exists())
//                .andExpect(jsonPath("$..lecture.lecturePrice.content").exists())
//                .andExpect(jsonPath("$..lecture.systemTypes").exists())
//                .andExpect(jsonPath("$..lecture.systemTypes..type").exists())
//                .andExpect(jsonPath("$..lecture.systemTypes..name").exists())
//                .andExpect(jsonPath("$..reviewId").exists())
//                .andExpect(jsonPath("$..chatroomId").exists())
                .andExpect(content().json(objectMapper.writeValueAsString(menteeEnrollmentInfos)));
    }

    @Test
    void getReviewsOfMyMentee() throws Exception {

        // given
        MenteeReview review = mock(MenteeReview.class);
        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(mock(User.class));
        when(review.getMentee()).thenReturn(mentee);
        when(review.getEnrollment()).thenReturn(mock(Enrollment.class));
        ReviewResponse response = new ReviewResponse(review, null);
        doReturn(response)
                .when(menteeReviewService).getReviewResponseOfLecture(1L, 1L);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}/lectures/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}