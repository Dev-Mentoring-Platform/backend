package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.service.MentorLectureService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.review.service.MentorReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.List;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.base.AbstractIntegrationTest.mentorSignUpRequest;
import static com.project.mentoridge.modules.base.AbstractIntegrationTest.mentorUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MentorController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class MentorControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/mentors";

    @MockBean
    MentorService mentorService;
    @MockBean
    MentorLectureService mentorLectureService;
    @MockBean
    MentorReviewService mentorReviewService;


    @Test
    void get_mentors() throws Exception {

        // given
        // when
        mockMvc.perform(get(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(mentorService).getMentorResponses(1);
    }

    @Test
    void get_my_info() throws Exception {

        // given
//        User user = getUserWithName("user");
//        PrincipalDetails principal = new PrincipalDetails(user);
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/my-info")
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorService).getMentorResponse(user);
    }

    @Test
    void get_mentor() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}", 3L))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorService).getMentorResponse(3L);
    }

    @Test
    void new_mentor() throws Exception {

        // given
        // when
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mentorSignUpRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        // then
        verify(mentorService).createMentor(any(User.class), eq(mentorSignUpRequest));
    }

    @Test
    void edit_mentor() throws Exception {

        // given
        // when
        mockMvc.perform(put(BASE_URL + "/my-info")
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mentorUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(mentorService).updateMentor(any(User.class), eq(mentorUpdateRequest));
    }

    @Test
    void quit_mentor() throws Exception {

        // given
        // when
        mockMvc.perform(delete(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        // then
        verify(mentorService).deleteMentor(any(User.class));
    }

    @Test
    void getCareers() throws Exception {

        // given

        Career career1 = Career.builder()
                .mentor(mock(Mentor.class))
                .job("job1")
                .companyName("company1")
                .license("license1")
                .others("others1")
                .build();
        Career career2 = Career.builder()
                .mentor(mock(Mentor.class))
                .job("job2")
                .companyName("company2")
                .license("license2")
                .others("others2")
                .build();
        List<CareerResponse> careers = Arrays.asList(new CareerResponse(career1), new CareerResponse(career2));
        doReturn(careers).when(mentorService).getCareerResponses(1L);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/careers", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..job").exists())
                .andExpect(jsonPath("$..companyName").exists())
                .andExpect(jsonPath("$..others").exists())
                .andExpect(jsonPath("$..license").exists());
        // verify(mentorService).getCareerResponses(1L);
    }

    @Test
    void getEducations() throws Exception {

        // given

        Education education = Education.builder()
                .mentor(mock(Mentor.class))
                .educationLevel(EducationLevelType.UNIVERSITY)
                .schoolName("school")
                .major("major")
                .others(null)
                .build();
        List<EducationResponse> educations = Arrays.asList(new EducationResponse(education));
        doReturn(educations).when(mentorService).getEducationResponses(1L);

        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/educations", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..educationLevel").exists())
                .andExpect(jsonPath("$..schoolName").exists())
                .andExpect(jsonPath("$..major").exists())
                .andExpect(jsonPath("$..others").exists());
        // verify(mentorService).getEducationResponses(1L);
    }

    @Test
    void get_eachLectures() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/lectures", 1L))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorLectureService).getEachLectureResponses(1L, 1);
    }

    @Test
    void get_eachLecture() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/lectures/{lecture_id}/lecturePrices/{lecture_price_id}", 1L, 1L, 1L))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorLectureService).getEachLectureResponse(1L, 1L, 1L);
    }

    @Test
    void get_reviews() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/reviews", 1L, 2))
                .andDo(print())
                .andExpect(status().isOk());
        verify(mentorReviewService).getReviewWithSimpleEachLectureResponsesOfMentorByMentees(1L, 1);
    }
}