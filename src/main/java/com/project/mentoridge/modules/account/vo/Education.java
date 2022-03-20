package com.project.mentoridge.modules.account.vo;

import com.project.mentoridge.modules.account.controller.request.EducationUpdateRequest;
import com.project.mentoridge.modules.account.enums.EducationLevelType;
import com.project.mentoridge.modules.base.BaseEntity;
import lombok.*;

import javax.persistence.*;

@ToString(callSuper = true)
//@EqualsAndHashCode(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "education_id"))
@Getter
//@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Education extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id",
            referencedColumnName = "mentor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_EDUCATION_MENTOR_ID"))
    private Mentor mentor;

    // 최종학력, 학교명, 전공명, 그 외 학력
    @Enumerated(EnumType.STRING)
    private EducationLevelType educationLevel;
    private String schoolName;
    private String major;
    private String others;

    @Builder(access = AccessLevel.PUBLIC)
    private Education(Mentor mentor, EducationLevelType educationLevel, String schoolName, String major, String others) {
        this.mentor = mentor;
        this.educationLevel = educationLevel;
        this.schoolName = schoolName;
        this.major = major;
        this.others = others;
    }

    public void update(EducationUpdateRequest educationUpdateRequest) {
        this.educationLevel = educationUpdateRequest.getEducationLevel();
        this.schoolName = educationUpdateRequest.getSchoolName();
        this.major = educationUpdateRequest.getMajor();
        this.others = educationUpdateRequest.getOthers();
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public void delete() {
        if (this.mentor != null) {
            this.mentor.getEducations().remove(this);
            this.mentor = null;
        }
    }

    public Education copy() {
        return Education.builder()
                .mentor(mentor)
                .educationLevel(educationLevel)
                .schoolName(schoolName)
                .major(major)
                .others(others)
                .build();
    }
}
