package com.project.mentoridge.modules.notification.vo;

import com.project.mentoridge.modules.account.vo.User;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.notification.enums.NotificationType;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter //@Setter
@AttributeOverride(name = "id", column = @Column(name = "notification_id"))
@Entity
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            referencedColumnName = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_NOTIFICATION_USER_ID"))
    private User user;  // 수신인

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String content;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean checked = false;    // 확인 여부
    private LocalDateTime checkedAt;

    public void check() {
        if (!checked) {
            this.checked = true;
            this.checkedAt = LocalDateTime.now();
        }
    }

    @Builder(access = AccessLevel.PRIVATE)
    private Notification(User user, NotificationType type) {
        this.user = user;
        this.type = type;
        this.content = type.getMessage();
    }

    public static Notification of(User user, NotificationType type) {
        return Notification.builder()
                .user(user)
                .type(type)
                .build();
    }
}
