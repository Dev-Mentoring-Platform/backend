package com.project.mentoridge.modules.subject.service;

import com.project.mentoridge.modules.subject.controller.response.LearningKindResponse;
import com.project.mentoridge.modules.subject.controller.response.SubjectResponse;

import java.util.List;

public interface SubjectService {

    List<LearningKindResponse> getLearningKindResponses();

    List<SubjectResponse> getSubjectResponses();

    List<SubjectResponse> getSubjectResponses(Long learningKindId);
}
