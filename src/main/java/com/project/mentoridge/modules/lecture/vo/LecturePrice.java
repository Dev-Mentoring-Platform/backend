package com.project.mentoridge.modules.lecture.vo;

import com.project.mentoridge.modules.base.BaseEntity;
import lombok.*;

import javax.persistence.*;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "lecture_price_id"))
@Entity
@Table(name = "lecture_price")
public class LecturePrice extends BaseEntity {

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id",
                referencedColumnName = "lecture_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_LECTURE_PRICE_LECTURE_ID"))
    private Lecture lecture;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private Boolean isGroup;

    private Integer groupNumber;

    @Column(nullable = false)
    private Integer totalTime;

    @Column(nullable = false)
    private Integer pertimeLecture;

    @Column(nullable = false)
    private Long pertimeCost;

    @Column(nullable = false)
    private Long totalCost;

    public void mappingLecture(Lecture lecture) {
        this.lecture = lecture;
    };

    @Builder(access = PRIVATE)
    private LecturePrice(Lecture lecture, Boolean isGroup, Integer groupNumber, Integer totalTime, Integer pertimeLecture, Long pertimeCost, Long totalCost) {
        this.lecture = lecture;
        this.isGroup = isGroup;
        this.groupNumber = groupNumber;
        this.totalTime = totalTime;
        this.pertimeLecture = pertimeLecture;
        this.pertimeCost = pertimeCost;
        this.totalCost = totalCost;
    }

    public static LecturePrice of(Lecture lecture, Boolean isGroup, Integer groupNumber, Integer totalTime, Integer pertimeLecture, Long pertimeCost, Long totalCost) {
        return LecturePrice.builder()
                .lecture(lecture)
                .isGroup(isGroup)
                .groupNumber(groupNumber)
                .totalTime(totalTime)
                .pertimeLecture(pertimeLecture)
                .pertimeCost(pertimeCost)
                .totalCost(totalCost)
                .build();
    }
}
