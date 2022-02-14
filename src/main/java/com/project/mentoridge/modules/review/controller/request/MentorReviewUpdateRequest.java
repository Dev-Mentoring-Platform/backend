package com.project.mentoridge.modules.review.controller.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorReviewUpdateRequest {

    @NotBlank
    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private MentorReviewUpdateRequest(@NotBlank String content) {
        this.content = content;
    }

/*    public static MentorReviewUpdateRequest of(String content) {
        return MentorReviewUpdateRequest.builder()
                .content(content)
                .build();
    }*/
}
