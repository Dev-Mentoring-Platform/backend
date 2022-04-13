package com.project.mentoridge.modules.purchase.vo;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import lombok.*;

import javax.persistence.*;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "pick_id"))
@Getter //@Setter
@Entity
public class Pick extends BaseEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id",
                referencedColumnName = "mentee_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_PICK_MENTEE_ID"))
    private Mentee mentee;

    // 양방향
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id",
                referencedColumnName = "lecture_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_PICK_LECTURE_ID"))
    private Lecture lecture;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_price_id",
            referencedColumnName = "lecture_price_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_PICK_LECTURE_PRICE_ID"))
    private LecturePrice lecturePrice;

    @Builder(access = AccessLevel.PRIVATE)
    private Pick(Mentee mentee, Lecture lecture, LecturePrice lecturePrice) {
        this.mentee = mentee;
        this.lecture = lecture;
        this.lecturePrice = lecturePrice;
    }

    public void delete() {
        this.mentee.getPicks().remove(this);
    }

    public void setMentee(Mentee mentee) {
        this.mentee = mentee;
    }

    public static Pick buildPick(Mentee mentee, Lecture lecture, LecturePrice lecturePrice) {
        Pick pick = Pick.builder()
                .mentee(mentee)
                .lecture(lecture)
                .lecturePrice(lecturePrice)
                .build();
        mentee.addPick(pick);
        return pick;
    }
}
