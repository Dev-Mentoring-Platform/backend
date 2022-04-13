package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.controller.response.PickResponse;
import com.project.mentoridge.modules.purchase.service.PickServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MenteePickControllerTest {

    private final static String BASE_URL = "/api/mentees/my-picks";

    @InjectMocks
    MenteePickController menteePickController;
    @Mock
    PickServiceImpl pickService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(menteePickController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getPicks() throws Exception {

        // given
        Page<PickResponse> picks = Page.empty();
        doReturn(picks)
                .when(pickService).getPickWithSimpleLectureResponses(any(User.class), anyInt());
        // when
        // then
        mockMvc.perform(get(BASE_URL, 1))
                .andDo(print())
                .andExpect(status().isOk())
                //.andExpect(jsonPath("$..lectureTitle").exists());
                .andExpect(content().json(objectMapper.writeValueAsString(picks)));
    }

    @Test
    void subtractPick() throws Exception {

        // given
        doNothing()
                .when(pickService).deletePick(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(delete(BASE_URL + "/{pick_id}", 1))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void clear() throws Exception {

        // given
        doNothing()
                .when(pickService).deleteAllPicks(any(User.class));
        // when
        // then
        mockMvc.perform(delete(BASE_URL))
                .andDo(print())
                .andExpect(status().isOk());
    }
}