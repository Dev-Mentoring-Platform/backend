package com.project.mentoridge.modules.board.controller.request;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.vo.Comment;
import com.project.mentoridge.modules.board.vo.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateRequest {

    @NotBlank
    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private CommentCreateRequest(String content) {
        this.content = content;
    }

    public Comment toEntity(User user, Post post) {
        return Comment.builder()
                .user(user)
                .post(post)
                .content(content)
                .build();
    }
}
