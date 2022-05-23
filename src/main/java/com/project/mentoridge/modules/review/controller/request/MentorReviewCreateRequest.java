package com.project.mentoridge.modules.review.controller.request;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.review.vo.MenteeReview;
import com.project.mentoridge.modules.review.vo.MentorReview;
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
/*
    public MentorReview toEntity(Mentor mentor, Lecture lecture, MenteeReview parent) {
        MentorReview child = MentorReview.builder()
                .content(content)
                .mentor(mentor)
                .lecture(lecture)
                .parent(parent)
                .build();
        parent.addChild(child);
        return child;
    }*/
    public MentorReview toEntity(Mentor mentor, MenteeReview parent) {
        MentorReview child = MentorReview.builder()
                .content(content)
                .mentor(mentor)
                .parent(parent)
                .build();
        parent.addChild(child);
        return child;
    }

}
