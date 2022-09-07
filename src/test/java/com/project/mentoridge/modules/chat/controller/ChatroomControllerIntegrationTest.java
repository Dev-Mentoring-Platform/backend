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
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static com.project.mentoridge.config.security.jwt.JwtTokenManager.AUTHORIZATION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    private String menteeAccessTokenWithPrefix;

    private User mentee2User;
    private Mentee mentee2;
    private String mentee2AccessTokenWithPrefix;

    private User mentee3User;
    private Mentee mentee3;
    private String mentee3AccessTokenWithPrefix;

    private User mentorUser;
    private Mentor mentor;
    private String mentorAccessTokenWithPrefix;

    private Long chatroomId;
    private Chatroom chatroom;

    private Long chatroom2Id;
    private Chatroom chatroom2;

    private Long chatroom3Id;
    private Chatroom chatroom3;

    private Message message;
    private Message message2;

    @BeforeEach
    @Override
    protected void init() {
        super.init();

        // mentee1
        menteeUser = saveMenteeUser(loginService);
        mentee = menteeRepository.findByUser(menteeUser);
        menteeAccessTokenWithPrefix = getAccessToken(menteeUser.getUsername(), RoleType.MENTEE);

        // mentee2
        mentee2User = saveMenteeUser("mentee2", loginService);
        mentee2 = menteeRepository.findByUser(mentee2User);
        mentee2AccessTokenWithPrefix = getAccessToken(mentee2User.getUsername(), RoleType.MENTEE);

        // mentee3
        mentee3User = saveMenteeUser("mentee3", loginService);
        mentee3 = menteeRepository.findByUser(mentee3User);
        mentee3AccessTokenWithPrefix = getAccessToken(mentee3User.getUsername(), RoleType.MENTEE);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        mentorAccessTokenWithPrefix = getAccessToken(mentorUser.getUsername(), RoleType.MENTOR);

        // chatroomOfMentee1AndMentor
        chatroomId = chatService.createChatroomByMentee(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), mentor.getId());
        chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        // chatroomOfMentee2AndMentor
        chatroom2Id = chatService.createChatroomByMentee(new PrincipalDetails(mentee2User, "ROLE_MENTEE"), mentor.getId());
        chatroom2 = chatroomRepository.findById(chatroom2Id).orElseThrow(RuntimeException::new);
        // chatroomOfMentee3AndMentor
        chatroom3Id = chatService.createChatroomByMentee(new PrincipalDetails(mentee3User, "ROLE_MENTEE"), mentor.getId());
        chatroom3 = chatroomRepository.findById(chatroom3Id).orElseThrow(RuntimeException::new);

        // messageOfChatroom1
        message = messageRepository.save(Message.builder()
                .type(MessageType.MESSAGE)
                .chatroom(chatroom)
                .sender(menteeUser)
                .text("hello~")
                .checked(false)
                .build());
        chatroom.updateLastMessagedAt(message.getCreatedAt());

        // messageOfChatroom2
        message2 = messageRepository.save(Message.builder()
                .type(MessageType.MESSAGE)
                .chatroom(chatroom2)
                .sender(mentee2User)
                .text("hi~")
                .checked(false)
                .build());
        chatroom2.updateLastMessagedAt(message2.getCreatedAt());
        // 채팅방 입장
        chatService.enterChatroom(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), chatroom2.getId());
    }

    @Test
    void get_my_all_chatrooms_when_auth_is_null() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(Matchers.blankOrNullString()));
    }

    @Test
    void get_my_all_chatrooms() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/all")
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                // 마지막 메시지 보낸 순서로 정렬 : chatroom2 -> chatroom1 -> chatroom3
                .andExpect(jsonPath("$.[0].chatroomId").value(chatroom2Id))
                .andExpect(jsonPath("$.[0].mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.[0].mentorUserId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.[0].mentorNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.[0].mentorImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.[0].menteeId").value(mentee2.getId()))
                .andExpect(jsonPath("$.[0].menteeUserId").value(mentee2User.getId()))
                .andExpect(jsonPath("$.[0].menteeNickname").value(mentee2User.getNickname()))
                .andExpect(jsonPath("$.[0].menteeImage").value(mentee2User.getImage()))
                .andExpect(jsonPath("$.[0].lastMessage").doesNotExist())
                .andExpect(jsonPath("$.[0].uncheckedMessageCount").doesNotExist())
                
                .andExpect(jsonPath("$.[1].chatroomId").value(chatroomId))
                .andExpect(jsonPath("$.[1].mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.[1].mentorUserId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.[1].mentorNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.[1].mentorImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.[1].menteeId").value(mentee.getId()))
                .andExpect(jsonPath("$.[1].menteeUserId").value(menteeUser.getId()))
                .andExpect(jsonPath("$.[1].menteeNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.[1].menteeImage").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.[1].lastMessage").doesNotExist())
                .andExpect(jsonPath("$.[1].uncheckedMessageCount").doesNotExist())

                .andExpect(jsonPath("$.[2].chatroomId").value(chatroom3Id))
                .andExpect(jsonPath("$.[2].mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.[2].mentorUserId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.[2].mentorNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.[2].mentorImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.[2].menteeId").value(mentee3.getId()))
                .andExpect(jsonPath("$.[2].menteeUserId").value(mentee3User.getId()))
                .andExpect(jsonPath("$.[2].menteeNickname").value(mentee3User.getNickname()))
                .andExpect(jsonPath("$.[2].menteeImage").value(mentee3User.getImage()))
                .andExpect(jsonPath("$.[2].lastMessage").doesNotExist())
                .andExpect(jsonPath("$.[2].uncheckedMessageCount").doesNotExist());
    }

    @Test
    void get_my_chatrooms_by_mentorUser_as_mentor() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                // 마지막 메시지 보낸 순서로 정렬 : chatroom2 -> chatroom1 -> chatroom3
                .andExpect(jsonPath("$.content[0].chatroomId").value(chatroom2Id))
                .andExpect(jsonPath("$.content[0].mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.content[0].mentorUserId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.content[0].mentorNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.content[0].mentorImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.content[0].menteeId").value(mentee2.getId()))
                .andExpect(jsonPath("$.content[0].menteeUserId").value(mentee2User.getId()))
                .andExpect(jsonPath("$.content[0].menteeNickname").value(mentee2User.getNickname()))
                .andExpect(jsonPath("$.content[0].menteeImage").value(mentee2User.getImage()))
                .andExpect(jsonPath("$.content[0].lastMessage").exists())
                    .andExpect(jsonPath("$.content[0].lastMessage.messageId").value(message2.getId()))
                    .andExpect(jsonPath("$.content[0].lastMessage.type").value(message2.getType().name()))
                    .andExpect(jsonPath("$.content[0].lastMessage.chatroomId").value(message2.getChatroomId()))
                    .andExpect(jsonPath("$.content[0].lastMessage.senderId").value(message2.getSenderId()))
                    .andExpect(jsonPath("$.content[0].lastMessage.text").value(message2.getText()))
                    .andExpect(jsonPath("$.content[0].lastMessage.createdAt").exists())
                    .andExpect(jsonPath("$.content[0].lastMessage.checked").value(message2.isChecked()))
                    .andExpect(jsonPath("$.content[0].uncheckedMessageCount").value(0L))

                .andExpect(jsonPath("$.content[1].chatroomId").value(chatroomId))
                .andExpect(jsonPath("$.content[1].mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.content[1].mentorUserId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.content[1].mentorNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.content[1].mentorImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.content[1].menteeId").value(mentee.getId()))
                .andExpect(jsonPath("$.content[1].menteeUserId").value(menteeUser.getId()))
                .andExpect(jsonPath("$.content[1].menteeNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.content[1].menteeImage").value(menteeUser.getImage()))
                .andExpect(jsonPath("$.content[1].lastMessage").exists())
                    .andExpect(jsonPath("$.content[1].lastMessage.messageId").value(message.getId()))
                    .andExpect(jsonPath("$.content[1].lastMessage.type").value(message.getType().name()))
                    .andExpect(jsonPath("$.content[1].lastMessage.chatroomId").value(message.getChatroomId()))
                    .andExpect(jsonPath("$.content[1].lastMessage.senderId").value(message.getSenderId()))
                    .andExpect(jsonPath("$.content[1].lastMessage.text").value(message.getText()))
                    .andExpect(jsonPath("$.content[1].lastMessage.createdAt").exists())
                    .andExpect(jsonPath("$.content[1].lastMessage.checked").value(message.isChecked()))
                    .andExpect(jsonPath("$.content[1].uncheckedMessageCount").value(1L))

                .andExpect(jsonPath("$.content[2].chatroomId").value(chatroom3Id))
                .andExpect(jsonPath("$.content[2].mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.content[2].mentorUserId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.content[2].mentorNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.content[2].mentorImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.content[2].menteeId").value(mentee3.getId()))
                .andExpect(jsonPath("$.content[2].menteeUserId").value(mentee3User.getId()))
                .andExpect(jsonPath("$.content[2].menteeNickname").value(mentee3User.getNickname()))
                .andExpect(jsonPath("$.content[2].menteeImage").value(mentee3User.getImage()))
                .andExpect(jsonPath("$.content[2].lastMessage").doesNotExist());
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
                .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].chatroomId").value(chatroomId))
                .andExpect(jsonPath("$.content[0].mentorId").value(mentor.getId()))
                .andExpect(jsonPath("$.content[0].mentorUserId").value(mentorUser.getId()))
                .andExpect(jsonPath("$.content[0].mentorNickname").value(mentorUser.getNickname()))
                .andExpect(jsonPath("$.content[0].mentorImage").value(mentorUser.getImage()))
                .andExpect(jsonPath("$.content[0].menteeId").value(mentee.getId()))
                .andExpect(jsonPath("$.content[0].menteeUserId").value(menteeUser.getId()))
                .andExpect(jsonPath("$.content[0].menteeNickname").value(menteeUser.getNickname()))
                .andExpect(jsonPath("$.content[0].menteeImage").value(menteeUser.getImage()))

                .andExpect(jsonPath("$.content[0].lastMessage").exists())
                .andExpect(jsonPath("$.content[0].lastMessage.messageId").value(message.getId()))
                .andExpect(jsonPath("$.content[0].lastMessage.type").value(message.getType().name()))
                .andExpect(jsonPath("$.content[0].lastMessage.chatroomId").value(message.getChatroomId()))
                .andExpect(jsonPath("$.content[0].lastMessage.senderId").value(message.getSenderId()))
                .andExpect(jsonPath("$.content[0].lastMessage.text").value(message.getText()))
                .andExpect(jsonPath("$.content[0].lastMessage.createdAt").exists())
                .andExpect(jsonPath("$.content[0].lastMessage.checked").value(message.isChecked()))
                .andExpect(jsonPath("$.content[0].uncheckedMessageCount").value(0L));
    }
    // TODO - TEST
/*
    @DisplayName("멘토 채팅방 입장")
    @Test
    void mentor_enter() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/enter", chatroomId)
                        .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());

        // 메세지 - checked
        List<Message> messages = messageRepository.findByChatroom(chatroom);
        for(Message message : messages) {
            assertThat(message.isChecked()).isTrue();
        }

        // 채팅방 - mentorEnter
        Chatroom _chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        assertThat(_chatroom.isMentorIn()).isTrue();
        assertThat(_chatroom.isMenteeIn()).isFalse();
    }*/

    @DisplayName("멘티 채팅방 입장")
    @Test
    void mentee_enter() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/enter", chatroomId)
                .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
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
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
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
                .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
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
                .header(AUTHORIZATION, mentorAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].messageId").value(message.getId()))
                .andExpect(jsonPath("$.content[0].type").value(message.getType().name()))
                .andExpect(jsonPath("$.content[0].chatroomId").value(message.getChatroomId()))
                .andExpect(jsonPath("$.content[0].senderId").value(message.getSenderId()))
                .andExpect(jsonPath("$.content[0].text").value(message.getText()))
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                .andExpect(jsonPath("$.content[0].checked").value(message.isChecked()));
    }

    @Test
    void get_messages_of_chatroom_by_menteeUser() throws Exception {

        // Given
        // When
        // Then
        mockMvc.perform(get(BASE_URL + "/{chatroom_id}/messages", chatroomId)
                .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].messageId").value(message.getId()))
                .andExpect(jsonPath("$.content[0].type").value(message.getType().name()))
                .andExpect(jsonPath("$.content[0].chatroomId").value(message.getChatroomId()))
                .andExpect(jsonPath("$.content[0].senderId").value(message.getSenderId()))
                .andExpect(jsonPath("$.content[0].text").value(message.getText()))
                .andExpect(jsonPath("$.content[0].createdAt").exists())
                .andExpect(jsonPath("$.content[0].checked").value(message.isChecked()));
    }

    @Test
    void accuse() throws Exception {

        // Given
        chatService.enterChatroom(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), chatroomId);

        // When
        // Then
        mockMvc.perform(put(BASE_URL + "/{chatroom_id}/accuse", chatroomId)
                        .header(AUTHORIZATION, menteeAccessTokenWithPrefix))
                .andDo(print())
                .andExpect(status().isOk());

        Chatroom _chatroom = chatroomRepository.findById(chatroomId).orElseThrow(RuntimeException::new);
        assertThat(_chatroom.getAccusedCount()).isEqualTo(1L);
    }
}
