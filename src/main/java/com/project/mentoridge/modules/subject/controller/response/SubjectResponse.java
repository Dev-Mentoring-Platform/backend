package com.project.mentoridge.modules.subject.controller.response;

import com.project.mentoridge.modules.subject.vo.Subject;
import lombok.Data;

@Data
public class SubjectResponse {

    Long learningKindId;
    String learningKind;
    String krSubject;

    public SubjectResponse(Subject subject) {
        this.learningKindId = subject.getLearningKind().getLearningKindId();
        this.learningKind = subject.getLearningKind().getLearningKind();
        this.krSubject = subject.getKrSubject();
    }
}
