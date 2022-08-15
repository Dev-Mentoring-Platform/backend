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

    @Enumerated(EnumType.STRING)
    private CategoryType category;

    private String title;

    @Lob
    private String content;

    private String image;

    // 조회 수
    private int hits = 0;

    // TODO - TEST
    /*
    JPA can only remove and cascade the remove over entities it knows about,
    and if you have not been maintaining both sides of this bidirectional relationship,
    issues like this will arise. If the collection of departments is empty,
    try an em.refresh() before the remove, forcing JPA to populate all relationships so that they can be correctly removed,
    though it is better to maintain both sides of the relationship as changes are made to avoid the database hit.
     */
    @ToString.Exclude
    //@OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @ToString.Exclude
    //@OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Liking> likings = new ArrayList<>();

    @Builder(access = AccessLevel.PUBLIC)
    private Post(User user, CategoryType category, String title, String content, String image) {
        this.user = user;
        this.category = category;
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public void addComments(List<Comment> comments) {
        this.comments.addAll(comments);
    }

    public void addLikings(List<Liking> likings) {
        this.likings.addAll(likings);
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
//        this.comments.clear();
//        this.likings.clear();
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
