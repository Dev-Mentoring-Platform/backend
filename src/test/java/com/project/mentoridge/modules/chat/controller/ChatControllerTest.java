package com.project.mentoridge.modules.chat.controller;

import com.project.mentoridge.modules.base.AbstractControllerTest;
import com.project.mentoridge.modules.chat.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ChatController.class,
        properties = {"spring.config.location=classpath:application-test.yml"})
public class ChatControllerTest extends AbstractControllerTest {

    @MockBean
    ChatService chatService;

    //@WithMockUser(username = "user@email.com", roles = {"MENTOR"})
    @Test
    void new_chatroom_by_mentor() throws Exception {

        // Given
//        doReturn(1L)
//                .when(chatService).createChatroomByMentor(any(PrincipalDetails.class), any(Long.class));
        // When
        // Then
        mockMvc.perform(post("/api/chat/mentor/me/mentee/{mentee_id}", 1L)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());
        verify(chatService).createChatroomByMentor(principalDetails, 1L);
    }

    //@WithMockUser(username = "user@email.com", roles = {"MENTEE"})
    @Test
    void new_chatroom_by_mentee() throws Exception {

        // Given
        doReturn(1L)
                .when(chatService).createChatroomByMentee(principalDetails, 1L);
        // When
        // Then
        String response = mockMvc.perform(post("/api/chat/mentee/me/mentor/{mentor_id}", 1L)
                        .header(AUTHORIZATION, accessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        assertThat(response).isEqualTo("1");
    }

}
