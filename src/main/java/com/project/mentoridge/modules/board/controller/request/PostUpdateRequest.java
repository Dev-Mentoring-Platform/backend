package com.project.mentoridge.modules.board.controller.request;

import com.project.mentoridge.modules.board.enums.CategoryType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUpdateRequest {

    private CategoryType category;
    private String title;
    private String content;
    private String image;

    @Builder(access = AccessLevel.PUBLIC)
    public PostUpdateRequest(CategoryType category, String title, String content, String image) {
        this.category = category;
        this.title = title;
        this.content = content;
        this.image = image;
    }
}
