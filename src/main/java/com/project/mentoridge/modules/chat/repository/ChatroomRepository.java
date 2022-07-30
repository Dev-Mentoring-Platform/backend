package com.project.mentoridge.modules.chat.repository;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.chat.vo.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    Optional<Chatroom> findByMenteeAndId(Mentee mentee, Long chatroomId);
    Optional<Chatroom> findByMentorAndId(Mentor mentor, Long chatroomId);

    List<Chatroom> findByMentor(Mentor mentor);
    List<Chatroom> findByMentee(Mentee mentee);
//    List<Chatroom> findByMentorOrderByIdDesc(Mentor mentor);
//    Page<Chatroom> findByMentorOrderByIdDesc(Mentor mentor, Pageable pageable);
//    List<Chatroom> findByMenteeOrderByIdDesc(Mentee mentee);
//    Page<Chatroom> findByMenteeOrderByIdDesc(Mentee mentee, Pageable pageable);
    Optional<Chatroom> findByMentorAndMentee(Mentor mentor, Mentee mentee);

    @Query(value = "SELECT * FROM chatroom " +
            "WHERE mentee_id = (SELECT mentee_id FROM mentee WHERE user_id = :userId) OR mentor_id = (SELECT mentor_id FROM mentor WHERE user_id = :userId)", nativeQuery = true)
    List<Chatroom> findAllChatroomByUser(@Param("userId") Long userId);

    @Query(value = "select c.id from Chatroom c where c.mentor = :mentor")
    List<Long> findIdsByMentor(@Param("mentor") Mentor mentor);

    @Query(value = "select c.id from Chatroom c where c.mentee = :mentee")
    List<Long> findIdsByMentee(@Param("mentee") Mentee mentee);

    @Transactional
    @Modifying(flushAutomatically = true)
    @Query(value = "delete from Chatroom c where c.id in :chatroomIds")
    void deleteByIds(@Param("chatroomIds") List<Long> chatroomIds);

    @Transactional
    @Modifying
    void deleteByMentor(Mentor mentor);

    @Transactional
    @Modifying
    void deleteByMentee(Mentee mentee);

    @Query(value = "select c from Chatroom c join fetch c.mentee me join fetch me.user meu " +
            "join fetch c.mentor mo join fetch mo.user mou where c.id = :chatroomId")
    Optional<Chatroom> findWithMentorUserAndMenteeUserById(@Param("chatroomId") Long chatroomId);
}
