package com.project.mentoridge.modules.board.controller.response;

import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.vo.Post;
import com.project.mentoridge.utils.LocalDateTimeUtil;
import lombok.Data;

@Data
public class PostResponse {

    private Long postId;
    private String userNickname;
    private String userImage;
    private CategoryType category;
    private String title;
    private String content;
    private String createdAt;
    // 조회 수
    private int hits;

    // 좋아요 수
    private Long likingCount = null;
    // 댓글 수
    private Long commentCount = null;

    private Boolean liked = null;

    public PostResponse(Post post) {
        this.postId = post.getId();
        this.userNickname = post.getUser().getNickname();
        this.userImage = post.getUser().getImage();
        this.category = post.getCategory();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = LocalDateTimeUtil.getDateTimeToString(post.getCreatedAt());
        this.hits = post.getHits();
    }
}
