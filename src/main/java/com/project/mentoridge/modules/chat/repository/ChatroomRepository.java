package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    Optional<Chatroom> findByMenteeAndId(Mentee mentee, Long chatroomId);
    Optional<Chatroom> findByMentorAndId(Mentor mentor, Long chatroomId);

    List<Chatroom> findByMentor(Mentor mentor);
    Page<Chatroom> findByMentor(Mentor mentor, Pageable pageable);
    List<Chatroom> findByMentee(Mentee mentee);
    Page<Chatroom> findByMentee(Mentee mentee, Pageable pageable);
    Optional<Chatroom> findByMentorAndMentee(Mentor mentor, Mentee mentee);
}
