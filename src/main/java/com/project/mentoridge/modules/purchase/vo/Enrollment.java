package com.project.mentoridge.modules.purchase.vo;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import lombok.*;

import javax.persistence.*;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "enrollment_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter //@Setter
@Entity
public class Enrollment extends BaseEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id",
                referencedColumnName = "mentee_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_ENROLLMENT_MENTEE_ID"))
    private Mentee mentee;

    // TODO - CHECK : lecture 삭제 시
    // 단방향 -> 양방향
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id",
                referencedColumnName = "lecture_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_ENROLLMENT_LECTURE_ID"))
    private Lecture lecture;

    // 단방향
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_price_id",
            referencedColumnName = "lecture_price_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_ENROLLMENT_LECTURE_PRICE_ID"))
    private LecturePrice lecturePrice;

    // 2022.02.20 - 강의 등록 취소 X
//    @Column(nullable = false, columnDefinition = "boolean default false")
//    private boolean canceled = false;

    // TODO - 등록 확인
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean checked = false;

    @Builder(access = AccessLevel.PUBLIC)
    private Enrollment(Mentee mentee, Lecture lecture, LecturePrice lecturePrice) {
        this.mentee = mentee;
        this.lecture = lecture;
        this.lecturePrice = lecturePrice;
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public void setLecture(Lecture lecture) {
        this.lecture = lecture;
    }

//    @PreRemove
    public void delete() {
        this.mentee.getEnrollments().remove(this);
    }

    public static Enrollment buildEnrollment(Mentee mentee, Lecture lecture, LecturePrice lecturePrice) {

        Enrollment enrollment = Enrollment.builder()
                .mentee(mentee)
                .lecture(lecture)
                .lecturePrice(lecturePrice)
                .build();
        mentee.addEnrollment(enrollment);
        lecture.addEnrollment(enrollment);

        return enrollment;
    }
}
