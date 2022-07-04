package com.project.mentoridge.modules.notice.vo;

import com.project.mentoridge.modules.base.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;

@AttributeOverride(name = "id", column = @Column(name = "notice_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Notice extends BaseEntity {

    private String title;
    @Lob
    private String content;

    @Builder(access = AccessLevel.PUBLIC)
    private Notice(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
