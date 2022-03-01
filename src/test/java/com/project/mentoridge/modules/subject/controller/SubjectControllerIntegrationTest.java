package com.project.mentoridge.modules.subject.controller;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class SubjectControllerIntegrationTest extends AbstractTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getLearningKinds() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/learningKinds"))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getSubjects() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/subjects"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..subjectId").exists())
                .andExpect(jsonPath("$..learningKind").exists())
                .andExpect(jsonPath("$..krSubject").exists());
    }

    @Test
    void getSubjectsByLearningKind() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get("/api/subjects/{learning_kind}", LearningKindType.IT))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..subjectId").exists())
                .andExpect(jsonPath("$..learningKind").exists())
                .andExpect(jsonPath("$..krSubject").exists());
    }
}