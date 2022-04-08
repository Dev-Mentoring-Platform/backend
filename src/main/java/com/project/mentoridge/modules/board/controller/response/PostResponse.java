package com.project.mentoridge.modules.board.controller.response;

import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import lombok.Data;

@Data
public class PostResponse {

    private Long postId;
    private String userNickname;
    private CategoryType category;
    private String title;
    private String content;
    private String createdAt;

    // 좋아요 수
    private int likingCount = 0;
    // 댓글 수
    private int commentCount = 0;

    public PostResponse(Post post) {
        this.postId = post.getId();
        this.userNickname = post.getUser().getNickname();
        this.category = post.getCategory();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = LocalDateTimeUtil.getDateTimeToString(post.getCreatedAt());
    }
}
