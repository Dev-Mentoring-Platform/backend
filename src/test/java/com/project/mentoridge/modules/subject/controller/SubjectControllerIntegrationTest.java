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

import static com.project.mentoridge.config.init.TestDataBuilder.getSubjectWithKrSubject;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class SubjectControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    SubjectRepository subjectRepository;

    @BeforeEach
    void init() {
        subjectRepository.save(getSubjectWithKrSubject("프론트엔드"));
        subjectRepository.save(getSubjectWithKrSubject("백엔드"));
        subjectRepository.save(getSubjectWithKrSubject("언어"));
    }

    @Test
    void getLearningKinds() throws Exception {

        mockMvc.perform(get("/api/learningKinds"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getSubjects() throws Exception {

        mockMvc.perform(get("/api/subjects"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getSubjectsByLearningKind() throws Exception {

        mockMvc.perform(get("/api/subjects/{learning_kind_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}