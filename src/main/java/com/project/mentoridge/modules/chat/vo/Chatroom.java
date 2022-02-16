package com.project.mentoridge.modules.chat.vo;

import com.project.mentoridge.modules.account.vo.Mentee;
import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import lombok.*;

import javax.persistence.*;

@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter(AccessLevel.PRIVATE)
@AttributeOverride(name = "id", column = @Column(name = "chatroom_id"))
@Entity
public class Chatroom extends BaseEntity {

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id",
            referencedColumnName = "enrollment_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CHATROOM_ENROLLMENT_ID"))
    private Enrollment enrollment;

    // TODO - CHECK : 페치 조인
    // @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id",
            referencedColumnName = "mentor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CHATROOM_MENTOR_ID"))
    private Mentor mentor;

    // TODO - CHECK : 페치 조인
    // @ManyToOne(fetch = FetchType.EAGER)
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentee_id",
            referencedColumnName = "mentee_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CHATROOM_MENTEE_ID"))
    private Mentee mentee;

    private int accusedCount = 0;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean closed = false;

    @Builder(access = AccessLevel.PUBLIC)
    private Chatroom(Enrollment enrollment, Mentor mentor, Mentee mentee) {
        this.enrollment = enrollment;
        this.mentor = mentor;
        this.mentee = mentee;
    }
/*

    public static Chatroom of(Enrollment enrollment, Mentor mentor, Mentee mentee) {
        return Chatroom.builder()
                .enrollment(enrollment)
                .mentor(mentor)
                .mentee(mentee)
                .build();
    }
*/

    public void close() {
        setClosed(true);
    }

    public void accused() {
        this.accusedCount++;
        if (this.accusedCount == 5) {
            close();
        }
    }

}
