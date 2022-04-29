package com.project.mentoridge.modules.board.controller.response;

import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import lombok.Data;

@Data
public class CommentResponse {

    private Long postId;
    private String userNickname;
    private String userImage;
    private String content;
    private String createdAt;

    public CommentResponse(Comment comment) {
        this.postId = comment.getPost().getId();
        this.userNickname = comment.getUser().getNickname();
        this.userImage = comment.getUser().getImage();
        this.content = comment.getContent();
        this.createdAt = LocalDateTimeUtil.getDateTimeToString(comment.getCreatedAt());
    }
}
