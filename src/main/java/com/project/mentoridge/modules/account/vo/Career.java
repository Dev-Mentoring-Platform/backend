package com.project.mentoridge.modules.account.vo;

import com.project.mentoridge.modules.account.controller.request.CareerUpdateRequest;
import com.project.mentoridge.modules.base.BaseEntity;
import com.project.mentoridge.modules.log.component.CareerLogService;
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

    private void update(CareerUpdateRequest careerUpdateRequest) {
        this.job = careerUpdateRequest.getJob();
        this.companyName = careerUpdateRequest.getCompanyName();
        this.others = careerUpdateRequest.getOthers();
        this.license = careerUpdateRequest.getLicense();
    }

    public void update(CareerUpdateRequest careerUpdateRequest, User user, CareerLogService careerLogService) {
        Career before = this.copy();
        update(careerUpdateRequest);
        careerLogService.update(user, before, this);
    }

    public void setMentor(Mentor mentor) {
        this.mentor = mentor;
    }

    private void delete() {
        if (this.mentor != null) {
            this.mentor.getCareers().remove(this);
            this.mentor = null;
        }
    }

    public void delete(User user, CareerLogService careerLogService) {
        delete();
        careerLogService.delete(user, this);
    }

    private Career copy() {
        return Career.builder()
                .mentor(mentor)
                .job(job)
                .companyName(companyName)
                .others(others)
                .license(license)
                .build();
    }

}
