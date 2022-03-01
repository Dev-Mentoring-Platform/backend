package com.project.mentoridge.modules.account.controller.request;

import com.project.mentoridge.modules.account.vo.Mentor;
import com.project.mentoridge.modules.account.vo.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class MentorSignUpRequest {

    @ApiModelProperty(value = "소개글", example = "안녕하세요", required = false)
    private String bio;

    @Valid
    @ApiModelProperty(value = "경력", required = false)
    private List<CareerCreateRequest> careers = new ArrayList<>();

    @Valid
    @ApiModelProperty(value = "교육", required = false)
    private List<EducationCreateRequest> educations = new ArrayList<>();

//    @ApiModelProperty(value = "강의주제", example = "spring", required = false)
//    private String subjects;
//
//    @ApiModelProperty(value = "전문성", example="false", required = false)
//    private boolean specialist;

    @Builder(access = AccessLevel.PUBLIC)
    private MentorSignUpRequest(String bio, List<CareerCreateRequest> careers, List<EducationCreateRequest> educations) {
        this.bio = bio;
        if (careers != null) {
            this.careers.addAll(careers);
        }
        if (educations != null) {
            this.educations.addAll(educations);
        }
    }

//    public void addCareerCreateRequest(CareerCreateRequest careerCreateRequest) {
//        this.careers.add(careerCreateRequest);
//    }
//
//    public void addEducationCreateRequest(EducationCreateRequest educationCreateRequest) {
//        this.educations.add(educationCreateRequest);
//    }

    public Mentor toEntity(User user) {
        Mentor mentor = Mentor.builder()
                .user(user)
                .bio(bio)
                .build();
        mentor.addCareers(careers);
        mentor.addEducations(educations);
        return mentor;
    }
}
