package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.modules.account.service.EducationService;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.educationCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.educationUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EducationController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class EducationControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/educations";

    @MockBean
    EducationService educationService;

    @Test
    void getEducation() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{education_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(educationService).getEducationResponse(any(User.class), eq(1L));
    }

    @Test
    void newEducation() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(educationCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(educationService).createEducation(any(User.class), eq(educationCreateRequest));
    }

    @Test
    void editEducation() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{education_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(educationUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(educationService).updateEducation(any(User.class), eq(1L), eq(educationUpdateRequest));
    }

    @Test
    void deleteEducation() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{education_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(educationService).deleteEducation(any(User.class), eq(1L));
    }
}