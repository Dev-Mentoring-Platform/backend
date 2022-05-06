package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.chat.vo._Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface _MessageRepository extends MongoRepository<_Message, String> {

    List<_Message> findAllByChatroomId(Long chatroomId);
    _Message findFirstByChatroomIdOrderByIdDesc(Long chatroomId);

    // Integer countAllByChatroomIdAndCheckedIsFalseAndSenderNicknameIsNot(Long chatroomId, String nickname);
    Integer countAllByChatroomIdAndCheckedIsFalseAndReceiverId(Long chatroomId, Long receiverId);
}
