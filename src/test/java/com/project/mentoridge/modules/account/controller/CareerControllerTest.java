package com.project.mentoridge.modules.account.controller;

import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.service.CareerService;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.CAREER;
import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.configuration.AbstractTest.careerCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.careerUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CareerController.class, excludeFilters = {})
@ExtendWith(MockitoExtension.class)
class CareerControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/careers";

    @InjectMocks
    CareerController careerController;
    @Mock
    CareerService careerService;

    private Career career = Career.builder()
            .mentor(mock(Mentor.class))
            .job("engineer")
            .companyName("google")
            .license(null)
            .others(null)
            .build();
    private CareerResponse careerResponse;

    @BeforeEach
    @Override
    protected void init() {
        super.init();
        mockMvc = MockMvcBuilders.standaloneSetup(careerController)
                .addFilter(jwtRequestFilter)
                .addInterceptors(authInterceptor)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }

    @Test
    void get_career() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(careerService).getCareerResponse(any(User.class), 1L);
    }

    @Test
    void get_career_and_get_response() throws Exception {

        // given
        careerResponse = new CareerResponse(career);
        when(careerService.getCareerResponse(any(User.class), 1L)).thenReturn(careerResponse);
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
        when(careerService.getCareerResponse(any(User.class), 1L))
                .thenThrow(new EntityNotFoundException(CAREER));
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(careerService);
    }

    @Test
    void new_career() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(post(BASE_URL)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(careerCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
        verify(careerService).createCareer(any(User.class), eq(careerCreateRequest));
    }

    @Test
    void edit_career() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(careerUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
        verify(careerService).updateCareer(any(User.class), 1L, eq(careerUpdateRequest));
    }

    @Test
    void delete_career() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(careerService).deleteCareer(any(User.class), 1L);
    }

    @Test
    void delete_career_throwEntityNotFoundException() throws Exception {

        // given
        doThrow(new EntityNotFoundException(CAREER))
                .when(careerService).deleteCareer(any(User.class), 1L);
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{career_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isBadRequest());
        verifyNoInteractions(careerService);
    }
}