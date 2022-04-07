package com.project.mentoridge.modules.board.controller.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentUpdateRequest {

    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private CommentUpdateRequest(String content) {
        this.content = content;
    }
}
