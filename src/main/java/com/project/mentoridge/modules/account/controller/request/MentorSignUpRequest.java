package com.project.mentoridge.modules.account.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
public class MentorSignUpRequest {

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
    private MentorSignUpRequest(List<CareerCreateRequest> careers, List<EducationCreateRequest> educations) {
        if (careers != null) {
            this.careers.addAll(careers);
        }
        if (educations != null) {
            this.educations.addAll(educations);
        }
    }

    public static MentorSignUpRequest of(List<CareerCreateRequest> careers, List<EducationCreateRequest> educations) {
        return MentorSignUpRequest.builder()
                .careers(careers)
                .educations(educations)
                .build();
    }

//    public void addCareerCreateRequest(CareerCreateRequest careerCreateRequest) {
//        this.careers.add(careerCreateRequest);
//    }
//
//    public void addEducationCreateRequest(EducationCreateRequest educationCreateRequest) {
//        this.educations.add(educationCreateRequest);
//    }
}
