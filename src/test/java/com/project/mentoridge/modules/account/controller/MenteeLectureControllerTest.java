package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.controller.request.CancellationCreateRequest;
import com.project.mentoridge.modules.purchase.service.CancellationServiceImpl;
import com.project.mentoridge.modules.purchase.service.EnrollmentServiceImpl;
import com.project.mentoridge.modules.purchase.vo.Cancellation;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.service.ReviewService;
import com.project.mentoridge.modules.review.vo.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static com.project.mentoridge.configuration.AbstractTest.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MenteeLectureControllerTest {

    private final static String BASE_URL = "/api/mentees/my-lectures";

    @InjectMocks
    MenteeLectureController menteeLectureController;
    @Mock
    LectureService lectureService;
    @Mock
    CancellationServiceImpl cancellationService;
    @Mock
    ReviewService reviewService;
    @Mock
    EnrollmentServiceImpl enrollmentService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(menteeLectureController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @DisplayName("(= 강의 개별 조회)")
    @Test
    void getLecture() throws Exception {

        // given
        Mentor mentor = Mentor.builder()
                .user(mock(User.class))
                .build();
        Lecture lecture = Lecture.builder()
                .mentor(mentor)
                .title("title")
                .subTitle("subTitle")
                .introduce("introduce")
                .content("content")
                .difficulty(DifficultyType.ADVANCED)
                .systems(Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE))
                .thumbnail("thumbnail")
                .build();
        LectureResponse lectureResponse = new LectureResponse(lecture);
        when(lectureService.getLectureResponse(any(User.class), anyLong())).thenReturn(lectureResponse);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(lectureResponse)));
    }

    @Test
    void cancel() throws Exception {

        // given
        doReturn(mock(Cancellation.class))
                .when(cancellationService).cancel(any(User.class), anyLong(), any(CancellationCreateRequest.class));

        // when
        // then
        mockMvc.perform(post(BASE_URL + "/{lecture_id}/cancellations", 1L)
                .content(objectMapper.writeValueAsString(cancellationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getReviewOfLecture() throws Exception {

        // given
        Review parent = Review.of(5, "content", mock(User.class), mock(Enrollment.class), mock(Lecture.class), null);
        Review child = Review.of(
                null, "content_", mock(User.class), null, mock(Lecture.class), parent
        );
        ReviewResponse response = new ReviewResponse(parent, child);
        doReturn(response)
                .when(reviewService).getReviewResponseOfLecture(anyLong(), anyLong());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews/{review_id}", 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void newReview() throws Exception {

        // given
        doReturn(mock(Review.class))
                .when(reviewService).createMenteeReview(any(User.class), anyLong(), any(MenteeReviewCreateRequest.class));

        // when
        // then
        mockMvc.perform(post(BASE_URL + "/{lecture_id}/reviews", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menteeReviewCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void editReview() throws Exception {

        // given
        doNothing()
                .when(reviewService).updateMenteeReview(any(User.class), anyLong(), anyLong(), any(MenteeReviewUpdateRequest.class));

        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{lecture_id}/reviews/{review_id}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menteeReviewUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteReview() throws Exception {

        // given
        doNothing()
                .when(reviewService).deleteMenteeReview(any(User.class), anyLong(), anyLong());
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{lecture_id}/reviews/{review_id}", 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void close() throws Exception {

        // given
        doNothing()
                .when(enrollmentService).close(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{lecture_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}