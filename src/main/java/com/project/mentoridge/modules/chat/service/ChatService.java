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
import com.project.mentoridge.modules.chat.repository.ChatroomMessageQueryRepository;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import com.project.mentoridge.modules.log.component.ChatroomLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    private final ChatroomLogService chatroomLogService;
    private final MessageRepository messageRepository;
    private final ChatroomMessageQueryRepository chatroomMessageQueryRepository;

    private final UserRepository userRepository;
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    @Transactional(readOnly = true)
    public Page<ChatroomResponse> getChatroomResponses(PrincipalDetails principalDetails, Integer page) {

        String role = principalDetails.getAuthority();
        User user = principalDetails.getUser();

        Page<Chatroom> chatrooms = null;
        if (role.equals(MENTOR.getType())) {
            Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTOR));
            chatrooms = chatroomRepository.findByMentor(mentor, getPageRequest(page));
        } else {
            Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                    .orElseThrow(() -> new UnauthorizedException(MENTEE));
            chatrooms = chatroomRepository.findByMentee(mentee, getPageRequest(page));
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
    public void createChatroomByMentor(User user, Long menteeId) {

        Mentor mentor = Optional.ofNullable(mentorRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTOR));

        Mentee mentee = menteeRepository.findById(menteeId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTEE));

        Chatroom chatroom = Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .build();
        chatroom = chatroomRepository.save(chatroom);
        chatroomLogService.insert(user, chatroom);
    }

    // 멘티가 채팅방 생성
    public void createChatroomByMentee(User user, Long mentorId) {

        Mentee mentee = Optional.ofNullable(menteeRepository.findByUser(user))
                .orElseThrow(() -> new UnauthorizedException(MENTEE));

        Mentor mentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new EntityNotFoundException(EntityNotFoundException.EntityType.MENTOR));

        Chatroom chatroom = Chatroom.builder()
                .mentor(mentor)
                .mentee(mentee)
                .build();
        chatroom = chatroomRepository.save(chatroom);
        chatroomLogService.insert(user, chatroom);
    }

    public void closeChatroom(User user, Long chatroomId) {

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));

        // chatroomRepository.delete(chatroom);
        chatroom.close();
        chatroomLogService.delete(user, chatroom);
    }

    public void sendMessage(ChatMessage chatMessage) {
        Message message = chatMessage.toEntity(userRepository, chatroomRepository);
        messageRepository.save(message);
    }

    public void enterChatroom(User user, Long chatroomId) {
        chatroomMessageQueryRepository.updateAllChecked(user, chatroomId);
    }

    public void accuseChatroom(User user, Long chatroomId) {

    }

}
