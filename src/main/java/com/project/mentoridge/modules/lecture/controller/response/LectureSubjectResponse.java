package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.vo.LectureSubject;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LectureSubjectResponse {

    // private Long learningKindId;
    private String learningKind;
    private String krSubject;

    public LectureSubjectResponse(LectureSubject lectureSubject) {
        this.learningKind = lectureSubject.getSubject().getLearningKind().getName();
        this.krSubject = lectureSubject.getSubject().getKrSubject();
    }
}
