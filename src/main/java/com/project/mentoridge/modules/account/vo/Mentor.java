package com.project.mentoridge.modules.account.vo;

import com.project.mentoridge.modules.account.controller.request.*;
import com.project.mentoridge.modules.account.enums.RoleType;
import com.project.mentoridge.modules.base.BaseEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

//@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AttributeOverride(name = "id", column = @Column(name = "mentor_id"))
@Getter
//@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Mentor extends BaseEntity {

    @Lob
    private String bio;         // 소개글

    // TODO - CHECK : 페치 조인
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @NotNull
    @JoinColumn(name = "user_id",
                referencedColumnName = "user_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "FK_MENTOR_USER_ID"))
    private User user;
//    private String subjects;

    @ToString.Exclude
    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Career> careers = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "mentor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();


//    private boolean specialist;

//    public List<String> getSubjectList() {
//        if (this.subjects.length() > 0) {
//            return Arrays.asList(this.subjects.split(COMMA));
//        }
//        return Collections.emptyList();
//    }

    public void addCareer(Career career) {
        career.setMentor(this);
        this.careers.add(career);
    }

    public void addEducation(Education education) {
        education.setMentor(this);
        this.educations.add(education);
    }

    public void addCareers(List<CareerCreateRequest> careerCreateRequests) {
        careerCreateRequests.forEach(careerCreateRequest -> {
            this.addCareer(careerCreateRequest.toEntity(null));
        });
    }

    public void addEducations(List<EducationCreateRequest> educationCreateRequests) {
        educationCreateRequests.forEach(educationCreateRequest -> {
            this.addEducation(educationCreateRequest.toEntity(null));
        });
    }

    public void updateCareers(List<CareerUpdateRequest> careerUpdateRequests) {
        // this.careers.forEach(Career::delete);
        this.careers.clear();
        careerUpdateRequests.forEach(careerUpdateRequest -> {
            this.addCareer(careerUpdateRequest.toEntity(this));
        });
    }

    public void updateEducations(List<EducationUpdateRequest> educationUpdateRequests) {
        // this.educations.forEach(Education::delete);
        this.educations.clear();
        educationUpdateRequests.forEach(educationUpdateRequest -> {
            this.addEducation(educationUpdateRequest.toEntity(this));
        });
    }

    public void update(MentorUpdateRequest mentorUpdateRequest) {
        this.bio = mentorUpdateRequest.getBio();
        updateCareers(mentorUpdateRequest.getCareers());
        updateEducations(mentorUpdateRequest.getEducations());
    }

    public void quit() {
        this.getCareers().clear();
        this.getEducations().clear();

        user.setRole(RoleType.MENTEE);
    }

    @Builder(access = AccessLevel.PUBLIC)
    private Mentor(User user, String bio, List<Career> careers, List<Education> educations) {
        this.user = user;
        this.bio = bio;
        if (careers != null) {
            careers.forEach(this::addCareer);
        }
        if (educations != null) {
            educations.forEach(this::addEducation);
        }
    }

    public Mentor copy() {
        return Mentor.builder()
                .user(user)
                .bio(bio)
                .careers(careers)
                .educations(educations)
                .build();
    }

}
