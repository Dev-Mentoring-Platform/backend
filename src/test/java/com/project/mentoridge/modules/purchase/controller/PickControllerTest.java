package com.project.mentoridge.modules.purchase.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.purchase.service.PickService;
import com.project.mentoridge.modules.purchase.vo.Pick;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PickControllerTest {

    @InjectMocks
    PickController pickController;
    @Mock
    PickService pickService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(pickController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void addPick() throws Exception {

        // given
        doReturn(Mockito.mock(Pick.class))
                .when(pickService).createPick(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(post("/api/lectures/{lecture_id}/picks", 1L))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}