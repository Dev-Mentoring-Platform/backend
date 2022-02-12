package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.controller.request.MentorSignUpRequest;
import com.project.mentoridge.modules.account.controller.request.MentorUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.controller.response.MentorResponse;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.service.MentorLectureService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.controller.response.LectureResponse;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MentorControllerTest {

    private final static String BASE_URL = "/api/mentors";

    @InjectMocks
    MentorController mentorController;
    @Mock
    MentorService mentorService;
    @Mock
    MentorLectureService mentorLectureService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getMentors() throws Exception {

        // given
        Mentor mentor1 = mock(Mentor.class);
        when(mentor1.getUser()).thenReturn(mock(User.class));
        Mentor mentor2 = mock(Mentor.class);
        when(mentor2.getUser()).thenReturn(mock(User.class));
        Page<MentorResponse> mentors = new PageImpl<>(Arrays.asList(new MentorResponse(mentor1), new MentorResponse(mentor2)), Pageable.ofSize(20), 2);
        doReturn(mentors).when(mentorService).getMentorResponses(1);
        // when
        // then
        mockMvc.perform(get(BASE_URL, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mentors)));
    }

    @Test
    void getMyInfo() throws Exception {

        // given
        User user = User.of(
                "user@email.com",
                "password",
                "user", null, null, null, "user@email.com",
                "user", null, null, null, RoleType.MENTEE,
                null, null
        );
        PrincipalDetails principal = new PrincipalDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(user);
        MentorResponse mentorResponse = new MentorResponse(mentor);
        doReturn(mentorResponse).when(mentorService).getMentorResponse(user);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/my-info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mentorResponse)));
    }

    @Test
    void getMentor() throws Exception {

        // given
        Mentor mentor = mock(Mentor.class);
        when(mentor.getUser()).thenReturn(mock(User.class));
        MentorResponse mentorResponse = new MentorResponse(mentor);
        doReturn(mentorResponse).when(mentorService).getMentorResponse(1L);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(mentorResponse)));
    }

    @Test
    void newMentor() throws Exception {

        // given
        doReturn(mock(Mentor.class))
                .when(mentorService).createMentor(any(User.class), any(MentorSignUpRequest.class));
        // when
        // then
        MentorSignUpRequest mentorSignUpRequest = AbstractTest.getMentorSignUpRequest();
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentorSignUpRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void editMentor() throws Exception {

        // given
        doNothing()
                .when(mentorService).updateMentor(any(User.class), any(MentorUpdateRequest.class));
        // when
        // then
        MentorUpdateRequest mentorUpdateRequest = AbstractTest.getMentorUpdateRequest();
        mockMvc.perform(put(BASE_URL + "/my-info")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mentorUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void quitMentor() throws Exception {

        // given
        doNothing()
                .when(mentorService).deleteMentor(any(User.class));
        // when
        // then
        mockMvc.perform(delete(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getCareers() throws Exception {

        // given
        Career career1 = Career.of(
                mock(Mentor.class),
                "job1",
                "company1",
                "others1",
                "license1"
        );
        Career career2 = Career.of(
                mock(Mentor.class),
                "job2",
                "company2",
                "others2",
                "license2"
        );
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
                .andExpect(jsonPath("$..license").exists())
                .andExpect(content().json(objectMapper.writeValueAsString(careers)));
    }

    // test - json path
    @Test
    void getEducations() throws Exception {

        // given
        Education education = Education.of(
                mock(Mentor.class),
                EducationLevelType.UNIVERSITY,
                "school",
                "major",
                null
        );
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
                // null 체크
                .andExpect(jsonPath("$..others").exists())
                .andExpect(content().json(objectMapper.writeValueAsString(educations)));
    }

    @Test
    void getLectures() throws Exception {

        // given
        Mentor mentor = mock(Mentor.class);
        User user = mock(User.class);
        when(mentor.getUser()).thenReturn(user);

        Lecture lecture1 = mock(Lecture.class);
        when(lecture1.getMentor()).thenReturn(mentor);
        Lecture lecture2 = mock(Lecture.class);
        when(lecture2.getMentor()).thenReturn(mentor);
        Page<LectureResponse> lectures = new PageImpl<>(Arrays.asList(new LectureResponse(lecture1), new LectureResponse(lecture2)), Pageable.ofSize(20), 2);
        doReturn(lectures)
                .when(mentorLectureService).getLectureResponses(1L, 1);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/lectures", 1L, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(lectures)));
    }
}