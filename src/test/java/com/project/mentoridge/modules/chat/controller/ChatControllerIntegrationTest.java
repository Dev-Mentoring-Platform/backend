package com.project.mentoridge.modules.chat.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.configuration.annotation.MockMvcTest;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractControllerIntegrationTest;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
public class ChatControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ChatService chatService;
    @Autowired
    ChatroomRepository chatroomRepository;

    @Autowired
    LoginService loginService;
    @Autowired
    MenteeRepository menteeRepository;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;

    private User menteeUser;
    private Mentee mentee;
    private String menteeAccessTokenWithPrefix;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessTokenWithPrefix;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessTokenWithPrefix = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessTokenWithPrefix = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);
    }

    @Test
    void new_chatroom_by_mentor() throws Exception {

        // Given
        // When
        mockMvc.perform(post("/api/chat/mentor/me/mentee/{mentee_id}", mentee.getId())
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        assertThat(chatroomRepository.findByMentorAndMentee(mentor, mentee).isPresent()).isTrue();
    }

    @Test
    void get_already_created_chatroom_by_mentor() throws Exception {

        // Given
        // 멘티에 의해 생성된 채팅방
        Long chatroomId = chatService.createChatroomByMentee(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), mentor.getId());
        // When
        mockMvc.perform(post("/api/chat/mentor/me/mentee/{mentee_id}", mentee.getId())
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        Chatroom chatroom = chatroomRepository.findByMentorAndMentee(mentor, mentee).orElseThrow(RuntimeException::new);
        assertThat(chatroom.getId()).isEqualTo(chatroomId);
    }

    @Test
    void new_chatroom_by_mentee() throws Exception {

        // Given
        // When
        mockMvc.perform(post("/api/chat/mentee/me/mentor/{mentor_id}", mentor.getId())
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        assertThat(chatroomRepository.findByMentorAndMentee(mentor, mentee).isPresent()).isTrue();
    }

    @Test
    void get_already_created_chatroom_by_mentee() throws Exception {

        // Given
        // 멘토에 의해 생성된 채팅방
        Long chatroomId = chatService.createChatroomByMentor(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), mentee.getId());
        // When
        mockMvc.perform(post("/api/chat/mentee/me/mentor/{mentor_id}", mentor.getId())
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());

        // Then
        Chatroom chatroom = chatroomRepository.findByMentorAndMentee(mentor, mentee).orElseThrow(RuntimeException::new);
        assertThat(chatroom.getId()).isEqualTo(chatroomId);
    }
}
