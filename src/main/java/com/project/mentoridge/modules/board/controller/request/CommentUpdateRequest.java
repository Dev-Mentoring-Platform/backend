package com.project.mentoridge.modules.board.controller.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentUpdateRequest {

    @NotBlank
    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private CommentUpdateRequest(String content) {
        this.content = content;
    }
}
