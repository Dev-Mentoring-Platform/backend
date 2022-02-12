package com.project.mentoridge.modules.subject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.lecture.embeddable.LearningKind;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.controller.response.LearningKindResponse;
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

import static org.mockito.ArgumentMatchers.any;
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

    private LearningKind learningKind1;
    private LearningKind learningKind2;
    private Subject subject1;
    private Subject subject2;

    @BeforeEach
    void setup() {

        mockMvc = MockMvcBuilders.standaloneSetup(subjectController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();

        learningKind1 = LearningKind.of(LearningKindType.IT);
        learningKind2 = LearningKind.of(LearningKindType.LANGUAGE);
        subject1 = Subject.of(learningKind1, "자바");
        subject2 = Subject.of(learningKind2, "중국어");
    }

    @Test
    void getLearningKinds() throws Exception {

        // given
        LearningKindResponse response1 = new LearningKindResponse(learningKind1);
        LearningKindResponse response2 = new LearningKindResponse(learningKind2);

        List<LearningKindResponse> learningKinds = Arrays.asList(response1, response2);
        doReturn(learningKinds)
                .when(subjectService).getLearningKindResponses();
        // when
        // then
        mockMvc.perform(get("/api/learningKinds"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(learningKinds)));
    }

    @Test
    void getSubjects() throws Exception {

        // given
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
        SubjectResponse response1 = new SubjectResponse(subject1);
        List<SubjectResponse> subjects = Arrays.asList(response1);
        doReturn(subjects)
                .when(subjectService).getSubjectResponses(1L);
        // when
        // then
        mockMvc.perform(get("/api/subjects/{learning_kind_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(subjects)));
    }
}