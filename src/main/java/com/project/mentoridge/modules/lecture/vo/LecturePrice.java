package com.project.mentoridge.modules.lecture.vo;

import com.project.mentoridge.modules.base.BaseEntity;
import lombok.*;

import javax.persistence.*;

import static lombok.AccessLevel.*;

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

    private Integer numberOfMembers;

    // 시간당 가격
    @Column(nullable = false)
    private Long pricePerHour;

    // 1회당 강의 시간
    @Column(nullable = false)
    private Integer timePerLecture;

    // 강의 횟수
    @Column(nullable = false)
    private Integer numberOfLectures;

    // TODO - 할인 정책 도입 시
    // 최종 수강료
    @Column(nullable = false)
    private Long totalPrice;

    public void mappingLecture(Lecture lecture) {
        this.lecture = lecture;
    };

    @Builder(access = PUBLIC)
    private LecturePrice(Lecture lecture, Boolean isGroup,
                         Integer numberOfMembers, Long pricePerHour, Integer timePerLecture, Integer numberOfLectures, Long totalPrice) {
        this.lecture = lecture;
        this.isGroup = isGroup;
        this.numberOfMembers = numberOfMembers;
        this.pricePerHour = pricePerHour;
        this.timePerLecture = timePerLecture;
        this.numberOfLectures = numberOfLectures;
        this.totalPrice = totalPrice;
    }
/*
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
    }*/
}
