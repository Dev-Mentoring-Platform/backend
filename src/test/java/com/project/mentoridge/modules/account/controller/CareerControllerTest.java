package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.service.CareerService;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.CAREER;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.base.AbstractIntegrationTest.careerCreateRequest;
import static com.project.mentoridge.modules.base.AbstractIntegrationTest.careerUpdateRequest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @AutoConfigureMockMvc
@WebMvcTest(controllers = CareerController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
class CareerControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/careers";

    @MockBean
    CareerService careerService;

    private Career career = Career.builder()
            .mentor(mock(Mentor.class))
            .job("engineer")
            .companyName("google")
            .license(null)
            .others(null)
            .build();

    @Test
    void get_career() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(careerService).getCareerResponse(user, 1L);
    }

    @Test
    void get_career_and_get_response() throws Exception {

        // given
        CareerResponse careerResponse = new CareerResponse(career);
        when(careerService.getCareerResponse(user, 1L)).thenReturn(careerResponse);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.job").hasJsonPath())
                .andExpect(jsonPath("$.companyName").hasJsonPath())
                .andExpect(jsonPath("$.others").hasJsonPath())
                .andExpect(jsonPath("$.license").hasJsonPath());
    }

    @Test
    void get_career_withEntityNotFoundException() throws Exception {

        // given
        when(careerService.getCareerResponse(user, 1L))
                .thenThrow(new EntityNotFoundException(CAREER));
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void new_career() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(careerCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(careerService).createCareer(user, careerCreateRequest);
    }

    @Test
    void edit_career() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(careerUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(careerService).updateCareer(user, 1L, careerUpdateRequest);
    }

    @Test
    void delete_career() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(careerService).deleteCareer(user, 1L);
    }

    @Test
    void delete_career_throwEntityNotFoundException() throws Exception {

        // given
        doThrow(new EntityNotFoundException(CAREER))
                .when(careerService).deleteCareer(user, 1L);
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}