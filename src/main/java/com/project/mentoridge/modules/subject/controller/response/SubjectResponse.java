package com.project.mentoridge.modules.subject.controller.response;

import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import com.project.mentoridge.modules.subject.vo.Subject;
import lombok.Data;

@Data
public class SubjectResponse {

//    Long learningKindId;
//    String learningKind;
    Long subjectId;
    LearningKindType learningKind;
    String krSubject;

    public SubjectResponse(Subject subject) {
        this.subjectId = subject.getId();
        this.learningKind = subject.getLearningKind();
        this.krSubject = subject.getKrSubject();
    }
}
