package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.init.TestDataBuilder;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.controller.response.MenteeLectureResponse;
import com.project.mentoridge.modules.account.controller.response.MenteeSimpleResponse;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.MentorMenteeService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.service.ReviewService;
import com.project.mentoridge.modules.review.vo.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MentorMenteeControllerTest {

    private final static String BASE_URL = "/api/mentors/my-mentees";

    @InjectMocks
    MentorMenteeController mentorMenteeController;
    @Mock
    MentorMenteeService mentorMenteeService;
    @Mock
    ReviewService reviewService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
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

        // menteeId, userId, name
        MenteeSimpleResponse menteeSimpleResponse = MenteeSimpleResponse.builder()
                .menteeId(1L)
                .userId(1L)
                .name("user")
                .build();
        Page<MenteeSimpleResponse> mentees = new PageImpl<>(Arrays.asList(menteeSimpleResponse), Pageable.ofSize(20), 1);
        doReturn(mentees)
                .when(mentorMenteeService).getMenteeSimpleResponses(user, false, 1);
        // when
        // then
        mockMvc.perform(get(BASE_URL, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mentees)));
    }

    // call real-method
    @Test
    void getMyMentee() throws Exception {

        // given
        MenteeLectureResponse menteeLectureResponse = MenteeLectureResponse.builder()
                .menteeId(1L)
                .lecture(Mockito.mock(Lecture.class))
                .lecturePrice(Mockito.mock(LecturePrice.class))
                .reviewId(1L)
                .chatroomId(1L)
                .build();
        Page<MenteeLectureResponse> menteeLectures =
                new PageImpl<>(Arrays.asList(menteeLectureResponse), Pageable.ofSize(20), 2);
        doReturn(menteeLectures)
                .when(mentorMenteeService).getMenteeLectureResponses(any(User.class), anyBoolean(), anyLong(), anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}", 1)
                .param("closed", "false")
                .param("page", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(menteeLectures)));
    }

    @Test
    void getReviewsOfMyMentee() throws Exception {

        // given
        Review review = Mockito.mock(Review.class);
        when(review.getUser()).thenReturn(Mockito.mock(User.class));
        ReviewResponse response = new ReviewResponse(review, null);
        doReturn(response)
                .when(reviewService).getReviewResponseOfLecture(1L, 1L);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}/lectures/{lecture_id}/reviews/{review_id}", 1L, 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}