package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.enums.DifficultyType;
import com.project.mentoridge.modules.lecture.enums.SystemType;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import com.project.mentoridge.modules.purchase.service.EnrollmentServiceImpl;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
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

import static com.project.mentoridge.config.init.TestDataBuilder.getSubjectWithSubjectIdAndKrSubject;
import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static com.project.mentoridge.configuration.AbstractTest.menteeReviewCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.menteeReviewUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MenteeLectureControllerTest {

    private final static String BASE_URL = "/api/mentees/my-lectures";

    @InjectMocks
    MenteeLectureController menteeLectureController;
    @Mock
    LectureService lectureService;
    @Mock
    MenteeReviewService menteeReviewService;
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
        User user = getUserWithName("user");
        Mentor mentor = Mentor.builder()
                .user(user)
                .build();

        LecturePrice lecturePrice = LecturePrice.builder()
                .lecture(null)
                .isGroup(true)
                .numberOfMembers(10)
                .pricePerHour(10000L)
                .timePerLecture(3)
                .numberOfLectures(5)
                .build();
        LectureSubject lectureSubject = LectureSubject.builder()
                .lecture(null)
                .subject(getSubjectWithSubjectIdAndKrSubject(1L, "백엔드"))
                .build();
        Lecture lecture = Lecture.builder()
                .mentor(mentor)
                .title("title")
                .subTitle("subTitle")
                .introduce("introduce")
                .content("content")
                .difficulty(DifficultyType.ADVANCED)
                .systems(Arrays.asList(SystemType.ONLINE, SystemType.OFFLINE))
                .lecturePrices(Arrays.asList(lecturePrice))
                .lectureSubjects(Arrays.asList(lectureSubject))
                .thumbnail("thumbnail")
                .build();
        LectureResponse lectureResponse = new LectureResponse(lecture);
        when(lectureService.getLectureResponse(user, 1L)).thenReturn(lectureResponse);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(content().json(objectMapper.writeValueAsString(lectureResponse)));
                .andExpect(jsonPath("$..id").exists())
                .andExpect(jsonPath("$..title").exists())
                .andExpect(jsonPath("$..subTitle").exists())
                .andExpect(jsonPath("$..introduce").exists())
                .andExpect(jsonPath("$..content").exists())
                .andExpect(jsonPath("$..difficulty").exists())
                .andExpect(jsonPath("$..systems").exists())
                .andExpect(jsonPath("$..systems..type").exists())
                .andExpect(jsonPath("$..systems..name").exists())
                .andExpect(jsonPath("$..lecturePrices").exists())
                .andExpect(jsonPath("$..lecturePrices..lecturePriceId").exists())
                .andExpect(jsonPath("$..lecturePrices..isGroup").exists())
                .andExpect(jsonPath("$..lecturePrices..numberOfMembers").exists())
                .andExpect(jsonPath("$..lecturePrices..pricePerHour").exists())
                .andExpect(jsonPath("$..lecturePrices..timePerLecture").exists())
                .andExpect(jsonPath("$..lecturePrices..numberOfLectures").exists())
                .andExpect(jsonPath("$..lecturePrices..totalPrice").exists())
                .andExpect(jsonPath("$..lecturePrices..isGroupStr").exists())
                .andExpect(jsonPath("$..lecturePrices..content").exists())
                .andExpect(jsonPath("$..lectureSubjects").exists())
                .andExpect(jsonPath("$..lectureSubjects..learningKind").exists())
                .andExpect(jsonPath("$..lectureSubjects..krSubject").exists())
                .andExpect(jsonPath("$..thumbnail").exists())
                .andExpect(jsonPath("$..approved").exists())
                .andExpect(jsonPath("$..closed").exists())
                .andExpect(jsonPath("$..reviewCount").exists())
                .andExpect(jsonPath("$..scoreAverage").exists())
                .andExpect(jsonPath("$..lectureMentor").exists())
                .andExpect(jsonPath("$..lectureMentor.mentorId").exists())
                .andExpect(jsonPath("$..lectureMentor.lectureCount").exists())
                .andExpect(jsonPath("$..lectureMentor.reviewCount").exists())
                .andExpect(jsonPath("$..lectureMentor.nickname").exists())
                .andExpect(jsonPath("$..lectureMentor.image").exists())
                .andExpect(jsonPath("$..picked").exists());
    }
/*
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
    }*/

    @Test
    void getReviewOfLecture() throws Exception {

        // given
        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(mock(User.class));
        MenteeReview parent = MenteeReview.builder()
                .score(5)
                .content("content")
                .mentee(mentee)
                .lecture(mock(Lecture.class))
                .enrollment(mock(Enrollment.class))
                .build();
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mock(User.class));
        MentorReview child = MentorReview.builder()
                .content("content_")
                .mentor(mentor)
                .lecture(mock(Lecture.class))
                .parent(parent)
                .build();
        ReviewResponse response = new ReviewResponse(parent, child);
        doReturn(response)
                .when(menteeReviewService).getReviewResponseOfLecture(anyLong(), anyLong());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    void newReview() throws Exception {

        // given
        doReturn(mock(MenteeReview.class))
                .when(menteeReviewService).createMenteeReview(any(User.class), anyLong(), any(MenteeReviewCreateRequest.class));

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
                .when(menteeReviewService).updateMenteeReview(any(User.class), anyLong(), anyLong(), any(MenteeReviewUpdateRequest.class));

        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menteeReviewUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteReview() throws Exception {

        // given
        doNothing()
                .when(menteeReviewService).deleteMenteeReview(any(User.class), anyLong(), anyLong());
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
/*
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
    }*/
}