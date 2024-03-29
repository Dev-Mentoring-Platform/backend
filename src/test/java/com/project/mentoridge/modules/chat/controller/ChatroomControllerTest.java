package com.project.mentoridge.modules.chat.controller;

import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.chat.service.ChatService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChatroomController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
public class ChatroomControllerTest extends AbstractControllerTest {

    private final static String BASE_URL = "/api/chat/rooms";

    @MockBean
    ChatService chatService;


    //@WithMockUser(username = "user@email.com", roles = {"MENTOR"})
    @DisplayName("채팅방 리스트 - 페이징 X")
    @Test
    void get_my_all_chatrooms() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/all")
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(chatService).getChatroomResponses(principalDetails);
    }

    //@WithMockUser(username = "user@email.com", roles = {"MENTOR"})
    @Test
    void get_paged_my_chatrooms() throws Exception {

        // given
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
//        Page<ChatroomResponse> chatrooms = new PageImpl<>(Arrays.asList(new ChatroomResponse(chatroom1), new ChatroomResponse(chatroom2)), Pageable.ofSize(20), 2);
//        doReturn(chatrooms).when(mentorChatroomService).getChatroomResponsesOfMentor(any(User.class), anyInt());

        // when
        // then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(chatService).getChatroomResponses(principalDetails, 1);
    }

    //@WithMockUser(username = "user@email.com", roles = {"MENTOR"})
    @Test
    void enter() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/enter", 3L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(chatService).enterChatroom(principalDetails, 3L);
    }

    //@WithMockUser(username = "user@email.com", roles = {"MENTOR"})
    @Test
    void out() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/out", 4L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(chatService).outChatroom(principalDetails, 4L);
    }

    @Test
    void get_messages() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(get(BASE_URL + "/{chatroom_id}/messages", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(chatService).getChatMessagesOfChatroom(1L, 1);
    }

    @Test
    void accuse() throws Exception {

        // given
        // when
        // then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/accuse", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(chatService).accuseChatroom(user, 1L);
    }
}
