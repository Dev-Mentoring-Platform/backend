package com.project.mentoridge.modules.account.vo;

import com.project.mentoridge.modules.account.controller.request.MenteeUpdateRequest;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.purchase.vo.Enrollment;
import com.project.mentoridge.modules.purchase.vo.Pick;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.project.mentoridge.utils.CommonUtil.COMMA;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "mentee_id"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
// @Setter
@Entity
public class Mentee extends BaseEntity {

    // TODO - CHECK : 페치 조인
    @NotNull
    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id",
                referencedColumnName = "user_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_MENTEE_USER_ID"))
    private User user;
    private String subjects;      // 학습하고 싶은 과목

    @OneToMany(mappedBy = "mentee", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    @OneToMany(mappedBy = "mentee", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Pick> picks = new ArrayList<>();

    public List<String> getSubjectList() {
        if (this.subjects != null && this.subjects.length() > 0) {
            return Arrays.asList(this.subjects.split(COMMA));
        }
        return Collections.emptyList();
    }

    public void addEnrollment(Enrollment enrollment) {
        this.enrollments.add(enrollment);
        enrollment.setMentee(this);
    }

    public void addPick(Pick pick) {
        this.picks.add(pick);
        pick.setMentee(this);
    }

    public void update(MenteeUpdateRequest menteeUpdateRequest) {
        this.subjects = menteeUpdateRequest.getSubjects();
    }

//    public void quit() {
//        this.user.quit();
//        setUser(null);
//    }

    @Builder(access = AccessLevel.PUBLIC)
    private Mentee(User user, String subjects) {
        this.user = user;
        this.subjects = subjects;
    }

    public Mentee copy() {
        return Mentee.builder()
                .user(user)
                .subjects(subjects)
                .build();
    }

}
