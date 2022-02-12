package com.project.mentoridge.modules.subject.controller;

import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.lecture.embeddable.LearningKind;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@Transactional
@MockMvcTest
class SubjectControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    SubjectRepository subjectRepository;

    @BeforeEach
    void init() {
        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.IT), "자바"));
        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.IT), "파이썬"));
        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.IT), "C/C++"));
        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.LANGUAGE), "영어"));
        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.LANGUAGE), "중국어"));
    }

    @Test
    void getLearningKinds() throws Exception {

        mockMvc.perform(get("/learningKinds"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getSubjects() throws Exception {

        mockMvc.perform(get("/subjects"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getSubjectsByLearningKind() throws Exception {

        mockMvc.perform(get("/subjects/{learning_kind_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}