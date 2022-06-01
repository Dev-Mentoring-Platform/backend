package com.project.mentoridge.modules.board.vo;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.board.controller.request.CommentUpdateRequest;
import com.project.mentoridge.modules.log.component.CommentLogService;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AttributeOverride(name = "id", column = @Column(name = "comment_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id",
            nullable = false, foreignKey = @ForeignKey(name = "FK_COMMENT_POST_ID"))
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            nullable = false, foreignKey = @ForeignKey(name = "FK_COMMENT_USER_ID"))
    private User user;

    @Lob
    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private Comment(Post post, User user, String content) {
        this.post = post;
        this.user = user;
        this.content = content;
    }

    private void update(CommentUpdateRequest commentUpdateRequest) {
        this.content = commentUpdateRequest.getContent();
    }

    public void update(CommentUpdateRequest commentUpdateRequest, User user, CommentLogService commentLogService) {
        Comment before = this.copy();
        update(commentUpdateRequest);
        commentLogService.update(user, before, this);
    }

    public void delete(User user, CommentLogService commentLogService) {
        if (this.post != null) {
            this.post.getComments().remove(this);
            this.post = null;
        }
        commentLogService.delete(user, this);
    }

    private Comment copy() {
        return Comment.builder()
                .post(post)
                .user(user)
                .content(content)
                .build();
    }
}
