package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.controller.request.MentorSignUpRequest;
import com.project.mentoridge.modules.account.controller.request.MentorUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.controller.response.MentorResponse;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
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

import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static com.project.mentoridge.configuration.AbstractTest.mentorSignUpRequest;
import static com.project.mentoridge.configuration.AbstractTest.mentorUpdateRequest;
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
//                .andExpect(jsonPath("$..mentorId").exists())
//                .andExpect(jsonPath("$..user").exists())
//                .andExpect(jsonPath("$..user.userId").exists())
//                .andExpect(jsonPath("$..user.username").exists())
//                .andExpect(jsonPath("$..user.role").exists())
//                .andExpect(jsonPath("$..user.name").exists())
//                .andExpect(jsonPath("$..user.gender").exists())
//                .andExpect(jsonPath("$..user.birthYear").exists())
//                .andExpect(jsonPath("$..user.phoneNumber").exists())
//                .andExpect(jsonPath("$..user.nickname").exists())
//                .andExpect(jsonPath("$..user.image").exists())
//                .andExpect(jsonPath("$..user.zone").exists())
//                .andExpect(jsonPath("$..bio").exists())
//                .andExpect(jsonPath("$..careers").exists())
//                .andExpect(jsonPath("$..careers..job").exists())
//                .andExpect(jsonPath("$..careers..companyName").exists())
//                .andExpect(jsonPath("$..careers..others").exists())
//                .andExpect(jsonPath("$..careers..license").exists())
//                .andExpect(jsonPath("$..educations").exists())
//                .andExpect(jsonPath("$..educations..educationLevel").exists())
//                .andExpect(jsonPath("$..educations..schoolName").exists())
//                .andExpect(jsonPath("$..educations..major").exists())
//                .andExpect(jsonPath("$..educations..others").exists());
                .andExpect(content().json(objectMapper.writeValueAsString(mentors)));
    }

    @Test
    void getMyInfo() throws Exception {

        // given
        User user = getUserWithName("user");
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
                .andExpect(content().json(objectMapper.writeValueAsString(mentorResponse)))
                // 누적 멘티 수 조회
                .andExpect(jsonPath("$.accumulatedMenteeCount").exists());
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
                .andExpect(content().json(objectMapper.writeValueAsString(mentorResponse)))
                // 누적 멘티 수 조회
                .andExpect(jsonPath("$.accumulatedMenteeCount").exists());
    }

    @Test
    void newMentor() throws Exception {

        // given
        doReturn(mock(Mentor.class))
                .when(mentorService).createMentor(any(User.class), any(MentorSignUpRequest.class));
        // when
        // then
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
                .andExpect(jsonPath("$..license").exists())
                .andExpect(content().json(objectMapper.writeValueAsString(careers)));
    }

    // test - json path
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
                .when(mentorLectureService).getLectureResponsesPerLecturePrice(1L, 1);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{mentor_id}/lectures", 1L, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(lectures)));
    }
}