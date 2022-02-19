package com.project.mentoridge.modules.subject.service;

import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.controller.response.SubjectResponse;

import java.util.List;

public interface SubjectService {

    List<LearningKindType> getLearningKinds();

    List<SubjectResponse> getSubjectResponses();

    List<SubjectResponse> getSubjectResponses(LearningKindType learningKind);
}
