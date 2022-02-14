package com.project.mentoridge.modules.account.vo;

import com.project.mentoridge.modules.account.controller.request.CareerUpdateRequest;
import com.project.mentoridge.modules.base.BaseEntity;
import lombok.*;

import javax.persistence.*;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "career_id"))
@Getter
// @Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Career extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id",
            referencedColumnName = "mentor_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_CAREER_MENTOR_ID"))
    private Mentor mentor;

    // 직업, 직장명, 그 외 경력, 자격증
    private String job;
    private String companyName;
    private String others;
    private String license;

    @Builder(access = AccessLevel.PUBLIC)
    private Career(Mentor mentor, String job, String companyName, String others, String license) {
        this.mentor = mentor;
        this.job = job;
        this.companyName = companyName;
        this.others = others;
        this.license = license;
    }

    public void update(CareerUpdateRequest careerUpdateRequest) {
        this.job = careerUpdateRequest.getJob();
        this.companyName = careerUpdateRequest.getCompanyName();
        this.others = careerUpdateRequest.getOthers();
        this.license = careerUpdateRequest.getLicense();
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    public void delete() {
        if (this.mentor != null) {
            this.mentor.getCareers().remove(this);
            this.mentor = null;
        }
    }

}
