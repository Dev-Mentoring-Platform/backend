package com.project.mentoridge.modules.review.controller;

import com.project.mentoridge.modules.account.controller.CareerController;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.menteeReviewUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MenteeReviewController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class MenteeReviewControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/mentees/my-reviews";

    @MockBean
    MenteeReviewService menteeReviewService;

    @Test
    void get_paged_reviews() throws Exception {

        // given
//        Page<ReviewWithSimpleEachLectureResponse> reviews = Page.empty();
//        doReturn(reviews)
//                .when(menteeReviewService).getReviewWithSimpleEachLectureResponses(any(User.class), anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeReviewService).getReviewWithSimpleEachLectureResponses(any(User.class), eq(1));
    }

    @Test
    void get_review() throws Exception {

        // given
/*
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
        doReturn(review).when(menteeReviewService).getReviewWithSimpleEachLectureResponse(anyLong());*/
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_review_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeReviewService).getReviewWithSimpleEachLectureResponse(eq(1L));
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

    @Test
    void edit_review() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{mentee_review_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menteeReviewUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeReviewService).updateMenteeReview(any(User.class), eq(1L), eq(menteeReviewUpdateRequest));
    }

    @Test
    void delete_review() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{mentee_review_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeReviewService).deleteMenteeReview(any(User.class), eq(1L));
    }
}