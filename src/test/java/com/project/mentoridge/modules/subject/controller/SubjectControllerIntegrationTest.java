package com.project.mentoridge.modules.subject.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@MockMvcTest
class SubjectControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SubjectRepository subjectRepository;

    private Subject subject1;
    private Subject subject2;

    @BeforeEach
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
        String response = mockMvc.perform(get("/api/learningKinds"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Arrays.asList(LearningKindType.IT.getName()).toString()))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void getSubjects() throws Exception {

        // given
        // when
        // then
        String response = mockMvc.perform(get("/api/subjects"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..subjectId").exists())
                .andExpect(jsonPath("$..learningKind").exists())
                .andExpect(jsonPath("$..krSubject").exists())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void getSubjectsByLearningKind() throws Exception {

        // given
        // when
        // then
        String response = mockMvc.perform(get("/api/learningKinds/{learning_kind}/subjects", LearningKindType.IT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..subjectId").exists())
                .andExpect(jsonPath("$..learningKind").exists())
                .andExpect(jsonPath("$..krSubject").exists())
                .andReturn().getResponse().getContentAsString();
    }
}