package com.project.mentoridge.modules.board.vo;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AttributeOverride(name = "id", column = @Column(name = "like_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Like extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "post_id",
            nullable = false, foreignKey = @ForeignKey(name = "FK_LIKE_POST_ID"))
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id",
            nullable = false, foreignKey = @ForeignKey(name = "FK_LIKE_USER_ID"))
    private User user;

    @Builder(access = AccessLevel.PUBLIC)
    private Like(Post post, User user) {
        this.post = post;
        this.user = user;
    }
}
