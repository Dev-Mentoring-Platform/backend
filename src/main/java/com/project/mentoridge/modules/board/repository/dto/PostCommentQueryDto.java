package com.project.mentoridge.modules.board.repository.dto;

import lombok.Data;

@Data
public class PostCommentQueryDto {

    private Long postId;
    private long commentCount;

    public PostCommentQueryDto(Long postId, long commentCount) {
        this.postId = postId;
        this.commentCount = commentCount;
    }
}
