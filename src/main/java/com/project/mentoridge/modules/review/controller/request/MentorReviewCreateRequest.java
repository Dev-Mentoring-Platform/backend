package com.project.mentoridge.modules.review.controller.request;

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
    private MentorReviewCreateRequest(@NotBlank String content) {
        this.content = content;
    }
/*
    public static MentorReviewCreateRequest of(String content) {
        return MentorReviewCreateRequest.builder()
                .content(content)
                .build();
    }*/
}
