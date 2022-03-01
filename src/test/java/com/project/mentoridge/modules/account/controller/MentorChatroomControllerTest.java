package com.project.mentoridge.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.controllerAdvice.RestControllerExceptionAdvice;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.controller.response.ChatroomResponse;
import com.project.mentoridge.modules.chat.enums.MessageType;
import com.project.mentoridge.modules.chat.service.ChatroomService;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.project.mentoridge.config.init.TestDataBuilder.getUserWithName;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class MentorChatroomControllerTest {

    private final static String BASE_URL = "/api/mentors/my-chatrooms";

    @InjectMocks
    MentorChatroomController mentorChatroomController;
    @Mock
    ChatroomService chatroomService;

    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(mentorChatroomController)
                .setControllerAdvice(RestControllerExceptionAdvice.class)
                .build();
    }

    @Test
    void getChatrooms() throws Exception {

        // given
        Mentor mentor1 = mock(Mentor.class);
        when(mentor1.getUser()).thenReturn(mock(User.class));
        Mentee mentee1 = mock(Mentee.class);
        when(mentee1.getUser()).thenReturn(mock(User.class));
        Chatroom chatroom1 = Chatroom.builder()
                .mentor(mentor1)
                .mentee(mentee1)
                .build();

        Mentor mentor2 = mock(Mentor.class);
        when(mentor2.getUser()).thenReturn(mock(User.class));
        Mentee mentee2 = mock(Mentee.class);
        when(mentee2.getUser()).thenReturn(mock(User.class));
        Chatroom chatroom2 = Chatroom.builder()
                .mentor(mentor2)
                .mentee(mentee2)
                .build();

        Page<ChatroomResponse> chatrooms = new PageImpl<>(Arrays.asList(new ChatroomResponse(chatroom1), new ChatroomResponse(chatroom2)), Pageable.ofSize(20), 2);
        doReturn(chatrooms).when(chatroomService).getChatroomResponsesOfMentor(any(User.class), anyInt());

        // when
        // then
        mockMvc.perform(get(BASE_URL, 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..chatroomId").exists())
                .andExpect(jsonPath("$..mentorId").exists())
                .andExpect(jsonPath("$..mentorNickname").exists())
                .andExpect(jsonPath("$..mentorImage").exists())
                .andExpect(jsonPath("$..menteeId").exists())
                .andExpect(jsonPath("$..menteeNickname").exists())
                .andExpect(jsonPath("$..menteeImage").exists())
                .andExpect(jsonPath("$..lastMessage").exists())
                .andExpect(jsonPath("$..uncheckedMessageCount").exists());
                //.andExpect(content().json(objectMapper.writeValueAsString(chatrooms)));
    }

    @Test
    void getMessagesOfChatroom() throws Exception {

        // given
        User user = getUserWithName("user");
        PrincipalDetails principal = new PrincipalDetails(user);
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));

        Message message1 = Message.builder()
                .type(MessageType.MESSAGE)
                .chatroomId(1L)
                .sessionId("sessionId")
                .senderNickname("user")
                .receiverId(1L)
                .message("message1")
                .sentAt(LocalDateTime.now())
                .checked(false)
                .build();
        Message message2 = Message.builder()
                .type(MessageType.MESSAGE)
                .chatroomId(1L)
                .sessionId("sessionId")
                .senderNickname("user")
                .receiverId(1L)
                .message("message2")
                .sentAt(LocalDateTime.now())
                .checked(false)
                .build();
        List<Message> messages = Arrays.asList(message1, message2);
        doReturn(messages)
                .when(chatroomService).getMessagesOfMentorChatroom(user, 1L);
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{chatroom_id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(messages)));
    }
}