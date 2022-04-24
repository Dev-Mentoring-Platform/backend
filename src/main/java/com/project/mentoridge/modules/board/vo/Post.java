package com.project.mentoridge.modules.board.vo;


import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AttributeOverride(name = "id", column = @Column(name = "post_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            nullable = false, foreignKey = @ForeignKey(name = "FK_POST_USER_ID"))
    private User user;

    private CategoryType category;

    private String title;

    @Lob
    private String content;

    private String image;

    // 조회 수
    private int hits = 0;

    @Builder(access = AccessLevel.PUBLIC)
    private Post(User user, CategoryType category, String title, String content, String image) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public void update(PostUpdateRequest updateRequest) {
        this.category = updateRequest.getCategory();
        this.title = updateRequest.getTitle();
        this.content = updateRequest.getContent();
        this.image = updateRequest.getImage();
    }

    public void hit() {
        this.hits += 1;
    }
}
