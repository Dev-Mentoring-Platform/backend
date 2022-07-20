package com.project.mentoridge.modules.chat.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.config.security.PrincipalDetails;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.repository.UserRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.chat.controller.ChatMessage;
import com.project.mentoridge.modules.chat.controller.response.ChatroomResponse;
import com.project.mentoridge.modules.chat.enums.MessageType;
import com.project.mentoridge.modules.chat.repository.*;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import com.project.mentoridge.modules.log.component.ChatroomLogService;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import com.project.mentoridge.modules.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.CHATROOM;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTEE;
import static com.project.mentoridge.modules.account.enums.RoleType.MENTOR;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class ChatService extends AbstractService {

    // public static final Map<Long, Map<String, WebSocketSession>> chatroomMap = new HashMap<>();

    private final ChatroomRepository chatroomRepository;
    private final ChatroomQueryRepository chatroomQueryRepository;
    private final ChatroomLogService chatroomLogService;
    private final MessageRepository messageRepository;
    private final MessageMongoRepository messageMongoRepository;
    private final ChatroomMessageQueryRepository chatroomMessageQueryRepository;

    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    private final SimpMessageSendingOperations messageSendingTemplate;
    private final NotificationService notificationService;

    @Transactional(readOnly = true)
    public List<ChatroomResponse> getChatroomResponses(PrincipalDetails principalDetails) {

        String role = principalDetails.getAuthority();
        User user = principalDetails.getUser();

        List<Chatroom> chatrooms = null;
        if (role.equals(MENTOR.getType())) {
            Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTOR));
            // chatrooms = chatroomRepository.findByMentorOrderByIdDesc(mentor);
            chatrooms = chatroomQueryRepository.findByMentorOrderByIdDesc(mentor);
        } else {
            Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTEE));
            // chatrooms = chatroomRepository.findByMenteeOrderByIdDesc(mentee);
            chatrooms = chatroomQueryRepository.findByMenteeOrderByIdDesc(mentee);
        }

        // List<Long> chatroomIds = chatrooms.stream().map(BaseEntity::getId).collect(Collectors.toList());
        List<ChatroomResponse> chatroomResponses = chatrooms.stream().map(ChatroomResponse::new).collect(Collectors.toList());
        // lastMessage - 마지막 메시지
/*        Map<Long, ChatMessage> lastMessages = chatroomMessageQueryRepository.findChatroomMessageQueryDtoMap(chatroomIds);
        Map<Long, Long> uncheckedMessageCounts = chatroomMessageQueryRepository.findChatroomMessageQueryDtoMap(user, chatroomIds);
        chatroomResponses.forEach(chatroomResponse -> {
            chatroomResponse.setLastMessage(lastMessages.get(chatroomResponse.getChatroomId()));
            // uncheckedMessageCounts - 안 읽은 메시지 개수
            Long uncheckedMessageCount = uncheckedMessageCounts.get(chatroomResponse.getChatroomId());
            if (uncheckedMessageCount == null) {
                uncheckedMessageCount = 0L;
            }
            chatroomResponse.setUncheckedMessageCount(uncheckedMessageCount);
        });*/

        return chatroomResponses;
    }

    @Transactional(readOnly = true)
    public Page<ChatroomResponse> getChatroomResponses(PrincipalDetails principalDetails, Integer page) {

        String role = principalDetails.getAuthority();
        User user = principalDetails.getUser();

        Page<Chatroom> chatrooms = null;
        if (role.equals(MENTOR.getType())) {
            Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTOR));
            // chatrooms = chatroomRepository.findByMentorOrderByIdDesc(mentor, getPageRequest(page));
            chatrooms = chatroomQueryRepository.findByMentorOrderByIdDesc(mentor, getPageRequest(page));
        } else {
            Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTEE));
            // chatrooms = chatroomRepository.findByMenteeOrderByIdDesc(mentee, getPageRequest(page));
            chatrooms = chatroomQueryRepository.findByMenteeOrderByIdDesc(mentee, getPageRequest(page));
        }
        List<Long> chatroomIds = chatrooms.stream().map(BaseEntity::getId).collect(Collectors.toList());
        Page<ChatroomResponse> chatroomResponses = chatrooms.map(ChatroomResponse::new);
        // lastMessage - 마지막 메시지
        Map<Long, ChatMessage> lastMessages = chatroomMessageQueryRepository.findChatroomMessageQueryDtoMap(chatroomIds);
        Map<Long, Long> uncheckedMessageCounts = chatroomMessageQueryRepository.findChatroomMessageQueryDtoMap(user, chatroomIds);
        chatroomResponses.forEach(chatroomResponse -> {
            chatroomResponse.setLastMessage(lastMessages.get(chatroomResponse.getChatroomId()));
            // uncheckedMessageCounts - 안 읽은 메시지 개수
            Long uncheckedMessageCount = uncheckedMessageCounts.get(chatroomResponse.getChatroomId());
            if (uncheckedMessageCount == null) {
                uncheckedMessageCount = 0L;
            }
            chatroomResponse.setUncheckedMessageCount(uncheckedMessageCount);
        });

        return chatroomResponses;
    }
    
    public Page<ChatMessage> getChatMessagesOfChatroom(Long chatroomId, Integer page) {

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));
        return messageRepository.findByChatroomOrderByIdDesc(chatroom, getPageRequest(page)).map(ChatMessage::new);
    }

    // 멘토가 채팅방 생성
    public Long createChatroomByMentor(PrincipalDetails principalDetails, Long menteeId) {
        return createChatroomByMentor(principalDetails.getAuthority(), principalDetails.getUser(), menteeId);
    }

    public Long createChatroomByMentor(String role, User mentorUser, Long menteeId) {

        if (!role.equals(MENTOR.getType())) {
            throw new UnauthorizedException(MENTOR);
        }
        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(mentorUser))
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTOR));
        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTEE));

        // 이미 존재하는 채팅방인 경우
        return chatroomRepository.findByMentorAndMentee(mentor, mentee)
                .map(BaseEntity::getId)
                .orElseGet(() -> {
                    Chatroom chatroom = Chatroom.builder()
                            .mentor(mentor)
                            .mentee(mentee)
                            .build();
                    chatroom = chatroomRepository.save(chatroom);
                    chatroomLogService.insert(mentorUser, chatroom);
                    return chatroom.getId();
                });
    }

    // 멘티가 채팅방 생성
    public Long createChatroomByMentee(PrincipalDetails principalDetails, Long mentorId) {
        return createChatroomByMentee(principalDetails.getAuthority(), principalDetails.getUser(), mentorId);
    }

    public Long createChatroomByMentee(String role, User menteeUser, Long mentorId) {

        if (!role.equals(MENTEE.getType())) {
            throw new UnauthorizedException(MENTEE);
        }
        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(menteeUser))
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTEE));
        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTOR));

        // 이미 존재하는 채팅방인 경우
        return chatroomRepository.findByMentorAndMentee(mentor, mentee)
                .map(BaseEntity::getId)
                .orElseGet(() -> {
                    Chatroom chatroom = Chatroom.builder()
                            .mentor(mentor)
                            .mentee(mentee)
                            .build();
                    chatroom = chatroomRepository.save(chatroom);
                    chatroomLogService.insert(menteeUser, chatroom);
                    return chatroom.getId();
                });
    }

    public void closeChatroom(User user, Long chatroomId) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));
        chatroom.close(user, chatroomLogService);
    }

    // TODO - 비동기 처리
    public void sendMessage(ChatMessage chatMessage) {

        Long chatroomId = chatMessage.getChatroomId();
        Long senderId = chatMessage.getSenderId();

        Chatroom chatroom = chatroomRepository.findWithMentorUserAndMenteeUserById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));
        User mentorUser = chatroom.getMentor().getUser();
        User menteeUser = chatroom.getMentee().getUser();
        if (mentorUser.getId().equals(senderId)) {
            if (chatroom.isMenteeIn()) {
                chatMessage.setChecked(true);
            }
        } else if (menteeUser.getId().equals(senderId)) {
            if (chatroom.isMentorIn()) {
                chatMessage.setChecked(true);
            }
        } else {
            throw new RuntimeException();
        }

        Message message = chatMessage.toEntity(userRepository, chatroomRepository);
        messageRepository.save(message);
        // messageMongoRepository.save(chatMessage.toDocument());
        notificationService.createNotification(chatMessage.getReceiverId(), NotificationType.CHAT);

        // topic - /sub/chat/room/{chatroom_id}로 메시지 send
        // 클라이언트는 해당 주소를 구독하고 있다가 메시지가 전달되면 화면에 출력
        // WebSocketHandler 대체
        messageSendingTemplate.convertAndSend("/sub/chat/room/" + chatroomId, chatMessage);
    }

    public void enterChatroom(PrincipalDetails principalDetails, Long chatroomId) {

        User user = principalDetails.getUser();
        chatroomMessageQueryRepository.updateAllChecked(user, chatroomId);

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));
        String role = principalDetails.getAuthority();
        if (role.equals(MENTOR.getType())) {
            chatroom.mentorEnter();
        } else {
            chatroom.menteeEnter();
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .type(MessageType.ENTER)
                .chatroomId(chatroomId)
                .senderId(user.getId())
                .build();
        messageSendingTemplate.convertAndSend("/sub/chat/room/" + chatroomId, chatMessage);
    }

    public void outChatroom(PrincipalDetails principalDetails, Long chatroomId) {

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));
        String role = principalDetails.getAuthority();
        if (role.equals(MENTOR.getType())) {
            chatroom.mentorOut();
        } else {
            chatroom.menteeOut();
        }
    }

    public void accuseChatroom(User user, Long chatroomId) {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));
        chatroom.accuse(user, chatroomLogService);
    }

}
