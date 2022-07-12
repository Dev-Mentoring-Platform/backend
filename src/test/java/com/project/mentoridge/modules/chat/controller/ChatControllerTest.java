package com.project.mentoridge.modules.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.chat.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {

    @InjectMocks
    ChatController chatController;
    @Mock
    ChatService chatService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void new_chatroom_by_mentor() throws Exception {

        // Given
        doReturn(1L)
                .when(chatService).createChatroomByMentor(any(PrincipalDetails.class), any(Long.class));
        // When
        // Then
        String response = mockMvc.perform(post("/api/chat/mentor/me/mentee/{mentee_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("1");
    }

    @Test
    void new_chatroom_by_mentee() throws Exception {

        // Given
        doReturn(1L)
                .when(chatService).createChatroomByMentee(any(PrincipalDetails.class), any(Long.class));
        // When
        // Then
        String response = mockMvc.perform(post("/api/chat/mentee/me/mentor/{mentor_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("1");
    }

}
