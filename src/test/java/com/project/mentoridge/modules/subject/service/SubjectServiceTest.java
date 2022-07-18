package com.project.mentoridge.modules.subject.service;

import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SubjectServiceTest {

    @InjectMocks
    SubjectServiceImpl subjectService;
    @Mock
    SubjectRepository subjectRepository;

    @Test
    void get_LearningKinds() {

        // given
        // when
        subjectService.getLearningKinds();
        // then
        verify(subjectRepository).findLearningKinds();
    }

    @Test
    void get_SubjectResponses() {

        // given
        // when
        subjectService.getSubjectResponses();
        // then
        verify(subjectRepository).findAll();
    }

    @Test
    void get_SubjectResponses_by_learningKind() {

        // given
        // when
        subjectService.getSubjectResponses(LearningKindType.IT);
        // then
        verify(subjectRepository).findAllByLearningKind(LearningKindType.IT);
    }

}