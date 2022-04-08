package com.project.mentoridge.modules.board.controller.request;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.board.vo.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCreateRequest {

    private CategoryType category;
    private String title;
    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private PostCreateRequest(CategoryType category, String title, String content) {
        this.category = category;
        this.title = title;
        this.content = content;
    }

    public Post toEntity(User user) {
        return Post.builder()
                .user(user)
                .category(category)
                .title(title)
                .content(content)
                .build();
    }
}