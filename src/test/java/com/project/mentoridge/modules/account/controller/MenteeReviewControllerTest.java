package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleLectureResponse;
import com.project.mentoridge.modules.purchase.service.EnrollmentServiceImpl;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleLectureResponse;
import com.project.mentoridge.modules.review.service.ReviewService;
import com.project.mentoridge.modules.review.vo.Review;
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

import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class MenteeReviewControllerTest {

    private final static String BASE_URL = "/api/mentees/my-reviews";

    @InjectMocks
    MenteeReviewController menteeReviewController;
    @Mock
    ReviewService reviewService;
    @Mock
    EnrollmentServiceImpl enrollmentService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(menteeReviewController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getReviews() throws Exception {

        // given
        Page<ReviewWithSimpleLectureResponse> reviews = Page.empty();
        doReturn(reviews)
                .when(reviewService).getReviewWithSimpleLectureResponses(any(User.class), anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$..reviewId").exists())
//                .andExpect(jsonPath("$..score").exists())
//                .andExpect(jsonPath("$..content").exists())
//                .andExpect(jsonPath("$..username").exists())
//                .andExpect(jsonPath("$..userNickname").exists())
//                .andExpect(jsonPath("$..createdAt").exists())
//                .andExpect(jsonPath("$..child").exists())
//                .andExpect(jsonPath("$..lecture").exists())
//                .andExpect(jsonPath("$..lecture..id").exists())
//                .andExpect(jsonPath("$..lecture..title").exists())
//                .andExpect(jsonPath("$..lecture..subTitle").exists())
//                .andExpect(jsonPath("$..lecture..introduce").exists())
//                .andExpect(jsonPath("$..lecture..difficulty").exists())
//                .andExpect(jsonPath("$..lecture..systems").exists())
//                .andExpect(jsonPath("$..lecture..systems..type").exists())
//                .andExpect(jsonPath("$..lecture..systems..name").exists())
//                .andExpect(jsonPath("$..lecture..lecturePrices").exists())
//                .andExpect(jsonPath("$..lecture..lecturePrices..lecturePriceId").exists())
//                .andExpect(jsonPath("$..lecture..lecturePrices..isGroup").exists())
//                .andExpect(jsonPath("$..lecture..lecturePrices..numberOfMembers").exists())
//                .andExpect(jsonPath("$..lecture..lecturePrices..pricePerHour").exists())
//                .andExpect(jsonPath("$..lecture..lecturePrices..timePerLecture").exists())
//                .andExpect(jsonPath("$..lecture..lecturePrices..numberOfLectures").exists())
//                .andExpect(jsonPath("$..lecture..lecturePrices..totalPrice").exists())
//                .andExpect(jsonPath("$..lecture..lecturePrices..isGroupStr").exists())
//                .andExpect(jsonPath("$..lecture..lecturePrices..content").exists())
//                .andExpect(jsonPath("$..lecture..lectureSubjects").exists())
//                .andExpect(jsonPath("$..lecture..lectureSubjects..learningKind").exists())
//                .andExpect(jsonPath("$..lecture..lectureSubjects..krSubject").exists())
//                .andExpect(jsonPath("$..thumbnail").exists());
                .andExpect(content().json(objectMapper.writeValueAsString(reviews)));
    }

    @Test
    void getReview() throws Exception {

        // given
        Review parent = Review.builder()
                .score(5)
                .content("content")
                .user(mock(User.class))
                .lecture(mock(Lecture.class))
                .enrollment(mock(Enrollment.class))
                .parent(null)
                .build();
        ReviewResponse review = new ReviewResponse(parent, null);
        doReturn(review).when(reviewService).getReviewResponse(anyLong());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{review_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(review)));
    }

    // TODO - CHECK
    @Test
    void getUnreviewedLecturesOfMentee() throws Exception {

        // given
        User user = getUserWithName("user");
        PrincipalDetails principal = new PrincipalDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(mock(User.class));
        Enrollment enrollment = Enrollment.builder()
                .mentee(mentee)
                .lecture(mock(Lecture.class))
                .lecturePrice(mock(LecturePrice.class))
                .build();
        Page<EnrollmentWithSimpleLectureResponse> lectures =
                new PageImpl<>(Arrays.asList(new EnrollmentWithSimpleLectureResponse(enrollment)), Pageable.ofSize(20), 1);
        doReturn(lectures)
                .when(enrollmentService).getEnrollmentWithSimpleLectureResponses(user, false, 1);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/unreviewed", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(lectures)));

    }
}