package com.project.mentoridge.modules.subject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.subject.controller.response.SubjectResponse;
import com.project.mentoridge.modules.subject.service.SubjectService;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getSubjectWithSubjectIdAndKrSubject;
import static com.project.mentoridge.modules.lecture.enums.LearningKindType.IT;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SubjectControllerTest {

    @InjectMocks
    SubjectController subjectController;
    @Mock
    SubjectService subjectService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(subjectController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getSubjects() throws Exception {

        // given
        Subject subject1 = getSubjectWithSubjectIdAndKrSubject(1L, "백엔드");
        Subject subject2 = getSubjectWithSubjectIdAndKrSubject(2L, "프론트엔드");

        SubjectResponse response1 = new SubjectResponse(subject1);
        SubjectResponse response2 = new SubjectResponse(subject2);
        List<SubjectResponse> subjects = Arrays.asList(response1, response2);
        doReturn(subjects)
                .when(subjectService).getSubjectResponses();
        // when
        // then
        mockMvc.perform(get("/api/subjects"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(subjects)));
    }

    @Test
    void _getSubjects() throws Exception {

        // given
        Subject subject1 = getSubjectWithSubjectIdAndKrSubject(1L, "백엔드");
        Subject subject2 = getSubjectWithSubjectIdAndKrSubject(2L, "프론트엔드");

        SubjectResponse response1 = new SubjectResponse(subject1);
        List<SubjectResponse> subjects = Arrays.asList(response1);
        doReturn(subjects)
                .when(subjectService).getSubjectResponses(IT);
        // when
        // then
        mockMvc.perform(get("/api/subjects/{learning_kind_id}", "IT"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(subjects)));
    }
}