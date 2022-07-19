package com.project.mentoridge.modules.chat.service;

import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.configuration.annotation.ServiceTest;
import com.project.mentoridge.modules.account.controller.request.SignUpRequest;
import com.project.mentoridge.modules.account.enums.GenderType;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.service.LoginService;
import com.project.mentoridge.modules.account.service.MentorService;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.address.repository.AddressRepository;
import com.project.mentoridge.modules.chat.controller.ChatMessage;
import com.project.mentoridge.modules.chat.controller.response.ChatroomResponse;
import com.project.mentoridge.modules.chat.enums.MessageType;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(Lifecycle.PER_CLASS)
@ServiceTest
class ChatServiceIntegrationTest {
// TODO - ENTER, SEND 테스트
    @Autowired
    ChatService chatService;
    @Autowired
    ChatroomRepository chatroomRepository;
//    @Autowired
//    MessageMongoRepository messageRepository;
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    LoginService loginService;
    @Autowired
    MentorService mentorService;
    @Autowired
    MentorRepository mentorRepository;
    @Autowired
    MenteeRepository menteeRepository;

    protected User mentorUser;
    protected Mentor mentor;
    protected User menteeUser1;
    protected Mentee mentee1;
    protected User menteeUser2;
    protected Mentee mentee2;

    @BeforeAll
    void init() {

        saveAddress(addressRepository);
        saveSubject(subjectRepository);

        mentorUser = saveMentorUser(loginService, mentorService);
        mentor = mentorRepository.findByUser(mentorUser);
        menteeUser1 = saveMenteeUser(loginService);
        mentee1 = menteeRepository.findByUser(menteeUser1);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("menteeUser2@email.com")
                .password("password")
                .passwordConfirm("password")
                .name("menteeUserName2")
                .gender(GenderType.MALE)
                .birthYear("1995")
                .phoneNumber("01033334444")
                .nickname("menteeUserNickname2")
                .build();
        menteeUser2 = loginService.signUp(signUpRequest);
        loginService.verifyEmail(menteeUser2.getUsername(), menteeUser2.getEmailVerifyToken());
        mentee2 = menteeRepository.findByUser(menteeUser2);

    }

    @DisplayName("로그인한 사용자의 채팅방 리스트")
    @Test
    void get_ChatroomResponses() {

        // given
        Chatroom chatroom1 = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee1)
                .build());
        Chatroom chatroom2 = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee2)
                .build());

        // when
        // 멘토로 접속한 경우
        List<ChatroomResponse> ofMentorUserWhenMentor = chatService.getChatroomResponses(new PrincipalDetails(mentorUser, "ROLE_MENTOR"));
        // 멘티로 접속한 경우
        List<ChatroomResponse> ofMentorUserWhenMentee = chatService.getChatroomResponses(new PrincipalDetails(mentorUser, "ROLE_MENTEE"));

        List<ChatroomResponse> ofMenteeUser1 = chatService.getChatroomResponses(new PrincipalDetails(menteeUser1, "ROLE_MENTEE"));
        List<ChatroomResponse> ofMenteeUser2 = chatService.getChatroomResponses(new PrincipalDetails(menteeUser2, "ROLE_MENTEE"));

        // then
        assertThat(ofMentorUserWhenMentor).hasSize(2);
        assertThat(ofMentorUserWhenMentee).hasSize(0);
        assertThat(ofMenteeUser1).hasSize(1);

        assertThat(ofMenteeUser2).hasSize(1);
        ChatroomResponse _ofMenteeUser2 = ofMenteeUser2.get(0);
        assertAll(
                () -> assertThat(_ofMenteeUser2.getChatroomId()).isEqualTo(chatroom2.getId()),

                () -> assertThat(_ofMenteeUser2.getMentorId()).isEqualTo(chatroom2.getMentor().getId()),
                () -> assertThat(_ofMenteeUser2.getMentorUserId()).isEqualTo(chatroom2.getMentor().getUser().getId()),
                () -> assertThat(_ofMenteeUser2.getMentorNickname()).isEqualTo(chatroom2.getMentor().getUser().getNickname()),
                () -> assertThat(_ofMenteeUser2.getMentorImage()).isEqualTo(chatroom2.getMentor().getUser().getImage()),

                () -> assertThat(_ofMenteeUser2.getMenteeId()).isEqualTo(chatroom2.getMentee().getId()),
                () -> assertThat(_ofMenteeUser2.getMenteeUserId()).isEqualTo(chatroom2.getMentee().getUser().getId()),
                () -> assertThat(_ofMenteeUser2.getMenteeNickname()).isEqualTo(chatroom2.getMentee().getUser().getNickname()),
                () -> assertThat(_ofMenteeUser2.getMenteeImage()).isEqualTo(chatroom2.getMentee().getUser().getImage()),

                () -> assertThat(_ofMenteeUser2.getLastMessage()).isNull(),
                () -> assertThat(_ofMenteeUser2.getUncheckedMessageCount()).isNull()
        );
    }

    @Test
    void get_paged_ChatroomResponses_noMessage() {

        // given
        Chatroom chatroom1 = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee1)
                .build());
        Chatroom chatroom2 = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee2)
                .build());

        // when
        // 멘토로 접속한 경우
        Page<ChatroomResponse> ofMentorUserWhenMentor = chatService.getChatroomResponses(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), 1);
        // 멘티로 접속한 경우
        Page<ChatroomResponse> ofMentorUserWhenMentee = chatService.getChatroomResponses(new PrincipalDetails(mentorUser, "ROLE_MENTEE"), 1);

        Page<ChatroomResponse> ofMenteeUser1 = chatService.getChatroomResponses(new PrincipalDetails(menteeUser1, "ROLE_MENTEE"), 1);
        Page<ChatroomResponse> ofMenteeUser2 = chatService.getChatroomResponses(new PrincipalDetails(menteeUser2, "ROLE_MENTEE"), 1);

        // then
        assertThat(ofMentorUserWhenMentor.getTotalElements()).isEqualTo(2);
        assertThat(ofMentorUserWhenMentee.getTotalElements()).isEqualTo(0);
        assertThat(ofMenteeUser1.getTotalElements()).isEqualTo(1);

        assertThat(ofMenteeUser2.getTotalElements()).isEqualTo(1);
        ChatroomResponse _ofMenteeUser2 = ofMenteeUser2.getContent().get(0);
        assertAll(
                () -> assertThat(_ofMenteeUser2.getChatroomId()).isEqualTo(chatroom2.getId()),

                () -> assertThat(_ofMenteeUser2.getMentorId()).isEqualTo(chatroom2.getMentor().getId()),
                () -> assertThat(_ofMenteeUser2.getMentorUserId()).isEqualTo(chatroom2.getMentor().getUser().getId()),
                () -> assertThat(_ofMenteeUser2.getMentorNickname()).isEqualTo(chatroom2.getMentor().getUser().getNickname()),
                () -> assertThat(_ofMenteeUser2.getMentorImage()).isEqualTo(chatroom2.getMentor().getUser().getImage()),

                () -> assertThat(_ofMenteeUser2.getMenteeId()).isEqualTo(chatroom2.getMentee().getId()),
                () -> assertThat(_ofMenteeUser2.getMenteeUserId()).isEqualTo(chatroom2.getMentee().getUser().getId()),
                () -> assertThat(_ofMenteeUser2.getMenteeNickname()).isEqualTo(chatroom2.getMentee().getUser().getNickname()),
                () -> assertThat(_ofMenteeUser2.getMenteeImage()).isEqualTo(chatroom2.getMentee().getUser().getImage()),

                () -> assertThat(_ofMenteeUser2.getLastMessage()).isNull(),
                () -> assertThat(_ofMenteeUser2.getUncheckedMessageCount()).isNull()
        );
    }

    @Test
    void get_paged_ChatroomResponses() {

        // given
        Chatroom chatroom1 = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee1)
                .build());
        Chatroom chatroom2 = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee2)
                .build());

        Message messageFromMenteeUser1ToMentorUser = Message.builder()
                .type(MessageType.MESSAGE)
                .chatroom(chatroom1)
                .sender(menteeUser1)
                .text("hello")
                .checked(true)
                .build();
        Message messageFromMentorUserToMenteeUser1 = Message.builder()
                .type(MessageType.MESSAGE)
                .chatroom(chatroom1)
                .sender(mentorUser)
                .text("hi~")
                .checked(false)
                .build();
        messageRepository.saveAll(Arrays.asList(messageFromMenteeUser1ToMentorUser, messageFromMentorUserToMenteeUser1));

        // when
        // 멘토로 접속한 경우
        Page<ChatroomResponse> ofMentorUserWhenMentor = chatService.getChatroomResponses(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), 1);
        // 멘티로 접속한 경우
        Page<ChatroomResponse> ofMentorUserWhenMentee = chatService.getChatroomResponses(new PrincipalDetails(mentorUser, "ROLE_MENTEE"), 1);

        Page<ChatroomResponse> ofMenteeUser1 = chatService.getChatroomResponses(new PrincipalDetails(menteeUser1, "ROLE_MENTEE"), 1);
        Page<ChatroomResponse> ofMenteeUser2 = chatService.getChatroomResponses(new PrincipalDetails(menteeUser2, "ROLE_MENTEE"), 1);

        // then
        assertThat(ofMentorUserWhenMentor.getTotalElements()).isEqualTo(2);
        assertThat(ofMentorUserWhenMentee.getTotalElements()).isEqualTo(0);

        assertThat(ofMenteeUser1.getTotalElements()).isEqualTo(1);
        ChatroomResponse _ofMenteeUser1 = ofMenteeUser1.getContent().get(0);
        assertAll(
                () -> assertThat(_ofMenteeUser1.getChatroomId()).isEqualTo(chatroom2.getId()),

                () -> assertThat(_ofMenteeUser1.getMentorId()).isEqualTo(chatroom2.getMentor().getId()),
                () -> assertThat(_ofMenteeUser1.getMentorUserId()).isEqualTo(chatroom2.getMentor().getUser().getId()),
                () -> assertThat(_ofMenteeUser1.getMentorNickname()).isEqualTo(chatroom2.getMentor().getUser().getNickname()),
                () -> assertThat(_ofMenteeUser1.getMentorImage()).isEqualTo(chatroom2.getMentor().getUser().getImage()),

                () -> assertThat(_ofMenteeUser1.getMenteeId()).isEqualTo(chatroom2.getMentee().getId()),
                () -> assertThat(_ofMenteeUser1.getMenteeUserId()).isEqualTo(chatroom2.getMentee().getUser().getId()),
                () -> assertThat(_ofMenteeUser1.getMenteeNickname()).isEqualTo(chatroom2.getMentee().getUser().getNickname()),
                () -> assertThat(_ofMenteeUser1.getMenteeImage()).isEqualTo(chatroom2.getMentee().getUser().getImage()),

                () -> assertThat(_ofMenteeUser1.getLastMessage().getType()).isEqualTo(messageFromMentorUserToMenteeUser1.getType()),
                () -> assertThat(_ofMenteeUser1.getLastMessage().getChatroomId()).isEqualTo(messageFromMentorUserToMenteeUser1.getChatroomId()),
                () -> assertThat(_ofMenteeUser1.getLastMessage().getSenderId()).isEqualTo(messageFromMentorUserToMenteeUser1.getSenderId()),
                () -> assertThat(_ofMenteeUser1.getLastMessage().getSenderId()).isEqualTo(mentorUser.getId()),
                () -> assertThat(_ofMenteeUser1.getLastMessage().getReceiverId()).isEqualTo(menteeUser1.getId()),
                () -> assertThat(_ofMenteeUser1.getLastMessage().getText()).isEqualTo(messageFromMentorUserToMenteeUser1.getText()),
                () -> assertThat(_ofMenteeUser1.getLastMessage().getCreatedAt()).isNotNull(),
                () -> assertThat(_ofMenteeUser1.getLastMessage().isChecked()).isFalse(),

                () -> assertThat(_ofMenteeUser1.getUncheckedMessageCount()).isEqualTo(1L)
        );
    }

    @Test
    void get_paged_ChatMessages() {

        // given
        Chatroom chatroom1 = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee1)
                .build());
        Message messageFromMenteeUser1ToMentorUser = Message.builder()
                .type(MessageType.MESSAGE)
                .chatroom(chatroom1)
                .sender(menteeUser1)
                .text("hello")
                .checked(true)
                .build();
        Message messageFromMentorUserToMenteeUser1 = Message.builder()
                .type(MessageType.MESSAGE)
                .chatroom(chatroom1)
                .sender(mentorUser)
                .text("hi~")
                .checked(false)
                .build();
        messageRepository.saveAll(Arrays.asList(messageFromMenteeUser1ToMentorUser, messageFromMentorUserToMenteeUser1));

        // when
        Page<ChatMessage> messages = chatService.getChatMessagesOfChatroom(chatroom1.getId(), 1);

        // then
        System.out.println(messages);
        assertThat(messages.getTotalElements()).isEqualTo(2L);
        // DESC
        ChatMessage message1 = messages.getContent().get(0);
        assertAll(
                () -> assertThat(message1.getType()).isEqualTo(messageFromMentorUserToMenteeUser1.getType()),
                () -> assertThat(message1.getChatroomId()).isEqualTo(messageFromMentorUserToMenteeUser1.getChatroomId()),
                () -> assertThat(message1.getSenderId()).isEqualTo(messageFromMentorUserToMenteeUser1.getSenderId()),
                () -> assertThat(message1.getSenderId()).isEqualTo(mentorUser.getId()),
                () -> assertThat(message1.getReceiverId()).isEqualTo(menteeUser1.getId()),
                () -> assertThat(message1.getText()).isEqualTo(messageFromMentorUserToMenteeUser1.getText()),
                () -> assertThat(message1.getCreatedAt()).isNotNull(),
                () -> assertThat(message1.isChecked()).isFalse()
        );
        ChatMessage message2 = messages.getContent().get(1);
        assertAll(
                () -> assertThat(message2.getType()).isEqualTo(messageFromMenteeUser1ToMentorUser.getType()),
                () -> assertThat(message2.getChatroomId()).isEqualTo(messageFromMenteeUser1ToMentorUser.getChatroomId()),
                () -> assertThat(message2.getSenderId()).isEqualTo(messageFromMenteeUser1ToMentorUser.getSenderId()),
                () -> assertThat(message2.getSenderId()).isEqualTo(menteeUser1.getId()),
                () -> assertThat(message2.getReceiverId()).isEqualTo(mentorUser.getId()),
                () -> assertThat(message2.getText()).isEqualTo(messageFromMenteeUser1ToMentorUser.getText()),
                () -> assertThat(message2.getCreatedAt()).isNotNull(),
                () -> assertThat(message2.isChecked()).isTrue()
        );
    }

    @DisplayName("멘토가 채팅방 생성")
    @Test
    void create_chatroom_by_mentor() {

        // given
        // when
        Long chatroomId = chatService.createChatroomByMentor(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), menteeUser1.getId());

        // then
        assertTrue(chatroomRepository.findByMentorAndMentee(mentor, mentee1).isPresent());
    }

    @DisplayName("멘토가 채팅방 생성 - 이미 존재하는 경우")
    @Test
    void create_chatroom_by_mentor_when_chatroom_already_exists() {

        // given
        Chatroom chatroom = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee1)
                .build());
        // when
        Long chatroomId = chatService.createChatroomByMentor(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), menteeUser1.getId());

        // then
        assertThat(chatroomId).isEqualTo(chatroom.getId());
    }

    @DisplayName("멘티가 채팅방 생성")
    @Test
    void create_chatroom_by_mentee() {

        // given
        // when
        Long chatroomId = chatService.createChatroomByMentee(new PrincipalDetails(menteeUser1, "ROLE_MENTEE"), mentorUser.getId());

        // then
        Optional<Chatroom> optional = chatroomRepository.findByMentorAndMentee(mentor, mentee1);
        assertTrue(optional.isPresent());

        Chatroom chatroom = optional.get();
        assertThat(chatroom.getId()).isEqualTo(chatroomId);
    }

    @DisplayName("멘티가 채팅방 생성 - 이미 존재하는 경우")
    @Test
    void create_chatroom_by_mentee_when_chatroom_already_exists() {

        // given
        Chatroom chatroom = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee1)
                .build());
        // when
        Long chatroomId = chatService.createChatroomByMentee(new PrincipalDetails(menteeUser1, "ROLE_MENTEE"), mentorUser.getId());

        // then
        assertThat(chatroomId).isEqualTo(chatroom.getId());
    }

    @DisplayName("채팅방 종료")
    @Test
    void close_chatroom() {

        // given
        Chatroom chatroom = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee1)
                .build());

        // when
        chatService.closeChatroom(mentorUser, chatroom.getId());
        // then
        assertTrue(chatroom.isClosed());
    }

    @Test
    void out_chatroom_by_mentor() {

        // given
        Chatroom chatroom = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee1)
                .build());
        chatroom.mentorEnter();
        chatroom.menteeEnter();

        // when
        chatService.outChatroom(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), chatroom.getId());
        // then
        assertFalse(chatroom.isMentorIn());
        assertTrue(chatroom.isMenteeIn());
    }

    @Test
    void out_chatroom_by_mentee() {

        // given
        Chatroom chatroom = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee1)
                .build());
        chatroom.mentorEnter();
        chatroom.menteeEnter();

        // when
        chatService.outChatroom(new PrincipalDetails(menteeUser1, "ROLE_MENTEE"), chatroom.getId());
        // then
        assertTrue(chatroom.isMentorIn());
        assertFalse(chatroom.isMenteeIn());
    }

    @Test
    void accuse_chatroom() {

        // given
        Chatroom chatroom = chatroomRepository.save(Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee1)
                .build());

        // when
        chatService.accuseChatroom(mentorUser, chatroom.getId());
        // then
        assertThat(chatroom.getAccusedCount()).isEqualTo(1L);
    }
}