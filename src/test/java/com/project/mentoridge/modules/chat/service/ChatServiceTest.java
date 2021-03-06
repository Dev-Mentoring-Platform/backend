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
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.List;
import java.util.Optional;

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

    @Test
    void get_ChatroomResponses_of_mentee() {

        // given
        // when
        User user = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        PrincipalDetails principalDetails = new PrincipalDetails(user, "ROLE_MENTEE");

        chatService.getChatroomResponses(principalDetails);

        // then
        verify(chatroomQueryRepository).findByMenteeOrderByIdDesc(mentee);
    }

    @Test
    void get_get_ChatroomResponses_of_mentor() {

        // given
        // when
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        PrincipalDetails principalDetails = new PrincipalDetails(user, "ROLE_MENTOR");

        chatService.getChatroomResponses(principalDetails);

        // then
        verify(chatroomQueryRepository).findByMentorOrderByIdDesc(mentor);
    }

    @Test
    void get_paged_ChatroomResponses_of_mentee() {

        // given
        // when
        User user = mock(User.class);
        Mentee mentee = mock(Mentee.class);
        PrincipalDetails principalDetails = new PrincipalDetails(user, "ROLE_MENTEE");

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
        // when
        User user = mock(User.class);
        Mentor mentor = mock(Mentor.class);
        PrincipalDetails principalDetails = new PrincipalDetails(user, "ROLE_MENTOR");

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
        // when
        chatService.getChatMessagesOfChatroom(1L, 1);

        // then
        verify(messageRepository).findByChatroom(eq(chatroom), any(Pageable.class));
    }

    @Test
    void create_chatroom_by_mentor_when_already_exists() {

        // given
        Mentor mentor = mock(Mentor.class);
        User mentorUser = mock(User.class);
        when(mentor.getUser()).thenReturn(mentorUser);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Mentee mentee = mock(Mentee.class);
        User menteeUser = mock(User.class);
        when(mentee.getUser()).thenReturn(menteeUser);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

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
        Mentor mentor = mock(Mentor.class);
        User mentorUser = mock(User.class);
        when(mentor.getUser()).thenReturn(mentorUser);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Mentee mentee = mock(Mentee.class);
/*
        User menteeUser = mock(User.class);
        when(mentee.getUser()).thenReturn(menteeUser);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);*/
        when(menteeRepository.findById(1L)).thenReturn(Optional.of(mentee));
        // when
        Long chatroomId = chatService.createChatroomByMentor(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), 1L);

        // then
        verify(chatroomRepository).save(any(Chatroom.class));
        verify(chatroomLogService).insert(eq(mentorUser), any(Chatroom.class));
    }

    @Test
    void create_chatroom_by_mentee_when_already_exists() {

        // given
        Mentor mentor = mock(Mentor.class);
        User mentorUser = mock(User.class);
        when(mentor.getUser()).thenReturn(mentorUser);

        Mentee mentee = mock(Mentee.class);
        User menteeUser = mock(User.class);
        when(mentee.getUser()).thenReturn(menteeUser);

        Chatroom chatroom = mock(Chatroom.class);
        when(chatroom.getId()).thenReturn(1L);
        when(chatroomRepository.findByMentorAndMentee(mentor, mentee)).thenReturn(Optional.of(chatroom));

        // when
        Long chatroomId = chatService.createChatroomByMentor(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), 1L);

        // then
        assertThat(chatroomId).isEqualTo(1L);

        verify(chatroomRepository, atMost(0)).save(chatroom);
        verify(chatroomLogService, atMost(0)).insert(menteeUser, chatroom);
    }

    @Test
    void create_chatroom_by_mentee() {

        // given
        Mentor mentor = mock(Mentor.class);
        User mentorUser = mock(User.class);
        when(mentor.getUser()).thenReturn(mentorUser);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Mentee mentee = mock(Mentee.class);
        User menteeUser = mock(User.class);
        when(mentee.getUser()).thenReturn(menteeUser);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        // when
        chatService.createChatroomByMentee(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), 1L);

        // then
        verify(chatroomRepository).save(any(Chatroom.class));
        verify(chatroomLogService).insert(eq(menteeUser), any(Chatroom.class));
    }

    @Test
    void close_chatroom() {

        // given
        Mentor mentor = mock(Mentor.class);
        User mentorUser = mock(User.class);
        when(mentor.getUser()).thenReturn(mentorUser);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Mentee mentee = mock(Mentee.class);
        User menteeUser = mock(User.class);
        when(mentee.getUser()).thenReturn(menteeUser);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
        chatService.closeChatroom(mentorUser, 1L);

        // then
        verify(chatroom).close(mentorUser, chatroomLogService);
        assertThat(chatroom.isClosed()).isTrue();
        verify(chatroomLogService).close(eq(mentorUser), any(Chatroom.class), any(Chatroom.class));
    }

    @Test
    void send_message() {

        // given
        Mentor mentor = mock(Mentor.class);
        User mentorUser = mock(User.class);
        when(mentor.getUser()).thenReturn(mentorUser);

        Mentee mentee = mock(Mentee.class);
        User menteeUser = mock(User.class);
        when(mentee.getUser()).thenReturn(menteeUser);

        // Chatroom chatroom = mock(Chatroom.class);
        Chatroom chatroom = Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .build();
        when(chatroomRepository.findWithMentorUserAndMenteeUserById(1L)).thenReturn(Optional.of(chatroom));

        ChatMessage chatMessage = mock(ChatMessage.class);
        when(chatMessage.getChatroomId()).thenReturn(1L);
        when(chatMessage.toEntity(any(UserRepository.class), any(ChatroomRepository.class))).thenReturn(mock(Message.class));
        // when
        chatService.sendMessage(chatMessage);

        // then
        verify(messageRepository).save(any(Message.class));
        verify(notificationService).createNotification(any(Long.class), NotificationType.CHAT);
        verify(messageSendingTemplate).convertAndSend("/sub/chat/room/" + 1L, chatMessage);
    }

    @Test
    void enter_chatroom_by_mentor() {

        // given
        Mentor mentor = mock(Mentor.class);
        User mentorUser = mock(User.class);
        when(mentor.getUser()).thenReturn(mentorUser);

        Mentee mentee = mock(Mentee.class);
        User menteeUser = mock(User.class);
        when(mentee.getUser()).thenReturn(menteeUser);

        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
        chatService.enterChatroom(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), 1L);

        // then
        verify(chatroomMessageQueryRepository).updateAllChecked(mentorUser, 1L);
        verify(chatroom).mentorEnter();
        verify(messageSendingTemplate).convertAndSend("/sub/chat/room/" + 1L, any(ChatMessage.class));
    }

    @Test
    void enter_chatroom_by_mentee() {

        // given
        Mentor mentor = mock(Mentor.class);
        User mentorUser = mock(User.class);
        when(mentor.getUser()).thenReturn(mentorUser);

        Mentee mentee = mock(Mentee.class);
        User menteeUser = mock(User.class);
        when(mentee.getUser()).thenReturn(menteeUser);

        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
        chatService.enterChatroom(new PrincipalDetails(menteeUser, "ROLE_MENTEE"), 1L);

        // then
        verify(chatroomMessageQueryRepository).updateAllChecked(menteeUser, 1L);
        verify(chatroom).menteeEnter();
        verify(messageSendingTemplate).convertAndSend("/sub/chat/room/" + 1L, any(ChatMessage.class));
    }

    @Test
    void out_chatroom_by_mentor() {

        // given
        Mentor mentor = mock(Mentor.class);
        User mentorUser = mock(User.class);
        when(mentor.getUser()).thenReturn(mentorUser);
        when(mentorRepository.findByUser(mentorUser)).thenReturn(mentor);

        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
        chatService.outChatroom(new PrincipalDetails(mentorUser, "ROLE_MENTOR"), 1L);

        // then
        verify(chatroom).mentorOut();
    }

    @Test
    void out_chatroom_by_mentee() {

        // given
        Mentee mentee = mock(Mentee.class);
        User menteeUser = mock(User.class);
        when(mentee.getUser()).thenReturn(menteeUser);
        when(menteeRepository.findByUser(menteeUser)).thenReturn(mentee);

        Chatroom chatroom = mock(Chatroom.class);
        when(chatroomRepository.findById(1L)).thenReturn(Optional.of(chatroom));

        // when
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
        verify(chatroomLogService).accuse(eq(user), any(Chatroom.class), any(Chatroom.class));
    }

//    @DisplayName("?????? ??????")
//    @Test
//    void new_chatroom() {
//        // ??????(?????????)??? ???????????? ?????? ??????
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