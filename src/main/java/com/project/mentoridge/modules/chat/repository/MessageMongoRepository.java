package com.project.mentoridge.modules.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Repository
public interface MessageMongoRepository extends MongoRepository<Message, String> {

    List<Message> findAllByChatroomId(Long chatroomId);
    Message findFirstByChatroomIdOrderByIdDesc(Long chatroomId);
}
