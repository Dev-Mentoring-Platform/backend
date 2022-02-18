package com.project.mentoridge.modules.inquiry.vo;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.inquiry.enums.InquiryType;
import lombok.*;

import javax.persistence.*;

@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "inquiry_id"))
@Getter
//@Setter
@Entity
public class Inquiry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_INQUIRY_USER_ID"))
    private User user;

    // TODO - 입력 체크
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InquiryType type;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    // TODO - 이미지

    @Builder(access = AccessLevel.PUBLIC)
    private Inquiry(User user, InquiryType type, String title, String content) {
        this.user = user;
        this.type = type;
        this.title = title;
        this.content = content;
    }

}
