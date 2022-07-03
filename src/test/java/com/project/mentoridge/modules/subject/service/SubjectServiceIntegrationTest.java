package com.project.mentoridge.modules.subject.service;

import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.controller.response.SubjectResponse;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import com.project.mentoridge.modules.subject.vo.Subject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class SubjectServiceIntegrationTest {

    @Autowired
    SubjectServiceImpl subjectService;
    @Autowired
    SubjectRepository subjectRepository;

    Subject subject1;
    Subject subject2;

    @BeforeAll
    void init() {

        subject1 = subjectRepository.save(Subject.builder()
                .subjectId(1L)
                .learningKind(LearningKindType.IT)
                .krSubject("백엔드")
                .build());
        subject2 = subjectRepository.save(Subject.builder()
                .subjectId(2L)
                .learningKind(LearningKindType.IT)
                .krSubject("프론트엔드")
                .build());
    }

    @Test
    void get_LearningKinds() {

        // given
        // when
        List<LearningKindType> learningKinds = subjectService.getLearningKinds();

        // then
        assertThat(learningKinds.size()).isEqualTo(1L);
        assertThat(learningKinds.get(0)).isEqualTo(LearningKindType.IT);
    }

    @Test
    void get_SubjectResponses() {

        // given
        // when
        List<SubjectResponse> subjectResponses = subjectService.getSubjectResponses();

        // then
        assertThat(subjectResponses.size()).isEqualTo(2L);
        assertThat(subjectResponses).containsAll(Arrays.asList(new SubjectResponse(subject1), new SubjectResponse(subject2)));
    }

    @Test
    void get_SubjectResponses_by_learningKind() {

        // given
        // when
        List<SubjectResponse> subjectResponses = subjectService.getSubjectResponses(LearningKindType.IT);

        // then
        assertThat(subjectResponses.size()).isEqualTo(2L);
        assertThat(subjectResponses).containsAll(Arrays.asList(new SubjectResponse(subject1), new SubjectResponse(subject2)));
    }

}