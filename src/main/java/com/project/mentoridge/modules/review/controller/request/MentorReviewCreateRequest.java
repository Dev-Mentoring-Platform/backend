package com.project.mentoridge.modules.review.controller.request;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.lecture.vo.Lecture;
import com.project.mentoridge.modules.review.vo.Review;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorReviewCreateRequest {

    @NotBlank
    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private MentorReviewCreateRequest(String content) {
        this.content = content;
    }

    public Review toEntity(User user, Lecture lecture, Review parent) {
        return Review.builder()
                .score(null)
                .content(content)
                .user(user)
                .lecture(lecture)
                .enrollment(null)
                .parent(parent)
                .build();
    }

}
