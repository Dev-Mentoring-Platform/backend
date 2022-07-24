package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.purchase.service.EnrollmentServiceImpl;
import com.project.mentoridge.modules.review.controller.request.MenteeReviewCreateRequest;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.base.AbstractIntegrationTest.menteeReviewCreateRequest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MenteeEnrollmentController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class MenteeEnrollmentControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/mentees/my-enrollments";

    @MockBean
    MenteeReviewService menteeReviewService;
    @MockBean
    EnrollmentServiceImpl enrollmentService;


    @DisplayName("신청 미승인 강의 리스트")
    @Test
    void get_paged_unchecked_enrollments() throws Exception {

        // given
        // when
        mockMvc.perform(get(BASE_URL + "/unchecked")
                        .param("page", "2")
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(enrollmentService).getEnrollmentWithEachLectureResponsesOfMentee(any(User.class), eq(false), eq(2));
    }

    @DisplayName("신청 승인완료 강의 리스트")
    @Test
    void get_paged_checked_enrollments() throws Exception {

        // given
        // when
        mockMvc.perform(get(BASE_URL + "/checked")
                        .param("page", "1")
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(enrollmentService).getEnrollmentWithEachLectureResponsesOfMentee(any(User.class), eq(true), eq(1));
    }

    @Test
    void get_enrolled_eachLecture() throws Exception {

        // given
        // when
        mockMvc.perform(get(BASE_URL + "/{enrollment_id}/lecture", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(enrollmentService).getEachLectureResponseOfEnrollment(any(User.class), eq(1L), eq(true));
    }

    @DisplayName("리뷰 미작성 수강내역 리스트")
    @Test
    void get_paged_unreviewed_enrollments() throws Exception {

        // given
/*
        User user = getUserWithName("user");
        PrincipalDetails principal = new PrincipalDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));*/

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/unreviewed")
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(enrollmentService).getEnrollmentWithSimpleEachLectureResponses(any(User.class), eq(false), eq(1));
    }

    @Test
    void get_enrollment() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{enrollment_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(enrollmentService).getEnrollmentWithSimpleEachLectureResponse(any(User.class), eq(1L));
    }

    @DisplayName("리뷰 작성")
    @Test
    void newReview() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(post(BASE_URL + "/{enrollment_id}/reviews", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menteeReviewCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(menteeReviewService).createMenteeReview(any(User.class), eq(1L), eq(menteeReviewCreateRequest));
    }

    @Test
    void newReview_with_wrong_score() throws Exception {

        // given
        // when
        MenteeReviewCreateRequest menteeReviewCreateRequest = MenteeReviewCreateRequest.builder()
                .score(6)
                .content("content")
                .build();
        mockMvc.perform(post(BASE_URL + "/{enrollment_id}/reviews", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menteeReviewCreateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        // then
        verifyNoInteractions(menteeReviewService);
    }

    @Test
    void newReview_with_no_content() throws Exception {

        // given
        // when
        MenteeReviewCreateRequest menteeReviewCreateRequest = MenteeReviewCreateRequest.builder()
                .score(3)
                .content("")
                .build();
        mockMvc.perform(post(BASE_URL + "/{enrollment_id}/reviews", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(menteeReviewCreateRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());
        // then
        verifyNoInteractions(menteeReviewService);
    }
}