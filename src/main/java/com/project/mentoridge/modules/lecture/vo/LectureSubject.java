package com.project.mentoridge.modules.lecture.vo;

import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.embeddable.LearningKind;
import lombok.*;

import javax.persistence.*;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = PROTECTED)
@Getter
@AttributeOverride(name = "id", column = @Column(name = "lecture_subject_id"))
@Entity
@Table(name = "lecture_subject")
public class LectureSubject extends BaseEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id",
                referencedColumnName = "lecture_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_LECTURE_SUBJECT_LECTURE_ID"))
    private Lecture lecture;

    @Embedded
    private LearningKind learningKind;

    @Column(length = 50, nullable = false)
    private String krSubject;

    public void mappingLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    // TODO - Enum Converter
    @Builder(access = PRIVATE)
    private LectureSubject(Lecture lecture, Long learningKindId, String learningKind, String krSubject) {
        this.lecture = lecture;
        this.learningKind = LearningKind.of(learningKindId, learningKind);
        this.krSubject = krSubject;
    }

    public static LectureSubject of(Lecture lecture, Long learningKindId, String learningKind, String krSubject) {
        return LectureSubject.builder()
                .lecture(lecture)
                .learningKindId(learningKindId)
                .learningKind(learningKind)
                .krSubject(krSubject)
                .build();
    }
}