package com.project.mentoridge.modules.subject.controller;

import com.project.mentoridge.modules.account.controller.CareerController;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.service.SubjectService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;

import static com.project.mentoridge.modules.lecture.enums.LearningKindType.IT;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SubjectController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class SubjectControllerTest extends AbstractControllerTest {

    @MockBean
    SubjectService subjectService;


    @Test
    void getLearningKinds() throws Exception {

        // given
        List<LearningKindType> learningKindTypes = Arrays.asList(IT);
        doReturn(learningKindTypes).when(subjectService).getLearningKinds();

        // when
        // then
        mockMvc.perform(get("/api/learningKinds"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(learningKindTypes)));
    }

    @Test
    void getSubjects() throws Exception {

        // given
/*
        Subject subject1 = getSubjectWithSubjectIdAndKrSubject(1L, "백엔드");
        Subject subject2 = getSubjectWithSubjectIdAndKrSubject(2L, "프론트엔드");

        SubjectResponse response1 = new SubjectResponse(subject1);
        SubjectResponse response2 = new SubjectResponse(subject2);
        List<SubjectResponse> subjects = Arrays.asList(response1, response2);
        doReturn(subjects)
                .when(subjectService).getSubjectResponses();*/
        // when
        // then
        mockMvc.perform(get("/api/subjects"))
                .andDo(print())
                .andExpect(status().isOk());
                //.andExpect(content().json(objectMapper.writeValueAsString(subjects)));
        verify(subjectService).getSubjectResponses();
    }

    @Test
    void _getSubjects() throws Exception {

        // given
/*
        Subject subject1 = getSubjectWithSubjectIdAndKrSubject(1L, "백엔드");
        Subject subject2 = getSubjectWithSubjectIdAndKrSubject(2L, "프론트엔드");

        SubjectResponse response1 = new SubjectResponse(subject1);
        List<SubjectResponse> subjects = Arrays.asList(response1);
        doReturn(subjects)
                .when(subjectService).getSubjectResponses(IT);*/
        // when
        // then
        mockMvc.perform(get("/api/learningKinds/{learning_kind}/subjects", "IT"))
                .andDo(print())
                .andExpect(status().isOk());
                //.andExpect(content().json(objectMapper.writeValueAsString(subjects)));
        verify(subjectService).getSubjectResponses(IT);
    }

//    private final String BASE_URL = "/subjects";
//
//    @Autowired
//    MockMvc mockMvc;
//
//    // @Test
//    void parent_목록조회() throws Exception {
//        mockMvc.perform(get(BASE_URL + "/parents")
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .accept(MediaType.APPLICATION_JSON_VALUE))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").exists())
//                .andExpect(jsonPath("$.result").exists())
//                .andExpect(jsonPath("$.result.parents").exists())
//                .andExpect(jsonPath("$.result.parents").isArray())
//                .andExpect(jsonPath("$.message").isString())
//                .andExpect(jsonPath("$.responseTime").isString());
//    }
//
//    // @Test
//    void subject_목록조회() throws Exception {
//        String parent = "개발";
//        mockMvc.perform(get(BASE_URL + "/parents/{parent}", parent)
//                .accept(MediaType.APPLICATION_JSON_VALUE)
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").exists())
//                .andExpect(jsonPath("$.result").exists())
//                .andExpect(jsonPath("$.result").isArray())
//                .andExpect(jsonPath("$.result[0].parent").isString())
//                .andExpect(jsonPath("$.result[0].subject").isString())
//                .andExpect(jsonPath("$.result[0].learningKind").isString())
//                .andExpect(jsonPath("$.message").isString())
//                .andExpect(jsonPath("$.responseTime").isString())
//        ;
//    }
}