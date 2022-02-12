package com.project.mentoridge.modules.lecture.embeddable;

import com.project.mentoridge.modules.lecture.enums.LearningKindType;
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

    @Builder(access = AccessLevel.PRIVATE)
    private LearningKind(Long learningKindId, String learningKind) {
        this.learningKindId = learningKindId;
        this.learningKind = learningKind;
    }

    public static LearningKind of(Long learningKindId, String learningKind) {
        return LearningKind.builder()
                .learningKind(learningKind)
                .learningKindId(learningKindId)
                .build();
    }

    public static LearningKind of(LearningKindType type) {
        return LearningKind.builder()
                .learningKind(type.getName())
                .learningKindId(type.getId())
                .build();
    }

    @Override
    public String toString() {
        return "LearningKind{" +
                "learningKindId=" + learningKindId +
                ", learningKind='" + learningKind + '\'' +
                '}';
    }
}
