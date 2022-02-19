package com.project.mentoridge.modules.subject.repository;

import com.project.mentoridge.modules.lecture.embeddable.LearningKind;
import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    @Query(value = "select distinct s.learningKind from Subject s")
    List<LearningKindType> findLearningKinds();

    @Query(value = "select s from Subject s where s.learningKind = :learningKind")
    List<Subject> findAllByLearningKind(LearningKindType learningKind);

}
