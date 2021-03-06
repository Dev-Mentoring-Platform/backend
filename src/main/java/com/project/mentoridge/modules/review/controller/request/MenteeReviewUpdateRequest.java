package com.project.mentoridge.modules.review.controller.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenteeReviewUpdateRequest {

    @Min(0) @Max(5)
    @NotNull
    private Integer score;

    @NotBlank
    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private MenteeReviewUpdateRequest(Integer score, String content) {
        this.score = score;
        this.content = content;
    }

}
