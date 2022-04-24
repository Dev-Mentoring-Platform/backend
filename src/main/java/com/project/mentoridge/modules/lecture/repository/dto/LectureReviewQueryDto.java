package com.project.mentoridge.modules.lecture.repository.dto;

import lombok.Data;

@Data
public class LectureReviewQueryDto {

    private Long lectureId;
    private Long lecturePriceId;
    private long reviewCount;       // 리뷰 총 개수
    private double scoreAverage;    // 강의 평점

    public LectureReviewQueryDto(Long lectureId, Long lecturePriceId, long reviewCount, double scoreAverage) {
        this.lectureId = lectureId;
        this.lecturePriceId = lecturePriceId;
        this.reviewCount = reviewCount;
        this.scoreAverage = scoreAverage;
    }
}
