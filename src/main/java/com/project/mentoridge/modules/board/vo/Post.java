package com.project.mentoridge.modules.board.vo;


import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.board.controller.request.PostUpdateRequest;
import com.project.mentoridge.modules.board.enums.CategoryType;
import com.project.mentoridge.modules.log.component.PostLogService;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    // ์กฐํ ์
    private int hits = 0;

    @ToString.Exclude
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder(access = AccessLevel.PUBLIC)
    private Post(User user, CategoryType category, String title, String content, String image) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    private void update(PostUpdateRequest postUpdateRequest) {
        this.category = postUpdateRequest.getCategory();
        this.title = postUpdateRequest.getTitle();
        this.content = postUpdateRequest.getContent();
        this.image = postUpdateRequest.getImage();
    }

    public void update(PostUpdateRequest postUpdateRequest, User user, PostLogService postLogService) {
        Post before = this.copy();
        update(postUpdateRequest);
        postLogService.update(user, before, this);
    }

    public void delete(User user, PostLogService postLogService) {
        this.comments.clear();
        postLogService.delete(user, this);
    }

    public void hit() {
        this.hits += 1;
    }

    private Post copy() {
        return Post.builder()
                .user(user)
                .category(category)
                .title(title)
                .content(content)
                .image(image)
                .build();
    }
}
