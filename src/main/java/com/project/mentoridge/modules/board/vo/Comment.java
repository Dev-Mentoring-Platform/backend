package com.project.mentoridge.modules.board.vo;

import com.project.mentoridge.modules.base.BaseEntity;
import lombok.AccessLevel;
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
            nullable = false, foreignKey = @ForeignKey(name = "FK_COMMENT_PICK_ID"))
    private Post post;

    @Lob
    private String content;
}
