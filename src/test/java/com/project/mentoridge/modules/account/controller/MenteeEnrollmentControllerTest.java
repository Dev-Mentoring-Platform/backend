package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.purchase.controller.response.EnrollmentWithSimpleEachLectureResponse;
import com.project.mentoridge.modules.purchase.service.EnrollmentServiceImpl;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import static com.project.mentoridge.configuration.AbstractTest.menteeReviewCreateRequest;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MenteeEnrollmentControllerTest {

    private final static String BASE_URL = "/api/mentees/my-enrollments";

    @InjectMocks
    MenteeEnrollmentController menteeEnrollmentController;
    @Mock
    MenteeReviewService menteeReviewService;
    @Mock
    EnrollmentServiceImpl enrollmentService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(menteeEnrollmentController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getUnreviewedLecturesOfMentee() throws Exception {

        // given
        User user = getUserWithName("user");
        PrincipalDetails principal = new PrincipalDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(user);

        Lecture lecture = mock(Lecture.class);
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mock(User.class));
        when(lecture.getMentor()).thenReturn(mentor);
        Enrollment enrollment = Enrollment.builder()
                .mentee(mentee)
                .lecture(lecture)
                .lecturePrice(mock(LecturePrice.class))
                .build();
        Page<EnrollmentWithSimpleEachLectureResponse> lectures =
                new PageImpl<>(Arrays.asList(new EnrollmentWithSimpleEachLectureResponse(enrollment)), Pageable.ofSize(20), 1);
        doReturn(lectures)
                .when(enrollmentService).getEnrollmentWithSimpleEachLectureResponses(user, false, 1);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/unreviewed"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..mentee").hasJsonPath())
                .andExpect(jsonPath("$..lectureTitle").hasJsonPath())
                .andExpect(jsonPath("$..createdAt").hasJsonPath())
                .andExpect(jsonPath("$..lecture").hasJsonPath())
                .andExpect(jsonPath("$..lecture.id").hasJsonPath())
                .andExpect(jsonPath("$..lecture.title").hasJsonPath())
                .andExpect(jsonPath("$..lecture.subTitle").hasJsonPath())
                .andExpect(jsonPath("$..lecture.introduce").hasJsonPath())
                .andExpect(jsonPath("$..lecture.difficulty").hasJsonPath())
                .andExpect(jsonPath("$..lecture.systems").hasJsonPath())
                .andExpect(jsonPath("$..lecture.lecturePrice").hasJsonPath())
                .andExpect(jsonPath("$..lecture.lectureSubjects").hasJsonPath())
                .andExpect(jsonPath("$..lecture.thumbnail").hasJsonPath())
                .andExpect(jsonPath("$..lecture.mentorNickname").hasJsonPath());
                //.andExpect(content().json(objectMapper.writeValueAsString(lectures)));

    }

    @DisplayName("리뷰 작성")
    @Test
    void newReview() throws Exception {

        // given
        doReturn(mock(MenteeReview.class))
                .when(menteeReviewService).createMenteeReview(any(User.class), anyLong(), any(MenteeReviewCreateRequest.class));

        // when
        // then
        mockMvc.perform(post(BASE_URL + "/{enrollment_id}/reviews", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(menteeReviewCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}