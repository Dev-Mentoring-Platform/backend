package com.project.mentoridge.modules.purchase.vo;

import com.project.mentoridge.modules.base.BaseEntity;
import lombok.*;

import javax.persistence.*;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "cancellation_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter //@Setter
@Entity
public class Cancellation extends BaseEntity {

    // TODO - CHECK : 양방향
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id",
            referencedColumnName = "enrollment_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CANCELLATION_ENROLLMENT_ID"))
    private Enrollment enrollment;

    private String reason;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean approved = false;

    private Cancellation(Enrollment enrollment, String reason) {
        this.enrollment = enrollment;
        this.reason = reason;
    }

    public static Cancellation of(Enrollment enrollment, String reason) {
        return new Cancellation(enrollment, reason);
    }

    public void approve() {
        this.approved = true;
    }
}
