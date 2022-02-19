package com.project.mentoridge.modules.review.controller.request;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.review.vo.Review;
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

    public Review toEntity(User user, Lecture lecture, Enrollment enrollment) {
        return Review.builder()
                .score(score)
                .content(content)
                .user(user)
                .lecture(lecture)
                .enrollment(enrollment)
                .parent(null)
                .build();
    }
}
