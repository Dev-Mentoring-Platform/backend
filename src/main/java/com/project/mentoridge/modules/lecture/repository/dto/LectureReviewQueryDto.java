package com.project.mentoridge.modules.lecture.repository.dto;

import lombok.Data;

@Data
public class LectureReviewQueryDto {

    private Long lectureId;
    private int reviewCount;
    // private Integer reviewCount;    // 리뷰 총 개수
    private double scoreAverage;    // 강의 평점

    public LectureReviewQueryDto(Long lectureId, int reviewCount, double scoreAverage) {
        this.lectureId = lectureId;
        this.reviewCount = reviewCount;
        this.scoreAverage = scoreAverage;
    }
}
