package com.project.mentoridge.modules.subject.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest    // AutoConfigureMockMvc
class SubjectControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SubjectRepository subjectRepository;

    private Subject subject1;
    private Subject subject2;

    @BeforeAll
    void init() {

        subject1 = subjectRepository.save(Subject.builder()
                        .subjectId(1L)
                        .learningKind(LearningKindType.IT)
                        .krSubject("프론트엔드")
                .build());
        subject2 = subjectRepository.save(Subject.builder()
                        .subjectId(2L)
                        .learningKind(LearningKindType.IT)
                        .krSubject("백엔드")
                .build());
    }

    @Test
    void getLearningKinds() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/learningKinds"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]").value(LearningKindType.IT.getType()));
    }

    @Test
    void getSubjects() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/subjects"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].subjectId").value(1L))
                .andExpect(jsonPath("$.[0].learningKind").value(LearningKindType.IT.getType()))
                .andExpect(jsonPath("$.[0].krSubject").value("프론트엔드"))
                .andExpect(jsonPath("$.[1].subjectId").value(2L))
                .andExpect(jsonPath("$.[1].learningKind").value(LearningKindType.IT.getType()))
                .andExpect(jsonPath("$.[1].krSubject").value("백엔드"));
    }

    @Test
    void getSubjectsByLearningKind() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/learningKinds/{learning_kind}/subjects", LearningKindType.IT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].subjectId").value(1L))
                .andExpect(jsonPath("$.[0].learningKind").value(LearningKindType.IT.getType()))
                .andExpect(jsonPath("$.[0].krSubject").value("프론트엔드"))
                .andExpect(jsonPath("$.[1].subjectId").value(2L))
                .andExpect(jsonPath("$.[1].learningKind").value(LearningKindType.IT.getType()))
                .andExpect(jsonPath("$.[1].krSubject").value("백엔드"));
    }
}