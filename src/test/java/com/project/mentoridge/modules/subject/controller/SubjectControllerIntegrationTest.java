package com.project.mentoridge.modules.subject.controller;

import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.project.mentoridge.config.init.TestDataBuilder.getSubjectWithSubjectIdAndKrSubject;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class SubjectControllerIntegrationTest extends AbstractTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void getLearningKinds() throws Exception {

        String response = mockMvc.perform(get("/api/learningKinds"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
    }

    @Test
    void getSubjects() throws Exception {

        String response = mockMvc.perform(get("/api/subjects"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
    }

    @Test
    void getSubjectsByLearningKind() throws Exception {

        String response = mockMvc.perform(get("/api/subjects/{learning_kind}", LearningKindType.IT))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        System.out.println(response);
    }
}