package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.modules.account.controller.response.SimpleMenteeResponse;
import com.project.mentoridge.modules.account.service.MentorMenteeService;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MentorMenteeController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class MentorMenteeControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/mentors/my-mentees";

    @MockBean
    MentorMenteeService mentorMenteeService;
    @MockBean
    MenteeReviewService menteeReviewService;


    @Test
    void get_my_mentees() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL, 1)
                        .param("closed", "true")
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorMenteeService).getSimpleMenteeResponses(user, true, true);
    }

    @Test
    void get_my_mentees_and_get_response() throws Exception {

        // given
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
        mockMvc.perform(get(BASE_URL, 1)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..menteeId").exists())
                .andExpect(jsonPath("$..userId").exists())
                .andExpect(jsonPath("$..name").exists())
                .andExpect(content().json(objectMapper.writeValueAsString(mentees)));
    }

    @Test
    void get_paged_enrollmentInfo_of_my_mentees() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}", 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                .param("page", "2"))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorMenteeService).getMenteeLectureResponses(user, 1L, 2);
    }

    @Test
    void get_enrollmentInfo_of_my_mentee() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}/lectures/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L, 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorMenteeService).getMenteeLectureResponse(user, 1L, 1L);
    }

    @Test
    void get_review_written_by_my_mentee() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentee_id}/enrollments/{enrollment_id}/reviews/{mentee_review_id}", 1L, 1L, 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeReviewService).getReviewResponseOfEnrollment(1L, 1L, 1L);
    }

    @Test
    void get_my_unchecked_mentees() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/unchecked")
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorMenteeService).getSimpleMenteeResponses(user, false, false);
    }
}