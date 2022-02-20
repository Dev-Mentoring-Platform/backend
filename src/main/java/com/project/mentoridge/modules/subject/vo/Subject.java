package com.project.mentoridge.modules.subject.vo;

import com.project.mentoridge.modules.lecture.enums.LearningKindType;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static lombok.AccessLevel.PUBLIC;

@Getter //@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
@Table(name = "subject"
        //, indexes = @Index(name = "IDX_SUBJECT", columnList = "learningKindId, learningKind, krSubject", unique = true)
)
@Entity
public class Subject {

    @Id //@GeneratedValue(strategy = IDENTITY)
    @Column(name = "subject_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private LearningKindType learningKind;
//    @Embedded
//    private LearningKind learningKind;

    @Column(length = 50, nullable = false)
    private String krSubject;

    @Builder(access = PUBLIC)
    private Subject(Long subjectId, LearningKindType learningKind, String krSubject) {
        this.id = subjectId;
        this.learningKind = learningKind;
        this.krSubject = krSubject;
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", learningKind=" + learningKind +
                ", krSubject='" + krSubject + '\'' +
                '}';
    }
}
