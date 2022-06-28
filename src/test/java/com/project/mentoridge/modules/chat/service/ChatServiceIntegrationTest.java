package com.project.mentoridge.modules.chat.service;

import com.project.mentoridge.config.security.PrincipalDetails;
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
import com.project.mentoridge.modules.chat.controller.response.ChatroomResponse;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.mentoridge.modules.account.controller.IntegrationTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Transactional
@SpringBootTest
class ChatServiceIntegrationTest {

    @Autowired
    ChatService chatService;
    @Autowired
    ChatroomRepository chatroomRepository;
//    @Autowired
//    MessageMongoRepository messageRepository;

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
        menteeUser2.generateEmailVerifyToken();
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
                () -> assertThat(_ofMenteeUser2.getMentorId()).isEqualTo(chatroom2.getId()),
                () -> assertThat(_ofMenteeUser2.getMentorUserId()).isEqualTo(chatroom2.getId()),
                () -> assertThat(_ofMenteeUser2.getMentorNickname()).isEqualTo(chatroom2.getId()),
                () -> assertThat(_ofMenteeUser2.getMentorImage()).isEqualTo(chatroom2.getId()),
                () -> assertThat(_ofMenteeUser2.getMenteeId()).isEqualTo(chatroom2.getId()),
                () -> assertThat(_ofMenteeUser2.getMenteeUserId()).isEqualTo(chatroom2.getId()),
                () -> assertThat(_ofMenteeUser2.getMenteeNickname()).isEqualTo(chatroom2.getId()),
                () -> assertThat(_ofMenteeUser2.getMenteeImage()).isEqualTo(chatroom2.getId())
        );
    }

    @Test
    void get_paged_ChatroomResponses() {

    }

    @Test
    void get_paged_ChatMessages() {

    }

    @Test
    void create_chatroom_by_mentor() {

    }

    @Test
    void create_chatroom_by_mentee() {

    }

    @Test
    void close_chatroom() {

    }

    @Test
    void send_message() {

    }

    @Test
    void enter_chatroom() {

    }

    @Test
    void out_chatroom() {

    }

    @Test
    void accuse_chatroom() {

    }
}