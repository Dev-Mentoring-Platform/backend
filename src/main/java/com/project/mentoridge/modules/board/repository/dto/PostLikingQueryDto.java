package com.project.mentoridge.modules.board.repository.dto;

import lombok.Data;

@Data
public class PostLikingQueryDto {

    private Long postId;
    private long LikingCount;

    public PostLikingQueryDto(Long postId, long likingCount) {
        this.postId = postId;
        this.LikingCount = likingCount;
    }
}
