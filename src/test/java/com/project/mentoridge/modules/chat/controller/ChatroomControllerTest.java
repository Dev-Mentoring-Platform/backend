package com.project.mentoridge.modules.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.controller.response.ChatroomResponse;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChatroomControllerTest {

    private final static String BASE_URL = "/api/chat/rooms";

    @InjectMocks
    ChatroomController chatroomController;
    @Mock
    ChatService chatService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatroomController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void get_my_chatrooms() {
//
//        // given
//        Mentor mentor1 = mock(Mentor.class);
//        when(mentor1.getUser()).thenReturn(mock(User.class));
//        Mentee mentee1 = mock(Mentee.class);
//        when(mentee1.getUser()).thenReturn(mock(User.class));
//        Chatroom chatroom1 = Chatroom.builder()
//                .mentor(mentor1)
//                .mentee(mentee1)
//                .build();
//
//        Mentor mentor2 = mock(Mentor.class);
//        when(mentor2.getUser()).thenReturn(mock(User.class));
//        Mentee mentee2 = mock(Mentee.class);
//        when(mentee2.getUser()).thenReturn(mock(User.class));
//        Chatroom chatroom2 = Chatroom.builder()
//                .mentor(mentor2)
//                .mentee(mentee2)
//                .build();
//
//        Page<ChatroomResponse> chatrooms = new PageImpl<>(Arrays.asList(new ChatroomResponse(chatroom1), new ChatroomResponse(chatroom2)), Pageable.ofSize(20), 2);
//        doReturn(chatrooms).when(mentorChatroomService).getChatroomResponsesOfMentor(any(User.class), anyInt());
//
//        // when
//        // then
//        mockMvc.perform(get(BASE_URL, 1))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$..chatroomId").exists())
//                .andExpect(jsonPath("$..mentorId").exists())
//                .andExpect(jsonPath("$..mentorNickname").exists())
//                .andExpect(jsonPath("$..mentorImage").exists())
//                .andExpect(jsonPath("$..menteeId").exists())
//                .andExpect(jsonPath("$..menteeNickname").exists())
//                .andExpect(jsonPath("$..menteeImage").exists())
//                .andExpect(jsonPath("$..lastMessage").exists())
//                .andExpect(jsonPath("$..uncheckedMessageCount").exists());
        //.andExpect(content().json(objectMapper.writeValueAsString(chatrooms)));
    }

    @Test
    void enter() throws Exception {

    }

    @Test
    void get_messages() throws Exception {

    }

    @Test
    void accuse() throws Exception {

        // given
        doNothing()
                .when(chatService).accuseChatroom(any(User.class), anyLong());
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/accuse", 1L))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
