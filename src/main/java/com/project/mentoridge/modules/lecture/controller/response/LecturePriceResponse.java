package com.project.mentoridge.modules.lecture.controller.response;

import com.project.mentoridge.modules.lecture.vo.LecturePrice;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LecturePriceResponse {

    private Long lecturePriceId;
    private Boolean isGroup;
    private Integer numberOfMembers;
    private Long pricePerHour;
    private Integer timePerLecture;
    private Integer numberOfLectures;
    private Long totalPrice;

    private String isGroupStr;
    private String content;

    // 강의 모집 종료 여부
    private Boolean closed;

    public LecturePriceResponse(LecturePrice lecturePrice) {
        this.lecturePriceId = lecturePrice.getId();
        this.isGroup = lecturePrice.getIsGroup();
        this.numberOfMembers = lecturePrice.getNumberOfMembers();
        this.pricePerHour = lecturePrice.getPricePerHour();
        this.timePerLecture = lecturePrice.getTimePerLecture();
        this.numberOfLectures = lecturePrice.getNumberOfLectures();
        this.totalPrice = lecturePrice.getTotalPrice();

        this.isGroupStr = lecturePrice.getIsGroup() ? "그룹강의" : "1:1 개인강의";
        this.content = String.format("시간당 %d원 x 1회 %d시간 x 총 %d회 수업 진행", this.pricePerHour, this.timePerLecture, this.numberOfLectures);

        this.closed = lecturePrice.isClosed();
    }
}
