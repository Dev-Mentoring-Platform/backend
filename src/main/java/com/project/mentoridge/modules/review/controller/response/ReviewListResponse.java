package com.project.mentoridge.modules.review.controller.response;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class ReviewListResponse {

    private Double scoreAverage;
    private Page<ReviewWithSimpleEachLectureResponse> reviews;
    private Long reviewCount;

    public ReviewListResponse(double scoreAverage, Page<ReviewWithSimpleEachLectureResponse> reviews, long reviewCount) {
        this.scoreAverage = scoreAverage;
        this.reviews = reviews;
        this.reviewCount = reviewCount;
    }
}
