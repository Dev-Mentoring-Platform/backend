package com.project.mentoridge.modules.review.controller.response;

import lombok.Data;
import org.springframework.data.domain.Page;

@Data
public class ReviewListResponse {

    private double scoreAverage;
    private Page<ReviewWithSimpleLectureResponse> reviews;
    private long reviewCount;

    public ReviewListResponse(double scoreAverage, Page<ReviewWithSimpleLectureResponse> reviews, long reviewCount) {
        this.scoreAverage = scoreAverage;
        this.reviews = reviews;
        this.reviewCount = reviewCount;
    }
}
