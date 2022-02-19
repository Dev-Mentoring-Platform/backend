package com.project.mentoridge.modules.lecture.vo;

import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.subject.vo.Subject;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PUBLIC;

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

//    @Embedded
//    private LearningKind learningKind;
//    @Column(length = 50, nullable = false)
//    private String krSubject;
    @OneToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "subject_id",
            referencedColumnName = "subject_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_LECTURE_SUBJECT_SUBJECT_ID"))
    private Subject subject;

    public void mappingLecture(Lecture lecture) {
        this.lecture = lecture;
    }

    // TODO - Enum Converter

    @Builder(access = PUBLIC)
    private LectureSubject(Lecture lecture, Subject subject) {
        this.lecture = lecture;
        this.subject = subject;
    }
}