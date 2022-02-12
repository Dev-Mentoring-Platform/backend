package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.chat.vo.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findAllByChatroomId(Long chatroomId);
    Message findFirstByChatroomIdOrderByIdDesc(Long chatroomId);

    // Integer countAllByChatroomIdAndCheckedIsFalseAndSenderNicknameIsNot(Long chatroomId, String nickname);
    Integer countAllByChatroomIdAndCheckedIsFalseAndReceiverId(Long chatroomId, Long receiverId);
}
