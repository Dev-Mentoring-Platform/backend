package com.project.mentoridge.modules.lecture.embeddable;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class LearningKind {

    private Long learningKindId;
    private String learningKind;

    @Builder(access = AccessLevel.PUBLIC)
    private LearningKind(Long learningKindId, String learningKind) {
        this.learningKindId = learningKindId;
        this.learningKind = learningKind;
    }

    @Override
    public String toString() {
        return "LearningKind{" +
                "learningKindId=" + learningKindId +
                ", learningKind='" + learningKind + '\'' +
                '}';
    }
}
