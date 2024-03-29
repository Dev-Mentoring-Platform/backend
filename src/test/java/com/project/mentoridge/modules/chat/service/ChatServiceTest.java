package com.project.mentoridge.modules.chat.service;

import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.chat.controller.ChatMessage;
import com.project.mentoridge.modules.chat.repository.ChatroomMessageQueryRepository;
import com.project.mentoridge.modules.chat.repository.ChatroomQueryRepository;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import com.project.mentoridge.modules.log.component.ChatroomLogService;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.Optional;

import static com.project.mentoridge.modules.base.TestDataBuilder.getUserWithName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    ChatService chatService;

    @Mock
    ChatroomRepository chatroomRepository;
    @Mock
    ChatroomQueryRepository chatroomQueryRepository;
    @Mock
    ChatroomLogService chatroomLogService;
    @Mock
    MessageRepository messageRepository;
    @Mock
    ChatroomMessageQueryRepository chatroomMessageQueryRepository;
    @Mock
    MenteeRepository menteeRepository;
    @Mock
    MentorRepository mentorRepository;
    @Mock
    UserRepository userRepository;

    @Mock
    SimpMessageSendingOperations messageSendingTemplate;
    @Mock
    NotificationService notificationService;
/*

    @Test
    void get_ChatroomResponses_of_mentee() {

        // given
        User menteeUser = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        PrincipalDetails principalDetails = new PrincipalDetails(menteeUser, "ROLE_MENTEE");
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        // when
        chatService.getChatroomResponses(principalDetails);

        // then
        verify(chatroomQueryRepository).findByMenteeOrderByIdDesc(mentee);
    }

    @Test
    void get_ChatroomResponses_of_mentor() {

        // given

        User mentorUser = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        PrincipalDetails principalDetails = new PrincipalDetails(mentorUser, "ROLE_MENTOR");
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        // when
        chatService.getChatroomResponses(principalDetails);

        // then
        verify(chatroomQueryRepository).findByMentorOrderByIdDesc(mentor);
    }

    @Test
    void get_paged_ChatroomResponses_of_mentee() {

        // given
        User user = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        PrincipalDetails principalDetails = new PrincipalDetails(user, "ROLE_MENTEE");
        when(menteeRepository.findByUser(user)).thenReturn(mentee);

        // when
        chatService.getChatroomResponses(principalDetails, 1);

        // then
        verify(chatroomQueryRepository).findByMenteeOrderByIdDesc(eq(mentee), any(Pageable.class));
        // lastMessage
        verify(chatroomMessageQueryRepository).findChatroomMessageQueryDtoMap(any(List.class));
        // uncheckedMessageCounts
        verify(chatroomMessageQueryRepository).findChatroomMessageQueryDtoMap(user, any(List.class));
    }

    @Test
    void get_paged_ChatroomResponses_of_mentor() {

        // given
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        PrincipalDetails principalDetails = new PrincipalDetails(user, "ROLE_MENTOR");
        when(mentorRepository.findByUser(user)).thenReturn(mentor);

        // when
        chatService.getChatroomResponses(principalDetails, 1);

        // then
        verify(chatroomQueryRepository).findByMentorOrderByIdDesc(eq(mentor), any(Pageable.class));
        // lastMessage
        verify(chatroomMessageQueryRepository).findChatroomMessageQueryDtoMap(any(List.class));
        // uncheckedMessageCounts
        verify(chatroomMessageQueryRepository).findChatroomMessageQueryDtoMap(user, any(List.class));
    }

    @Test
    void get_paged_ChatMessages() {

        // given
        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));
        when(messageRepository.findByChatroom(chatroom, any(Pageable.class))).thenReturn(Page.empty());

        // when
        Page<ChatMessage> response = chatService.getChatMessagesOfChatroom(1L, 1);

        // then
        assertThat(response.getContent()).hasSize(0);
    }
*/

    @Test
    void create_chatroom_by_mentor_when_already_exists() {

        // given
        User mentorUser = getUserWithName("mentorUser");
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .build();
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);
        User menteeUser = getUserWithName("menteeUser");
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();
        when(menteeRepository.findById(1L)).thenReturn(Optional.of(mentee));

        Chatroom chatroom = mock(Chatroom.class);
        when(chatroom.getId()).thenReturn(1L);
        when(chatroomRepository.findByMentorAndMentee(mentor, mentee)).thenReturn(Optional.of(chatroom));

        // when
        Long chatroomId = chatService.createChatroomByMentor(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), 1L);

        // then
        assertThat(chatroomId).isEqualTo(1L);
        verify(chatroomRepository, atMost(0)).save(chatroom);
        verify(chatroomLogService, atMost(0)).insert(mentorUser, chatroom);
    }

    @Test
    void create_chatroom_by_mentor() {

        // given
        User mentorUser = getUserWithName("mentorUser");
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .build();
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);
        User menteeUser = getUserWithName("menteeUser");
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();
        when(menteeRepository.findById(1L)).thenReturn(Optional.of(mentee));

        when(chatroomRepository.findByMentorAndMentee(mentor, mentee)).thenReturn(Optional.empty());
//        Chatroom saved = Chatroom.builder()
//                .mentor(mentor)
//                .mentee(mentee)
//                .build();
        Chatroom saved = mock(Chatroom.class);
        when(saved.getId()).thenReturn(1L);
        when(chatroomRepository.save(any(Chatroom.class))).thenReturn(saved);

        // when
        Long chatroomId = chatService.createChatroomByMentor(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), 1L);

        // then
        verify(chatroomRepository).save(any(Chatroom.class));
        verify(chatroomLogService).insert(mentorUser, saved);
        assertThat(chatroomId).isEqualTo(1L);
    }

    @Test
    void create_chatroom_by_mentee_when_already_exists() {

        // given
        User mentorUser = getUserWithName("mentorUser");
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .build();
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        User menteeUser = getUserWithName("menteeUser");
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        Chatroom chatroom = mock(Chatroom.class);
        when(chatroom.getId()).thenReturn(1L);
        when(chatroomRepository.findByMentorAndMentee(mentor, mentee)).thenReturn(Optional.of(chatroom));

        // when
        Long chatroomId = chatService.createChatroomByMentee(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), 1L);

        // then
        assertThat(chatroomId).isEqualTo(1L);
        verify(chatroomRepository, atMost(0)).save(chatroom);
        verify(chatroomLogService, atMost(0)).insert(menteeUser, chatroom);
    }

    @Test
    void create_chatroom_by_mentee() {

        // given
        User mentorUser = getUserWithName("mentorUser");
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .build();
        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
        User menteeUser = getUserWithName("menteeUser");
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        when(chatroomRepository.findByMentorAndMentee(mentor, mentee)).thenReturn(Optional.empty());
//        Chatroom saved = Chatroom.builder()
//                .mentor(mentor)
//                .mentee(mentee)
//                .build();
        Chatroom saved = mock(Chatroom.class);
        when(saved.getId()).thenReturn(1L);
        when(chatroomRepository.save(any(Chatroom.class))).thenReturn(saved);

        // when
        Long chatroomId = chatService.createChatroomByMentee(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), 1L);

        // then
        verify(chatroomRepository).save(any(Chatroom.class));
        verify(chatroomLogService).insert(menteeUser, saved);
        assertThat(chatroomId).isEqualTo(1L);
    }

    @Test
    void close_chatroom() {

        // given
        User mentorUser = getUserWithName("mentorUser");
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .build();
        User menteeUser = getUserWithName("menteeUser");
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();
        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
        chatService.closeChatroom(mentorUser, 1L);

        // then
        verify(chatroom).close(mentorUser, chatroomLogService);
        // verify(chatroomLogService).close(eq(mentorUser), any(Chatroom.class), any(Chatroom.class));
    }

    @Test
    void send_message_by_menteeUser() {

        // given
        User mentorUser = mock(User.class);
        when(mentorUser.getId()).thenReturn(1L);
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .build();
        User menteeUser = mock(User.class);
        when(menteeUser.getId()).thenReturn(2L);
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();

        Chatroom chatroom = mock(Chatroom.class);
        when(chatroom.getMentee()).thenReturn(mentee);
        when(chatroom.getMentor()).thenReturn(mentor);
        when(chatroom.isMentorIn()).thenReturn(true);
        when(chatroomRepository.findWithMentorUserAndMenteeUserById(1L)).thenReturn(Optional.of(chatroom));

        ChatMessage chatMessage = mock(ChatMessage.class);
        when(chatMessage.getChatroomId()).thenReturn(1L);
        when(chatMessage.getSenderId()).thenReturn(2L);
        when(chatMessage.toEntity(any(UserRepository.class), any(ChatroomRepository.class))).thenReturn(mock(Message.class));
        // when
        chatService.sendMessage(chatMessage);

        // then
        verify(chatMessage).setChecked(true);
        verify(messageRepository).save(any(Message.class));
        verify(notificationService).createNotification(any(Long.class), eq(NotificationType.CHAT));
        verify(messageSendingTemplate).convertAndSend("/sub/chat/room/" + 1L, chatMessage);
    }

    @Test
    void enter_chatroom_by_mentor() {

        // given
        User mentorUser = getUserWithName("mentorUser");
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .build();
        User menteeUser = getUserWithName("menteeUser");
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();
        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
        chatService.enterChatroom(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), 1L);

        // then
        verify(chatroomMessageQueryRepository).updateAllChecked(mentorUser, 1L);
        verify(chatroom).mentorEnter();
        verify(messageSendingTemplate).convertAndSend(eq("/sub/chat/room/" + 1L), any(ChatMessage.class));
    }

    @Test
    void enter_chatroom_by_mentee() {

        // given
        User mentorUser = getUserWithName("mentorUser");
        Mentor mentor = Mentor.builder()
                .user(mentorUser)
                .build();
        User menteeUser = getUserWithName("menteeUser");
        Mentee mentee = Mentee.builder()
                .user(menteeUser)
                .build();
        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
        chatService.enterChatroom(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), 1L);

        // then
        verify(chatroomMessageQueryRepository).updateAllChecked(menteeUser, 1L);
        verify(chatroom).menteeEnter();
        verify(messageSendingTemplate).convertAndSend(eq("/sub/chat/room/" + 1L), any(ChatMessage.class));
    }

    @Test
    void out_chatroom_by_mentor() {

        // given
        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
        User mentorUser = mock(User.class);
        chatService.outChatroom(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), 1L);

        // then
        verify(chatroom).mentorOut();
    }

    @Test
    void out_chatroom_by_mentee() {

        // given
        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
        User menteeUser = mock(User.class);
        chatService.outChatroom(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), 1L);

        // then
        verify(chatroom).menteeOut();
    }

    @Test
    void accuse_chatroom() {

        // given
        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
        User user = mock(User.class);
        chatService.accuseChatroom(user, 1L);

        // then
        verify(chatroom).accuse(user, chatroomLogService);
        // verify(chatroomLogService).accuse(eq(user), any(Chatroom.class), any(Chatroom.class));
    }

//    @DisplayName("채팅 신청")
//    @Test
//    void new_chatroom() {
//        // 멘티(로그인)가 멘토에게 채팅 신청
//        // given
//        User menteeUser = getUserWithNameAndRole("mentee", RoleType.MENTEE);
//        Mentee mentee = Mentee.builder()
//                .user(menteeUser)
//                .build();
//        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);
//        User mentorUser = getUserWithNameAndRole("mentor", RoleType.MENTOR);
//        Mentor mentor = Mentor.builder()
//                .user(mentorUser)
//                .bio("hello")
//                .build();
//        when(mentorRepository.findById(1L)).thenReturn(Optional.of(mentor));
//
//        Chatroom chatroom = mock(Chatroom.class);
//        when(chatroomRepository.save(any(Chatroom.class))).thenReturn(chatroom);
//
//        // when
//        chatService.createChatroomByMentee(menteeUser, 1L);
//        // then
//        verify(chatroomRepository).save(any(Chatroom.class));
//        // assertThat(WebSocketHandler.chatroomMap.get(1L)).isNotNull();
//    }

}