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
import com.project.mentoridge.modules.chat.enums.MessageType;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.service.ChatService;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMenteeUser;
import static com.project.mentoridge.modules.account.controller.IntegrationTest.saveMentorUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(Lifecycle.PER_CLASS)
@MockMvcTest
public class ChatroomControllerIntegrationTest extends AbstractControllerIntegrationTest {

    private final String BASE_URL = "/api/chat/rooms";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ChatService chatService;
    @Autowired
    ChatroomRepository chatroomRepository;
    @Autowired
    MessageRepository messageRepository;

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
    private String menteeAccessToken;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessToken;

    private Long chatroomId;
    private Chatroom chatroom;
    private Message message;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessToken = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessToken = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);

        chatroomId = chatService.createChatroomByMentee(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), mentor.getId());
        chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        message = messageRepository.save(Message.builder()
                .type(MessageType.MESSAGE)
                .chatroom(chatroom)
                .sender(menteeUser)
                .text("hello~")
                .checked(false)
                .build());
    }

    @Test
    void get_my_all_chatrooms() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..chatroomId").value(chatroomId))
                .andExpect(jsonPath("$..mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$..mentorUserId").value(mentorUser.getId()))
                .andExpect(jsonPath("$..mentorNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$..mentorImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$..menteeId").value(mentee.getId()))
                .andExpect(jsonPath("$..menteeUserId").value(menteeUser.getId()))
                .andExpect(jsonPath("$..menteeNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$..menteeImage").value(menteeUser.getImage()))

                .andExpect(jsonPath("$..lastMessage").doesNotExist())
                .andExpect(jsonPath("$..uncheckedMessageCount").doesNotExist());
    }

    @Test
    void get_my_chatrooms_by_mentorUser_as_mentor() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..chatroomId").value(chatroomId))
                .andExpect(jsonPath("$..mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$..mentorUserId").value(mentorUser.getId()))
                .andExpect(jsonPath("$..mentorNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$..mentorImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$..menteeId").value(mentee.getId()))
                .andExpect(jsonPath("$..menteeUserId").value(menteeUser.getId()))
                .andExpect(jsonPath("$..menteeNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$..menteeImage").value(menteeUser.getImage()))

                .andExpect(jsonPath("$..lastMessage").exists())
                .andExpect(jsonPath("$..lastMessage.messageId").value(message.getId()))
                .andExpect(jsonPath("$..lastMessage.type").value(message.getType()))
                .andExpect(jsonPath("$..lastMessage.chatroomId").value(message.getChatroomId()))
                .andExpect(jsonPath("$..lastMessage.senderId").value(message.getSenderId()))
                .andExpect(jsonPath("$..lastMessage.text").value(message.getText()))
                .andExpect(jsonPath("$..lastMessage.createdAt").value(message.getCreatedAt()))
                .andExpect(jsonPath("$..lastMessage.checked").value(message.isChecked()))
                .andExpect(jsonPath("$..uncheckedMessageCount").value(1L));
    }

    @Test
    void get_my_chatrooms_by_mentorUser_as_mentee() throws Exception {

        // Given
        String accessToken = getAccessToken(mentorUser.getUsername(), RoleType.MENTEE);
        // When
        // Then
        mockMvc.perform(get(BASE_URL)
                .header(AUTHORIZATION, accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..chatroomId").doesNotExist());
    }

    @Test
    void get_my_chatrooms_by_menteeUser_as_mentee() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL)
                .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..chatroomId").value(chatroomId))
                .andExpect(jsonPath("$..mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$..mentorUserId").value(mentorUser.getId()))
                .andExpect(jsonPath("$..mentorNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$..mentorImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$..menteeId").value(mentee.getId()))
                .andExpect(jsonPath("$..menteeUserId").value(menteeUser.getId()))
                .andExpect(jsonPath("$..menteeNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$..menteeImage").value(menteeUser.getImage()))

                .andExpect(jsonPath("$..lastMessage").exists())
                .andExpect(jsonPath("$..lastMessage.messageId").value(message.getId()))
                .andExpect(jsonPath("$..lastMessage.type").value(message.getType()))
                .andExpect(jsonPath("$..lastMessage.chatroomId").value(message.getChatroomId()))
                .andExpect(jsonPath("$..lastMessage.senderId").value(message.getSenderId()))
                .andExpect(jsonPath("$..lastMessage.text").value(message.getText()))
                .andExpect(jsonPath("$..lastMessage.createdAt").value(message.getCreatedAt()))
                .andExpect(jsonPath("$..lastMessage.checked").value(message.isChecked()))
                .andExpect(jsonPath("$..uncheckedMessageCount").value(0L));
    }

    @DisplayName("멘토 채팅방 입장")
    @Test
    void mentor_enter() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/enter", chatroomId)
                        .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk());

        // 메세지 - checked
        Message checked = messageRepository.findById(message.getId()).orElseThrow(RuntimeException::new);
        assertThat(checked.isChecked()).isTrue();
        // 채팅방 - mentorEnter
        Chatroom _chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        assertThat(_chatroom.isMentorIn()).isTrue();
        assertThat(_chatroom.isMenteeIn()).isFalse();
    }

    @DisplayName("멘티 채팅방 입장")
    @Test
    void mentee_enter() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/enter", chatroomId)
                .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk());

        // 메세지 - checked
        Message checked = messageRepository.findById(message.getId()).orElseThrow(RuntimeException::new);
        assertThat(checked.isChecked()).isFalse();
        // 채팅방 - mentorEnter
        Chatroom _chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        assertThat(_chatroom.isMentorIn()).isFalse();
        assertThat(_chatroom.isMenteeIn()).isTrue();
    }

    @DisplayName("멘토 채팅방 나가기")
    @Test
    void mentor_out() throws Exception {

        // Given
        chatService.enterChatroom(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), chatroomId);
        chatService.enterChatroom(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), chatroomId);

        // When
        // Then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/out", chatroomId)
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk());

        Chatroom _chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        assertThat(_chatroom.isMentorIn()).isFalse();
        assertThat(_chatroom.isMenteeIn()).isTrue();
    }

    @DisplayName("멘티 채팅방 나가기")
    @Test
    void mentee_out() throws Exception {

        // Given
        chatService.enterChatroom(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), chatroomId);
        chatService.enterChatroom(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), chatroomId);

        // When
        // Then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/out", chatroomId)
                .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk());

        Chatroom _chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        assertThat(_chatroom.isMentorIn()).isTrue();
        assertThat(_chatroom.isMenteeIn()).isFalse();
    }

    @Test
    void get_messages_of_chatroom_by_mentorUser() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{chatroom_id}/messages", chatroomId)
                .header(AUTHORIZATION, mentorAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..messageId").value(message.getId()))
                .andExpect(jsonPath("$..type").value(message.getType()))
                .andExpect(jsonPath("$..chatroomId").value(message.getChatroomId()))
                .andExpect(jsonPath("$..senderId").value(message.getSenderId()))
                .andExpect(jsonPath("$..text").value(message.getText()))
                .andExpect(jsonPath("$..createdAt").value(message.getCreatedAt()))
                .andExpect(jsonPath("$..checked").value(message.isChecked()));
    }

    @Test
    void get_messages_of_chatroom_by_menteeUser() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{chatroom_id}/messages", chatroomId)
                .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..messageId").value(message.getId()))
                .andExpect(jsonPath("$..type").value(message.getType()))
                .andExpect(jsonPath("$..chatroomId").value(message.getChatroomId()))
                .andExpect(jsonPath("$..senderId").value(message.getSenderId()))
                .andExpect(jsonPath("$..text").value(message.getText()))
                .andExpect(jsonPath("$..createdAt").value(message.getCreatedAt()))
                .andExpect(jsonPath("$..checked").value(message.isChecked()));
    }

    @Test
    void accuse() throws Exception {

        // Given
        chatService.enterChatroom(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), chatroomId);

        // When
        // Then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/accuse", chatroomId)
                        .header(AUTHORIZATION, menteeAccessToken))
                .andDo(print())
                .andExpect(status().isOk());

        Chatroom _chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        assertThat(_chatroom.getAccusedCount()).isEqualTo(1L);
    }
}
