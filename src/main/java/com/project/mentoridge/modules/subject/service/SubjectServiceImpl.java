package com.project.mentoridge.modules.subject.service;

import com.project.mentoridge.modules.subject.controller.response.LearningKindResponse;
import com.project.mentoridge.modules.subject.controller.response.SubjectResponse;
import com.project.mentoridge.modules.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    // @Cacheable("learningKinds")
    @Override
    public List<LearningKindResponse> getLearningKindResponses() {
        return subjectRepository.findLearningKinds().stream()
                .map(LearningKindResponse::new).collect(toList());
    }

    // @Cacheable("subjects")
    @Override
    public List<SubjectResponse> getSubjectResponses() {
        return subjectRepository.findAll().stream()
                .map(SubjectResponse::new).collect(toList());
    }

    // @Cacheable("subjectsByLearningKind")
    @Override
    public List<SubjectResponse> getSubjectResponses(Long learningKindId) {
        return subjectRepository.findAllByLearningKindId(learningKindId).stream()
                .map(SubjectResponse::new).collect(toList());
    }
}
