package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.configuration.AbstractTest;
import com.project.mentoridge.modules.account.controller.request.EducationCreateRequest;
import com.project.mentoridge.modules.account.controller.request.EducationUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.EducationResponse;
import com.project.mentoridge.modules.account.service.EducationService;
import com.project.mentoridge.modules.account.vo.Education;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.project.mentoridge.config.init.TestDataBuilder.getEducationWithMentor;
import static com.project.mentoridge.configuration.AbstractTest.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class EducationControllerTest {

    private final static String BASE_URL = "/api/educations";

    MockMvc mockMvc;
    ObjectMapper objectMapper;

    @InjectMocks
    EducationController educationController;
    @Mock
    EducationService educationService;

    @BeforeEach
    void setup() {

        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        mockMvc = MockMvcBuilders.standaloneSetup(educationController)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }

    @Test
    void getEducation() throws Exception {

        // given
        Education education = getEducationWithMentor(mock(Mentor.class));
        EducationResponse educationResponse = new EducationResponse(education);
        doReturn(educationResponse)
                .when(educationService).getEducationResponse(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{education_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                //.andExpect(content().json(objectMapper.writeValueAsString(educationResponse)));
                .andExpect(jsonPath("$.educationLevel").exists())
                .andExpect(jsonPath("$.schoolName").exists())
                .andExpect(jsonPath("$.major").exists())
                .andExpect(jsonPath("$.others").exists());
    }

    @Test
    void newEducation() throws Exception {

        // given
        Education education = Mockito.mock(Education.class);
        when(educationService.createEducation(any(User.class), any(EducationCreateRequest.class))).thenReturn(education);

        // when
        // then
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(educationCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void editEducation() throws Exception {

        // given
        doNothing()
                .when(educationService).updateEducation(any(User.class), anyLong(), any(EducationUpdateRequest.class));

        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{education_id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(educationUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteEducation() throws Exception {

        // given
        doNothing()
                .when(educationService).deleteEducation(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{education_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}