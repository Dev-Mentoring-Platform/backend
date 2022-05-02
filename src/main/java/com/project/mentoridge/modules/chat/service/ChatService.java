package com.project.mentoridge.modules.chat.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.config.exception.UnauthorizedException;
import com.project.mentoridge.modules.account.repository.MenteeRepository;
import com.project.mentoridge.modules.account.repository.MentorRepository;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.repository.MessageRepository;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.chat.vo.Message;
import com.project.mentoridge.modules.log.component.ChatroomLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
    private final MessageRepository messageRepository;
    private final ChatroomLogService chatroomLogService;

    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;


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

    public void accuseChatroom(User user, Long chatroomId) {

    }

    public void closeChatroom(User user, Long chatroomId) {

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));

        // chatroomRepository.delete(chatroom);
        chatroom.close();
        chatroomLogService.delete(user, chatroom);
    }

    @Transactional(readOnly = true)
    public List<Message> getMessages() {
        return messageRepository.findAll();
    }

    public void sendMessage(Message message) {
        messageRepository.save(message);
    }
}
