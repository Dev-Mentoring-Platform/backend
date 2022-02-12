package com.project.mentoridge.modules.subject.vo;

import com.project.mentoridge.modules.lecture.embeddable.LearningKind;
import lombok.*;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter //@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor(access = PROTECTED)
@Table(name = "subject"
        //, indexes = @Index(name = "IDX_SUBJECT", columnList = "learningKindId, learningKind, krSubject", unique = true)
)
@Entity
public class Subject {

    @GeneratedValue(strategy = IDENTITY)
    @Id
    private Long id;

//    @Column(length = 50, nullable = false)
//    private Long learningKindId;
//    @Column(length = 50, nullable = false)
//    private String learningKind;
    @Embedded
    private LearningKind learningKind;

    @Column(length = 50, nullable = false)
    private String krSubject;

    @Builder(access = PRIVATE)
    private Subject(LearningKind learningKind, String krSubject) {
        this.learningKind = learningKind;
        this.krSubject = krSubject;
    }

    public static Subject of(LearningKind learningKind, String krSubject) {
        return Subject.builder()
                .learningKind(learningKind)
                .krSubject(krSubject)
                .build();
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
