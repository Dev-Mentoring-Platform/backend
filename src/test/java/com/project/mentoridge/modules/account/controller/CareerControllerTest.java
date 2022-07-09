package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.controller.request.CareerCreateRequest;
import com.project.mentoridge.modules.account.controller.request.CareerUpdateRequest;
import com.project.mentoridge.modules.account.controller.response.CareerResponse;
import com.project.mentoridge.modules.account.service.CareerService;
import com.project.mentoridge.modules.account.vo.Career;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.CAREER;
import static com.project.mentoridge.configuration.AbstractTest.careerCreateRequest;
import static com.project.mentoridge.configuration.AbstractTest.careerUpdateRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CareerController.class, excludeFilters = {})
@ExtendWith(MockitoExtension.class)
class CareerControllerTest {

    private final static String BASE_URL = "/api/careers";

    @Mock
    CareerService careerService;
    @InjectMocks
    CareerController careerController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    private Career career = Career.builder()
            .mentor(mock(Mentor.class))
            .job("engineer")
            .companyName("google")
            .license(null)
            .others(null)
            .build();
    private CareerResponse careerResponse;

    @BeforeEach
    void init() {

        objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        mockMvc = MockMvcBuilders.standaloneSetup(careerController)
                .setControllerAdvice(RestControllerExceptionAdvice.class).build();
    }

    @Test
    void getCareer() throws Exception {

        // given
        careerResponse = new CareerResponse(career);
        when(careerService.getCareerResponse(any(User.class), anyLong())).thenReturn(careerResponse);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{career_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.job").hasJsonPath())
                .andExpect(jsonPath("$.companyName").hasJsonPath())
                .andExpect(jsonPath("$.others").hasJsonPath())
                .andExpect(jsonPath("$.license").hasJsonPath());
    }

    @Test
    void getCareer_withEntityNotFoundException() throws Exception {

        // given
        when(careerService.getCareerResponse(any(User.class), anyLong()))
                .thenThrow(new EntityNotFoundException(CAREER));
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{career_id}", 1L))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void newCareer() throws Exception {

        // given
        when(careerService.createCareer(any(User.class), any(CareerCreateRequest.class)))
                .thenReturn(career);
        // when
        // then
        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(careerCreateRequest)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void editCareer() throws Exception {

        // given
        doNothing().when(careerService)
                .updateCareer(any(User.class), anyLong(), any(CareerUpdateRequest.class));

        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{career_id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(careerUpdateRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteCareer() throws Exception {

        // given
        doNothing()
                .when(careerService).deleteCareer(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{career_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void deleteCareer_throwEntityNotFoundException() throws Exception {

        // given
        doThrow(new EntityNotFoundException(CAREER))
                .when(careerService).deleteCareer(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{career_id}", 1L))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}