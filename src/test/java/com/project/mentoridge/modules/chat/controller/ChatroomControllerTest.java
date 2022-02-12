package com.project.mentoridge.modules.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.service.ChatroomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChatroomControllerTest {

    private final static String BASE_URL = "/api/chat/rooms";

    @InjectMocks
    ChatroomController chatroomController;
    @Mock
    ChatroomService chatroomService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatroomController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void accuse() throws Exception {

        // given
        doNothing()
                .when(chatroomService).accuse(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/accuse", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
