package com.project.mentoridge.modules.review.controller.request;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.vo.MenteeReview;
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
public class MenteeReviewCreateRequest {

    @Min(0) @Max(5)
    @NotNull
    private Integer score;

    @NotBlank
    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private MenteeReviewCreateRequest(Integer score, String content) {
        this.score = score;
        this.content = content;
    }

    public MenteeReview toEntity(Mentee mentee, Lecture lecture, Enrollment enrollment) {
        return MenteeReview.builder()
                .score(score)
                .content(content)
                .mentee(mentee)
                .lecture(lecture)
                .enrollment(enrollment)
                .build();
    }
}
