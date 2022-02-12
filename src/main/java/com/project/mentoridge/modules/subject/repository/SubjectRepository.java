package com.project.mentoridge.modules.subject.repository;

import com.project.mentoridge.modules.lecture.embeddable.LearningKind;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query(value = "select distinct s.learningKind from Subject s")
    List<LearningKind> findLearningKinds();

//    @Query(value = "select * from subject where learning_kind_id = :learning_kind_id", nativeQuery = true)
//    List<Subject> findAllByLearningKindId(@Param("learning_kind_id") Long learningKindId);
    @Query(value = "select s from Subject s where s.learningKind.learningKindId = :learningKindId")
    List<Subject> findAllByLearningKindId(Long learningKindId);

}
