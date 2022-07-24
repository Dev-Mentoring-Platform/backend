package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.modules.account.service.MentorLectureService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.lecture.service.LectureService;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.controller.request.MentorReviewCreateRequest;
import com.project.mentoridge.modules.review.controller.request.MentorReviewUpdateRequest;
import com.project.mentoridge.modules.review.controller.response.ReviewResponse;
import com.project.mentoridge.modules.review.service.MenteeReviewService;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.base.AbstractIntegrationTest.mentorReviewCreateRequest;
import static com.project.mentoridge.modules.base.AbstractIntegrationTest.mentorReviewUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MentorLectureController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class MentorLectureControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/mentors/my-lectures";

    @MockBean
    MentorLectureService mentorLectureService;
    @MockBean
    LectureService lectureService;
    @MockBean
    MentorReviewService mentorReviewService;
    @MockBean
    MenteeReviewService menteeReviewService;


    @DisplayName("멘토가 등록한 강의 리스트")
    @Test
    void get_paged_lectures_of_mentor() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorLectureService).getLectureResponses(user, 1);
    }

    @Test
    void get_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}", 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(lectureService).getLectureResponse(user, 1L);
    }

    @Test
    void get_paged_reviews_of_lecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews", 1L)
                        .param("page", "3")
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeReviewService).getReviewResponsesOfLecture(1L, 3);
    }

    @Test
    void get_review_of_lecture_when_child_isNull() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(menteeReviewService).getReviewResponseOfLecture(1L, 1L);
    }

    @Test
    void get_review_of_lecture_when_child_is_not_null() throws Exception {

        // given
        MenteeReview parent = mock(MenteeReview.class);
        Mentee mentee = mock(Mentee.class);
        when(mentee.getUser()).thenReturn(mock(User.class));
        when(parent.getMentee()).thenReturn(mentee);
        when(parent.getEnrollment()).thenReturn(mock(Enrollment.class));

        MentorReview child = mock(MentorReview.class);
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mock(User.class));
        when(child.getMentor()).thenReturn(mentor);

        ReviewResponse response = new ReviewResponse(parent, child);
        doReturn(response)
                .when(menteeReviewService).getReviewResponseOfLecture(1L, 1L);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menteeReviewId").hasJsonPath())
                .andExpect(jsonPath("$.enrollmentId").hasJsonPath())
                .andExpect(jsonPath("$.score").hasJsonPath())
                .andExpect(jsonPath("$.content").hasJsonPath())
                .andExpect(jsonPath("$.username").hasJsonPath())
                .andExpect(jsonPath("$.userNickname").hasJsonPath())
                .andExpect(jsonPath("$.createdAt").hasJsonPath())
                .andExpect(jsonPath("$.child").hasJsonPath())
                .andExpect(jsonPath("$.child.mentorReviewId").hasJsonPath())
                .andExpect(jsonPath("$.child.content").hasJsonPath())
                .andExpect(jsonPath("$.child.username").hasJsonPath())
                .andExpect(jsonPath("$.child.userNickname").hasJsonPath())
                .andExpect(jsonPath("$.child.createdAt").hasJsonPath());
    }

    @DisplayName("멘토 리뷰 작성")
    @Test
    void new_review() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(post(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}", 1L, 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(mentorReviewCreateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(mentorReviewService).createMentorReview(eq(user), eq(1L), eq(1L), any(MentorReviewCreateRequest.class));
    }

    @Test
    void edit_review() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}/children/{mentor_review_id}", 1L, 1L, 2L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                        .content(objectMapper.writeValueAsString(mentorReviewUpdateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorReviewService).updateMentorReview(eq(user), eq(1L), eq(1L), eq(2L), any(MentorReviewUpdateRequest.class));
    }

    @Test
    void delete_review() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{lecture_id}/reviews/{mentee_review_id}/children/{mentor_review_id}", 1L, 1L, 2L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorReviewService).deleteMentorReview(user, 1L, 1L, 2L);
    }

    @Test
    void get_paged_mentees_of_lecture() throws Exception {

        // given
//        User user = getUserWithName("user");
//        PrincipalDetails principal = new PrincipalDetails(user);
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
//
//        Mentee mentee = mock(Mentee.class);
//        when(mentee.getUser()).thenReturn(mock(User.class));
//        MenteeResponse menteeResponse = new MenteeResponse(mentee);
//        Page<MenteeResponse> mentees =
//                new PageImpl<>(Arrays.asList(menteeResponse), Pageable.ofSize(20), 1);
//        doReturn(mentees)
//                .when(mentorLectureService).getMenteeResponsesOfLecture(user, 1L, 1);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/mentees", 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorLectureService).getMenteeResponsesOfLecture(user, 1L, 1);
    }

    @Test
    void get_paged_enrollments_of_lecture() throws Exception {

        // given
//        User user = getUserWithName("user");
//        PrincipalDetails principal = new PrincipalDetails(user);
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
//
//        Enrollment enrollment = mock(Enrollment.class);
//        Mentee mentee = mock(Mentee.class);
//        when(mentee.getUser()).thenReturn(mock(User.class));
//        when(enrollment.getMentee()).thenReturn(mentee);
//        when(enrollment.getLecture()).thenReturn(mock(Lecture.class));
//        EnrollmentResponse enrollmentResponse = new EnrollmentResponse(enrollment);
//        Page<EnrollmentResponse> enrollments = new PageImpl<>(Arrays.asList(enrollmentResponse), Pageable.ofSize(20), 1);
//        doReturn(enrollments)
//                .when(mentorLectureService).getEnrollmentResponsesOfLecture(user, 1L, 1);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{lecture_id}/enrollments", 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorLectureService).getEnrollmentResponsesOfLecture(user, 1L, 1);
    }

    @Test
    void close() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}/close", 1L, 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(lectureService).close(user, 1L, 1L);
    }

    @Test
    void open() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{lecture_id}/lecturePrices/{lecture_price_id}/open", 1L, 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(lectureService).open(user, 1L, 1L);
    }
}