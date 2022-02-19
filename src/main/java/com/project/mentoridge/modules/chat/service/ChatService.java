package com.project.mentoridge.modules.chat.service;

import com.project.mentoridge.config.exception.EntityNotFoundException;
import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.chat.WebSocketHandler;
import com.project.mentoridge.modules.chat.repository.ChatroomRepository;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.project.mentoridge.config.exception.EntityNotFoundException.EntityType.CHATROOM;

@Transactional
@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatroomRepository chatroomRepository;

    // 강의 수강 시 채팅방 자동 생성
    public void createChatroom(Mentor mentor, Mentee mentee, Enrollment enrollment) {

        Chatroom chatroom = Chatroom.builder()
                .enrollment(enrollment)
                .mentor(mentor)
                .mentee(mentee)
                .build();
        chatroom = chatroomRepository.save(chatroom);
        // TODO - CHECK
        WebSocketHandler.chatroomMap.put(chatroom.getId(), new HashMap<>());
    }

    // 채팅방 삭제
    // - 강의 취소 시 채팅방 자동 삭제
    // - 강의 종료 시 채팅방 자동 삭제
    public void deleteChatroom(Enrollment enrollment) {

        Chatroom chatroom = chatroomRepository.findByEnrollment(enrollment)
                .orElseThrow(() -> new EntityNotFoundException(CHATROOM));
        Long chatroomId = chatroom.getId();

        chatroomRepository.deleteByEnrollment(enrollment);

        // TODO - 테스트
        // TODO - 웹소켓 세션 삭제
        Map<String, WebSocketSession> sessionMap = WebSocketHandler.chatroomMap.get(chatroomId);
        for (String key : sessionMap.keySet()) {
            WebSocketSession wss = sessionMap.get(key);
            try {
                wss.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        WebSocketHandler.chatroomMap.remove(sessionMap);
    }

    public void deleteChatroom(Long chatroomId) {

    }
}
