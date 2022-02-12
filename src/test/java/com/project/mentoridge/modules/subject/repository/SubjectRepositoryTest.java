package com.project.mentoridge.modules.subject.repository;

import com.project.mentoridge.modules.lecture.embeddable.LearningKind;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
class SubjectRepositoryTest {

    @Autowired
    SubjectRepository subjectRepository;

//    @BeforeEach
//    void init() {
//
//        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.IT), "자바"));
//        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.IT), "파이썬"));
//        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.IT), "C/C++"));
//        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.LANGUAGE), "영어"));
//        subjectRepository.save(Subject.of(LearningKind.of(LearningKindType.LANGUAGE), "중국어"));
//    }

    @Test
    void getLearningKinds() {

        // given
        // when
        // then
        List<LearningKind> learningKinds = subjectRepository.findLearningKinds();
        learningKinds.stream().forEach(
                learningKind -> System.out.println(learningKind)
        );
    }

    @Test
    void findAllByLearningKindId() {

        // given
        LearningKind learningKind = subjectRepository.findLearningKinds().stream().findFirst()
                .orElseThrow(RuntimeException::new);
        Long learningKindId = learningKind.getLearningKindId();

        // when
        // then
        List<Subject> subjects = subjectRepository.findAllByLearningKindId(learningKindId);
        subjects.stream().forEach(
                subject -> System.out.println(subject)
        );
    }

}