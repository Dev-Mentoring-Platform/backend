package com.project.mentoridge.modules.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.service.EnrollmentServiceImpl;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.response.ReviewWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MenteeReviewControllerTest {

    private final static String BASE_URL = "/api/mentees/my-reviews";

    @InjectMocks
    MenteeReviewController menteeReviewController;
    @Mock
    MenteeReviewService menteeReviewService;
    @Mock
    EnrollmentServiceImpl enrollmentService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(menteeReviewController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getReviews() throws Exception {

        // given
        Page<ReviewWithSimpleEachLectureResponse> reviews = Page.empty();
        doReturn(reviews)
                .when(menteeReviewService).getReviewWithSimpleEachLectureResponses(any(User.class), anyInt());
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
        Mentee mentee = mock(Mentee.class);
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user@email.com");
        when(user.getNickname()).thenReturn("user");
        when(user.getImage()).thenReturn("image");
        when(mentee.getUser()).thenReturn(user);

        Lecture lecture = mock(Lecture.class);
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mock(User.class));
        when(lecture.getMentor()).thenReturn(mentor);

        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getLecturePrice()).thenReturn(mock(LecturePrice.class));
        MenteeReview parent = MenteeReview.builder()
                .score(5)
                .content("content")
                .mentee(mentee)
                .lecture(lecture)
                .enrollment(enrollment)
                .build();
        ReviewWithSimpleEachLectureResponse review = new ReviewWithSimpleEachLectureResponse(parent, null);
        doReturn(review).when(menteeReviewService).getReviewWithSimpleEachLectureResponse(anyLong());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_review_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menteeReviewId").hasJsonPath())
                .andExpect(jsonPath("$.enrollmentId").hasJsonPath())
                .andExpect(jsonPath("$.score").hasJsonPath())
                .andExpect(jsonPath("$.content").hasJsonPath())
                .andExpect(jsonPath("$.username").hasJsonPath())
                .andExpect(jsonPath("$.userNickname").hasJsonPath())
                .andExpect(jsonPath("$.userImage").hasJsonPath())
                .andExpect(jsonPath("$.createdAt").hasJsonPath())

                .andExpect(jsonPath("$.child").hasJsonPath())
                .andExpect(jsonPath("$.lecture").hasJsonPath())
                .andExpect(jsonPath("$.lecture.id").hasJsonPath())
                .andExpect(jsonPath("$.lecture.title").hasJsonPath())
                .andExpect(jsonPath("$.lecture.subTitle").hasJsonPath())
                .andExpect(jsonPath("$.lecture.introduce").hasJsonPath())
                .andExpect(jsonPath("$.lecture.difficulty").hasJsonPath())
                .andExpect(jsonPath("$.lecture.systems").hasJsonPath())
                .andExpect(jsonPath("$.lecture.lecturePrice").hasJsonPath())
                .andExpect(jsonPath("$.lecture.lectureSubjects").hasJsonPath())
                .andExpect(jsonPath("$.lecture.thumbnail").hasJsonPath())
                .andExpect(jsonPath("$.lecture.mentorNickname").hasJsonPath());
                //.andExpect(content().json(objectMapper.writeValueAsString(review)));
    }

//    @Test
//    void getReviewOfLecture() throws Exception {
//
//        // given
//        Mentee mentee = mock(Mentee.class);
//        when(mentee.getUser()).thenReturn(mock(User.class));
//        MenteeReview parent = MenteeReview.builder()
//                .score(5)
//                .content("content")
//                .mentee(mentee)
//                .lecture(mock(Lecture.class))
//                .enrollment(mock(Enrollment.class))
//                .build();
//        Mentor mentor = mock(Mentor.class);
//        when(mentor.getUser()).thenReturn(mock(User.class));
//        MentorReview child = MentorReview.builder()
//                .content("content_")
//                .mentor(mentor)
//                .lecture(mock(Lecture.class))
//                .parent(parent)
//                .build();
//        ReviewResponse response = new ReviewResponse(parent, child);
//        doReturn(response)
//                .when(menteeReviewService).getReviewResponseOfLecture(anyLong(), anyLong());
//        // when
//        // then
//        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(response)));
//    }

//    @Test
//    void editReview() throws Exception {
//
//        // given
//        doNothing()
//                .when(menteeReviewService).updateMenteeReview(any(User.class), anyLong(), anyLong(), any(MenteeReviewUpdateRequest.class));
//
//        // when
//        // then
//        mockMvc.perform(put(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(objectMapper.writeValueAsString(menteeReviewUpdateRequest)))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    void deleteReview() throws Exception {
//
//        // given
//        doNothing()
//                .when(menteeReviewService).deleteMenteeReview(any(User.class), anyLong(), anyLong());
//        // when
//        // then
//        mockMvc.perform(delete(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L))
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
}